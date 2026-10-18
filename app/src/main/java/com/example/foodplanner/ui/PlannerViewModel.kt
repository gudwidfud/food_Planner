package com.example.foodplanner.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodplanner.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class GroceryItem(
    val key: String, val name: String, val quantity: Double?, val unit: String, val checked: Boolean,
    val cost: Double? = null, val price: PriceInfo? = null, val packs: String? = null,
    val category: GroceryCategory = GroceryCategory.Other,
)

val MEAL_TYPES = listOf("Breakfast", "Lunch", "Dinner", "Snack")

class PlannerViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDb.get(app).dao()

    init { LidlPrices.load(app) }

    private val prefs = app.getSharedPreferences("preferences", android.content.Context.MODE_PRIVATE)
    val diets = MutableStateFlow(
        prefs.getStringSet("diets", emptySet()).orEmpty().mapNotNull { n -> Diet.entries.find { it.name == n } }.toSet(),
    )

    fun setDiet(diet: Diet, on: Boolean) {
        val next = if (on) diets.value + diet else diets.value - diet
        diets.value = next
        prefs.edit().putStringSet("diets", next.map { it.name }.toSet()).apply()
    }

    val recipes = dao.recipes().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val ingredients = dao.ingredients().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val meals = dao.meals().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val weekOffset = MutableStateFlow(0)
    val weekStart: StateFlow<LocalDate> = weekOffset
        .map { LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(it.toLong()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))

    /** Everything is scaled to one serving, whatever the recipe's own yield is. */
    private val scaledIngredients = combine(recipes, ingredients) { recs, ings ->
        val servings = recs.associate { it.id to it.servings.coerceAtLeast(1) }
        ings.map { it.copy(quantity = it.quantity?.div(servings[it.recipeId] ?: 1)) }
    }

    val grocery: StateFlow<List<GroceryItem>> =
        combine(weekStart, meals, scaledIngredients, dao.checked(), dao.prices()) { start, meals, ings, checked, prices ->
            val overrides = prices.associateBy { it.name }
            val dates = (0..6).map { start.plusDays(it.toLong()).toString() }.toSet()
            val byRecipe = ings.groupBy { it.recipeId }
            val totals = LinkedHashMap<String, GroceryItem>()
            meals.filter { it.date in dates }.forEach { meal ->
                byRecipe[meal.recipeId].orEmpty().forEach { raw ->
                    val ing = raw.copy(name = GroceryNames.canonical(raw.name), unit = GroceryNames.unit(raw.unit))
                    val key = "$start|${ing.name.lowercase().trim()}|${ing.unit}"
                    val prev = totals[key]
                    val qty = if (prev == null) ing.quantity
                    else if (prev.quantity != null && ing.quantity != null) prev.quantity + ing.quantity
                    else prev.quantity ?: ing.quantity
                    totals[key] = GroceryItem(key, prev?.name ?: ing.name, qty, ing.unit, key in checked)
                }
            }
            // Buy whole packs per ingredient, summed across units and meals
            val itemsWithInfo = totals.values.map { it to LidlPrices.lookup(it.name, overrides) }
            val amountByName = HashMap<String, Double>()
            itemsWithInfo.forEach { (it, info) ->
                if (info != null) {
                    val n = it.name.lowercase().trim()
                    amountByName[n] = (amountByName[n] ?: 0.0) + LidlPrices.amount(info, it.quantity, it.unit)
                }
            }
            val seen = HashSet<String>()
            itemsWithInfo.map { (it, info) ->
                val n = it.name.lowercase().trim()
                if (info == null) it
                else if (seen.add(n)) {
                    val packs = LidlPrices.packsNeeded(info, amountByName.getValue(n))
                    it.copy(price = info, cost = LidlPrices.packCost(info, amountByName.getValue(n)),
                        packs = LidlPrices.packLabel(info, packs))
                } else it.copy(price = info, cost = 0.0)
            }.map { it.copy(category = GroceryCategories.of(it.name)) }
                .sortedWith(compareBy({ it.category.ordinal }, { it.checked }, { it.name.lowercase() }))
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val autoBusy = MutableStateFlow(false)
    val autoStatus = MutableStateFlow<String?>(null)

    /**
     * Fills empty Dinner slots of the shown week, one serving each, greedily picking the recipe
     * that adds the least to the week's shelf cost (whole packs), so ingredients get reused.
     * With [refresh], auto-filled dinners are replaced by different recipes; manual ones are kept.
     */
    fun autoFill(refresh: Boolean = false) = viewModelScope.launch {
        if (autoBusy.value) return@launch
        autoBusy.value = true
        autoStatus.value = null
        try {
            // Import only complete dinners; OpenStove is bundled, TheMealDB needs a connection.
            suspend fun import(list: List<ImportedRecipe>, limit: Int) {
                val have = dao.recipes().first().map { it.name.lowercase() }.toMutableSet()
                var added = 0
                for (r in list.shuffled()) {
                    if (added >= limit) break
                    if (r.name.lowercase() in have) continue
                    val items = r.ingredients.mapNotNull { parseIngredient(it) }
                    if (!Nutrition.isFullMeal(r.name, r.servings, items)) continue
                    dao.saveRecipe(Recipe(0, r.name, r.servings, r.sourceUrl, r.instructions, r.cuisine), items)
                    have += r.name.lowercase(); added++
                }
            }
            val dates = (0..6).map { weekStart.value.plusDays(it.toLong()) }
            var existing = dao.meals().first().filter { m -> dates.any { it.toString() == m.date } }
            // Refresh must never bring back a recipe it is replacing
            val avoid = if (refresh) existing.filter { it.auto && it.mealType == "Dinner" }.map { it.recipeId }.toSet() else emptySet()

            suspend fun loadCandidates(): List<Recipe> {
                val ings = dao.ingredients().first().groupBy { it.recipeId }
                val diets = diets.value
                return dao.recipes().first().filter {
                    it.id !in avoid && ings[it.id].orEmpty().isNotEmpty() &&
                        Nutrition.isFullMeal(it.name, it.servings, ings.getValue(it.id)) &&
                        Diet.allowed(diets, listOf(it.name) + ings.getValue(it.id).map { i -> i.name })
                }
            }

            import(OpenStove.load(getApplication()), 100)
            var fresh = loadCandidates()
            if (fresh.size < 150 || (refresh && fresh.size < 10)) {
                runCatching { MealDb.browseAll() }.onSuccess { all ->
                    import(all.filter { Nutrition.categoryAllowed(it.category) }, 500)
                }
                fresh = loadCandidates()
            }
            if (fresh.isEmpty()) {
                autoStatus.value = if (refresh) "No other suitable recipes found, so the plan was left unchanged. Add more recipes or check your connection."
                else "No suitable recipes available. Add some or check your connection."
                return@launch
            }
            val recs = dao.recipes().first().associateBy { it.id }
            val ings = dao.ingredients().first().groupBy { it.recipeId }
            val overrides = dao.prices().first().associateBy { it.name }
            val protein = fresh.associate { it.id to Nutrition.proteinPerServing(it.servings, ings.getValue(it.id)) }
            val strong = fresh.filter { (protein[it.id] ?: 0.0) >= Nutrition.MIN_PROTEIN_G }
            // Fall back to the highest-protein half if too few recipes clear the bar
            val usable = if (strong.size >= 5) strong else fresh.sortedByDescending { protein[it.id] }.take(maxOf(5, fresh.size / 2))

            fun weekCost(ids: List<Long>): Double {
                val amounts = HashMap<String, Double>()
                val infos = HashMap<String, PriceInfo>()
                for (id in ids) {
                    val servings = recs[id]?.servings?.coerceAtLeast(1) ?: 1
                    for (ing in ings[id].orEmpty()) {
                        val info = LidlPrices.lookup(ing.name, overrides) ?: continue
                        val key = ing.name.lowercase().trim()
                        amounts[key] = (amounts[key] ?: 0.0) + LidlPrices.amount(info, ing.quantity?.div(servings), ing.unit)
                        infos[key] = info
                    }
                }
                return amounts.entries.sumOf { LidlPrices.packCost(infos.getValue(it.key), it.value) }
            }

            if (refresh) {
                dao.deleteAutoDinners(dates.map { it.toString() })
                existing = existing.filterNot { it.auto && it.mealType == "Dinner" }
            }
            val noise = if (refresh) 1.5 else 0.3
            val chosen = existing.map { it.recipeId }.toMutableList()
            val slots = dates.map { it to "Dinner" }
                .filter { (d, t) -> existing.none { it.date == d.toString() && it.mealType == t } }

            // Variety: no recipe or dish type (salad, soup, curry...) from the surrounding 4 weeks, when enough alternatives exist
            val allDinners = dao.meals().first().filter { it.mealType == "Dinner" }
            val dinnerByDate = allDinners.associate { it.date to it.recipeId }.toMutableMap()
            val windowFrom = weekStart.value.minusWeeks(4).toString()
            val windowTo = weekStart.value.plusWeeks(5).toString()
            val weekSet = dates.map { it.toString() }.toSet()
            val nearby = allDinners.filter { it.date >= windowFrom && it.date < windowTo && it.date !in weekSet }.map { it.recipeId }
            val recent = nearby.toSet()
            fun typeOf(id: Long) = Nutrition.dishType(recs[id]?.name.orEmpty())
            val recentTypes = nearby.mapNotNull { typeOf(it) }.toSet()
            val fresher = usable.filter { it.id !in recent }
            val freshest = fresher.filter { Nutrition.dishType(it.name) !in recentTypes }
            val varied = when {
                freshest.size >= 7 -> freshest
                fresher.size >= 7 -> fresher
                else -> usable
            }
            val proteinOf = HashMap<Long, String>()
            fun mainProtein(id: Long) = proteinOf.getOrPut(id) {
                Nutrition.mainProtein(recs[id]?.servings ?: 1, ings[id].orEmpty())
            }
            val maxPerWeek = 2

            for ((date, type) in slots) {
                val prev = dinnerByDate[date.minusDays(1).toString()]
                val next = dinnerByDate[date.plusDays(1).toString()]
                val weekDinners = dates.mapNotNull { dinnerByDate[it.toString()] }
                var options = varied.filter { p ->
                    p.id != prev && p.id != next && weekDinners.count { it == p.id } < maxPerWeek
                }
                if (options.isEmpty()) options = varied.filter { it.id != prev && it.id != next }
                if (options.isEmpty()) options = varied
                val best = options.minByOrNull { p ->
                    val repeats = weekDinners.count { it == p.id }
                    val sameProtein = weekDinners.count { mainProtein(it) == mainProtein(p.id) }
                    val nearProtein = listOfNotNull(prev, next).count { mainProtein(it) == mainProtein(p.id) }
                    val sameType = Nutrition.dishType(p.name)?.let { t -> weekDinners.count { typeOf(it) == t } } ?: 0
                    weekCost(chosen + p.id) + repeats * 6.0 + sameProtein * 1.5 + nearProtein * 3.0 + sameType * 5.0 +
                        (if (p.id in recent) 4.0 else 0.0) + kotlin.random.Random.nextDouble() * noise
                } ?: break
                chosen += best.id
                dinnerByDate[date.toString()] = best.id
                dao.insertMeal(PlannedMeal(date = date.toString(), mealType = type, recipeId = best.id, auto = true))
            }
            autoStatus.value = if (slots.isEmpty()) "Week is already full" else "Filled ${slots.size} meals, est. ${LidlPrices.CURRENCY}${"%.2f".format(java.util.Locale.US, weekCost(chosen))} for the week"
        } catch (e: Exception) {
            autoStatus.value = e.message ?: "Auto-fill failed"
        } finally {
            autoBusy.value = false
        }
    }

    fun setPrice(name: String, price: Double, basis: String) = viewModelScope.launch {
        dao.setPrice(PriceItem(name.lowercase().trim(), price, basis))
    }

    fun resetPrice(name: String) = viewModelScope.launch { dao.clearPrice(name.lowercase().trim()) }

    fun shiftWeek(delta: Int) { weekOffset.value += delta }

    fun addMeal(date: LocalDate, type: String, recipeId: Long) = viewModelScope.launch {
        dao.insertMeal(PlannedMeal(date = date.toString(), mealType = type, recipeId = recipeId))
    }

    fun removeMeal(id: Long) = viewModelScope.launch { dao.deleteMeal(id) }

    /** Replaces one planned meal with a random different recipe that fits the preferences and avoids nearby repeats. */
    fun swapMeal(meal: PlannedMeal) = viewModelScope.launch {
        val ings = dao.ingredients().first().groupBy { it.recipeId }
        val all = dao.meals().first()
        val date = LocalDate.parse(meal.date)
        val weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val week = (0..6).map { weekStart.plusDays(it.toLong()).toString() }.toSet()
        val adjacent = setOf(date.minusDays(1).toString(), date.plusDays(1).toString())
        val inWeek = all.filter { it.date in week && it.id != meal.id }.map { it.recipeId }.toSet()
        val nearby = all.filter { it.date in adjacent }.map { it.recipeId }.toSet()
        val diets = diets.value
        val pool = dao.recipes().first().filter {
            val list = ings[it.id].orEmpty()
            it.id != meal.recipeId && list.isNotEmpty() &&
                Nutrition.isFullMeal(it.name, it.servings, list) &&
                Diet.allowed(diets, listOf(it.name) + list.map { i -> i.name })
        }
        val pick = (pool.filter { it.id !in inWeek && it.id !in nearby }.ifEmpty { pool.filter { it.id !in nearby } }.ifEmpty { pool })
            .randomOrNull()
        if (pick == null) {
            autoStatus.value = "No other suitable recipes to swap in. Run Auto-fill to import more."
            return@launch
        }
        dao.deleteMeal(meal.id)
        dao.insertMeal(meal.copy(id = 0, recipeId = pick.id, auto = true))
        autoStatus.value = "Swapped for ${pick.name}"
    }

    fun saveRecipe(id: Long, name: String, servings: Int, url: String?, ingredientLines: String, instructions: String? = null) =
        viewModelScope.launch {
            val items = ingredientLines.lines().mapNotNull { parseIngredient(it) }
            dao.saveRecipe(Recipe(id, name.trim(), servings, url, instructions?.trim()?.takeIf { it.isNotEmpty() }), items)
        }

    fun deleteRecipe(id: Long) = viewModelScope.launch { dao.deleteRecipe(id) }

    fun toggle(item: GroceryItem) = viewModelScope.launch {
        if (item.checked) dao.uncheck(item.key) else dao.check(GroceryCheck(item.key))
    }
}
