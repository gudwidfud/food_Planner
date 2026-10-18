package com.example.foodplanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** User-edited price override. name is lowercase; basis is "kg", "l" or "each". */
@Entity
data class PriceItem(@PrimaryKey val name: String, val price: Double, val basis: String)

data class PriceInfo(val price: Double, val basis: String, val pieceG: Double = 100.0, val pack: Double = 0.0)

private class Def(val keywords: List<String>, val price: Double, val basis: String, val pieceG: Double = 100.0)

/**
 * Approximate shelf prices in EUR. Lidl has no public price API and Heisse Preise has no Lidl data,
 * so ingredients start from built-in estimates, adjusted with Hofer prices from the Heisse Preise
 * snapshot (tools/build-prices.js) where those look plausible. Users can override per ingredient.
 */
object LidlPrices {
    const val CURRENCY = "€"

    private val defs = listOf(
        Def(listOf("chicken breast", "chicken fillet"), 7.99, "kg", 200.0),
        Def(listOf("chicken thigh", "chicken leg", "drumstick"), 4.99, "kg", 150.0),
        Def(listOf("chicken"), 5.99, "kg", 200.0),
        Def(listOf("minced beef", "ground beef", "beef mince"), 9.99, "kg"),
        Def(listOf("beef", "steak"), 13.99, "kg", 200.0),
        Def(listOf("minced pork", "pork mince", "ground pork"), 6.99, "kg"),
        Def(listOf("pork", "ham"), 7.99, "kg", 150.0),
        Def(listOf("bacon", "pancetta"), 12.99, "kg", 20.0),
        Def(listOf("sausage", "chorizo"), 7.99, "kg", 80.0),
        Def(listOf("lamb"), 14.99, "kg", 200.0),
        Def(listOf("turkey"), 8.99, "kg", 200.0),
        Def(listOf("salmon"), 17.99, "kg", 150.0),
        Def(listOf("tuna"), 1.19, "each", 150.0),
        Def(listOf("prawn", "shrimp"), 15.99, "kg", 10.0),
        Def(listOf("cod", "haddock", "white fish", "fish"), 12.99, "kg", 150.0),
        Def(listOf("egg"), 0.35, "each"),
        Def(listOf("milk"), 1.15, "l"),
        Def(listOf("double cream", "heavy cream", "cream"), 6.50, "l"),
        Def(listOf("yogurt", "yoghurt"), 2.20, "kg"),
        Def(listOf("butter"), 9.00, "kg"),
        Def(listOf("parmesan", "pecorino"), 17.99, "kg"),
        Def(listOf("mozzarella"), 7.50, "kg", 125.0),
        Def(listOf("feta"), 8.50, "kg", 200.0),
        Def(listOf("cheddar", "cheese"), 8.99, "kg"),
        Def(listOf("flour"), 0.99, "kg"),
        Def(listOf("sugar"), 1.20, "kg"),
        Def(listOf("brown sugar", "icing sugar"), 1.80, "kg"),
        Def(listOf("honey"), 6.50, "kg"),
        Def(listOf("rice"), 1.89, "kg"),
        Def(listOf("spaghetti", "pasta", "penne", "fusilli", "noodle", "macaroni", "lasagne"), 1.29, "kg"),
        Def(listOf("bread crumb", "breadcrumb"), 3.50, "kg"),
        Def(listOf("bread", "baguette", "bun", "roll", "tortilla", "wrap", "pitta"), 3.20, "kg", 60.0),
        Def(listOf("oats", "oat"), 1.50, "kg"),
        Def(listOf("potato"), 1.20, "kg", 150.0),
        Def(listOf("sweet potato"), 2.20, "kg", 250.0),
        Def(listOf("onion"), 1.20, "kg", 150.0),
        Def(listOf("spring onion", "scallion"), 0.79, "each", 15.0),
        Def(listOf("garlic"), 6.00, "kg", 5.0),
        Def(listOf("ginger"), 6.00, "kg", 20.0),
        Def(listOf("carrot"), 1.10, "kg", 80.0),
        Def(listOf("tomato paste", "tomato puree", "tomato purée"), 4.00, "kg", 70.0),
        Def(listOf("chopped tomato", "tinned tomato", "canned tomato", "passata"), 1.60, "kg", 400.0),
        Def(listOf("cherry tomato"), 3.50, "kg", 15.0),
        Def(listOf("tomato"), 2.50, "kg", 120.0),
        Def(listOf("pepper", "capsicum"), 3.20, "kg", 150.0),
        Def(listOf("black pepper", "white pepper"), 20.00, "kg"),
        Def(listOf("chilli", "chili"), 8.00, "kg", 10.0),
        Def(listOf("mushroom"), 4.50, "kg", 20.0),
        Def(listOf("courgette", "zucchini"), 2.20, "kg", 250.0),
        Def(listOf("aubergine", "eggplant"), 2.80, "kg", 300.0),
        Def(listOf("broccoli"), 2.50, "kg", 300.0),
        Def(listOf("cauliflower"), 2.00, "kg", 600.0),
        Def(listOf("cabbage"), 1.20, "kg", 800.0),
        Def(listOf("spinach"), 5.00, "kg"),
        Def(listOf("lettuce", "salad", "rocket"), 1.00, "each", 200.0),
        Def(listOf("cucumber"), 0.79, "each", 350.0),
        Def(listOf("leek"), 2.00, "kg", 200.0),
        Def(listOf("celery"), 2.00, "kg", 60.0),
        Def(listOf("pea"), 2.20, "kg"),
        Def(listOf("corn", "sweetcorn"), 2.50, "kg"),
        Def(listOf("green bean"), 3.50, "kg"),
        Def(listOf("chickpea", "kidney bean", "black bean", "bean", "lentil"), 1.80, "kg", 240.0),
        Def(listOf("coconut milk"), 3.50, "l", 400.0),
        Def(listOf("stock", "broth", "bouillon"), 0.25, "each", 10.0),
        Def(listOf("olive oil"), 6.50, "l"),
        Def(listOf("vegetable oil", "sunflower oil", "oil"), 2.20, "l"),
        Def(listOf("vinegar"), 1.50, "l"),
        Def(listOf("soy sauce"), 4.00, "l"),
        Def(listOf("ketchup"), 3.00, "l"),
        Def(listOf("mayonnaise", "mayo"), 4.00, "l"),
        Def(listOf("mustard"), 5.00, "kg"),
        Def(listOf("salt"), 0.60, "kg"),
        Def(listOf("cumin", "paprika", "cinnamon", "oregano", "thyme", "basil", "parsley", "coriander",
            "rosemary", "turmeric", "curry", "chilli powder", "spice", "herb", "bay leaf", "nutmeg"), 25.00, "kg", 10.0),
        Def(listOf("lemon", "lime"), 0.35, "each", 80.0),
        Def(listOf("orange"), 0.40, "each", 200.0),
        Def(listOf("apple"), 2.20, "kg", 180.0),
        Def(listOf("banana"), 1.30, "kg", 120.0),
        Def(listOf("avocado"), 0.89, "each", 170.0),
        Def(listOf("berry", "strawberr", "blueberr", "raspberr"), 8.00, "kg"),
        Def(listOf("raisin", "sultana", "dried fruit"), 7.00, "kg"),
        Def(listOf("almond", "walnut", "cashew", "peanut", "nut"), 12.00, "kg"),
        Def(listOf("chocolate", "cocoa"), 10.00, "kg"),
        Def(listOf("baking powder", "baking soda", "yeast", "bicarbonate"), 10.00, "kg", 10.0),
        Def(listOf("wine"), 3.50, "l"),
        Def(listOf("water"), 0.20, "l"),
    )

    /**
     * Smallest pack sold in the shop, keyed by a Def's first keyword: grams/ml for kg and l items,
     * pieces for "each" items. Anything missing falls back to 500 g/ml (50 g for spices) or 1 piece.
     */
    private val PACKS = mapOf(
        "chicken breast" to 500.0, "chicken thigh" to 800.0, "chicken" to 800.0,
        "minced beef" to 500.0, "beef" to 400.0, "minced pork" to 500.0, "pork" to 500.0,
        "bacon" to 150.0, "sausage" to 300.0, "lamb" to 400.0, "turkey" to 500.0,
        "salmon" to 250.0, "tuna" to 1.0, "prawn" to 250.0, "cod" to 400.0,
        "egg" to 6.0, "milk" to 1000.0, "double cream" to 200.0, "yogurt" to 500.0, "butter" to 250.0,
        "parmesan" to 100.0, "mozzarella" to 125.0, "feta" to 200.0, "cheddar" to 200.0,
        "flour" to 1000.0, "sugar" to 1000.0, "brown sugar" to 500.0, "honey" to 250.0,
        "rice" to 1000.0, "spaghetti" to 500.0, "bread crumb" to 200.0, "bread" to 500.0, "oats" to 500.0,
        "potato" to 2000.0, "sweet potato" to 1000.0, "onion" to 1000.0, "spring onion" to 1.0,
        "garlic" to 60.0, "ginger" to 100.0, "carrot" to 1000.0,
        "tomato paste" to 200.0, "chopped tomato" to 400.0, "cherry tomato" to 250.0, "tomato" to 500.0,
        "pepper" to 500.0, "mushroom" to 250.0, "courgette" to 500.0, "aubergine" to 300.0,
        "broccoli" to 500.0, "cauliflower" to 600.0, "cabbage" to 1000.0, "spinach" to 250.0,
        "lettuce" to 1.0, "cucumber" to 1.0, "leek" to 500.0, "celery" to 500.0,
        "pea" to 450.0, "corn" to 400.0, "green bean" to 400.0, "chickpea" to 400.0,
        "coconut milk" to 400.0, "stock" to 12.0,
        "olive oil" to 500.0, "vegetable oil" to 1000.0, "vinegar" to 500.0, "soy sauce" to 150.0,
        "ketchup" to 500.0, "mayonnaise" to 250.0, "mustard" to 200.0, "salt" to 500.0,
        "cumin" to 40.0,
        "lemon" to 2.0, "orange" to 1.0, "apple" to 1000.0, "banana" to 500.0, "avocado" to 2.0,
        "berry" to 250.0, "raisin" to 250.0, "almond" to 200.0, "chocolate" to 100.0,
        "baking powder" to 50.0, "wine" to 750.0, "water" to 1000.0,
    )

    /** Prices from the bundled Heisse Preise (Hofer) snapshot, keyed by a Def's first keyword. */
    @Volatile private var external: Map<String, PriceInfo> = emptyMap()

    fun load(context: android.content.Context) {
        external = runCatching {
            val json = org.json.JSONObject(context.assets.open("hofer_prices.json").bufferedReader().use { it.readText() })
            json.keys().asSequence().associateWith { k ->
                val o = json.getJSONObject(k)
                PriceInfo(o.getDouble("price"), o.getString("basis"))
            }
        }.getOrDefault(emptyMap())
    }

    // Snapshot prices are only trusted when they are plausible next to the built-in estimate;
    // keyword matching on product names is noisy.
    private fun merged(d: Def): PriceInfo {
        val own = PriceInfo(d.price, d.basis, d.pieceG, PACKS[d.keywords.first()] ?: 0.0)
        val ext = external[d.keywords.first()] ?: return own
        val sameBasis = ext.basis == d.basis || (ext.basis in listOf("kg", "l") && d.basis in listOf("kg", "l"))
        val ratio = ext.price / d.price
        return if (sameBasis && ratio in 0.5..2.0) own.copy(price = ext.price) else own
    }

    fun default(name: String): PriceInfo? {
        val n = name.lowercase()
        var best: Pair<Int, Def>? = null
        for (d in defs) for (k in d.keywords) {
            if (n.contains(k) && (best == null || k.length > best.first)) best = k.length to d
        }
        return best?.second?.let { merged(it) }
    }

    fun lookup(name: String, overrides: Map<String, PriceItem>): PriceInfo? {
        val key = name.lowercase().trim()
        val base = default(key)
        overrides[key]?.let {
            val pack = if (base != null && base.basis == it.basis) base.pack else 0.0
            return PriceInfo(it.price, it.basis, base?.pieceG ?: 100.0, pack)
        }
        return base
    }

    private val MASS_G = mapOf("g" to 1.0, "gram" to 1.0, "grams" to 1.0, "kg" to 1000.0,
        "oz" to 28.35, "ounce" to 28.35, "ounces" to 28.35, "lb" to 453.6, "lbs" to 453.6,
        "pound" to 453.6, "pounds" to 453.6)
    private val VOLUME_ML = mapOf("ml" to 1.0, "l" to 1000.0, "liter" to 1000.0, "litre" to 1000.0,
        "tsp" to 5.0, "teaspoon" to 5.0, "teaspoons" to 5.0, "tbsp" to 15.0, "tablespoon" to 15.0,
        "tablespoons" to 15.0, "cup" to 240.0, "cups" to 240.0)
    private val COUNT_G = mapOf("" to null, "clove" to 5.0, "cloves" to 5.0, "slice" to 20.0,
        "slices" to 20.0, "pinch" to 0.5, "can" to 400.0, "cans" to 400.0)

    /** Estimated cost of [quantity] [unit] of an ingredient, or null when no price is known. */
    fun estimate(info: PriceInfo?, quantity: Double?, unit: String): Double? {
        if (info == null) return null
        return packCost(info, amount(info, quantity, unit))
    }

    /** Amount in grams/ml (kg and l basis) or pieces (each basis). */
    fun amount(info: PriceInfo, quantity: Double?, unit: String): Double {
        val q = quantity ?: 1.0
        val u = unit.lowercase()
        val grams: Double? = MASS_G[u]?.let { it * q } ?: VOLUME_ML[u]?.let { it * q }
        if (info.basis == "each") return if (grams == null) q else (grams / info.pieceG).coerceAtLeast(0.1)
        return grams ?: (q * (COUNT_G[u] ?: info.pieceG))
    }

    /** Grams (or ml) of product for an ingredient line. */
    fun grams(info: PriceInfo, quantity: Double?, unit: String): Double {
        val a = amount(info, quantity, unit)
        return if (info.basis == "each") a * info.pieceG else a
    }

    /** Smallest purchasable pack in g/ml (kg and l basis) or pieces (each basis). */
    fun packSize(info: PriceInfo): Double = when {
        info.pack > 0 -> info.pack
        info.basis == "each" -> if (info.price < 0.5) 6.0 else 1.0
        info.price >= 20 -> 50.0
        else -> 500.0
    }

    /** Number of whole packs needed for [amount]. */
    fun packsNeeded(info: PriceInfo, amount: Double): Int =
        if (amount <= 0) 0 else kotlin.math.ceil(amount / packSize(info) - 1e-9).toInt()

    /** What you actually pay at the shelf: whole packs only, so reusing an ingredient across meals is cheap. */
    fun packCost(info: PriceInfo, amount: Double): Double {
        val packs = packsNeeded(info, amount)
        val perPack = if (info.basis == "each") info.price * packSize(info) else info.price * packSize(info) / 1000.0
        return perPack * packs
    }

    fun packLabel(info: PriceInfo, packs: Int): String {
        val size = packSize(info)
        val unit = when (info.basis) { "each" -> "pcs"; "l" -> "ml"; else -> "g" }
        val s = if (size == kotlin.math.floor(size)) size.toLong().toString() else size.toString()
        return "$packs × $s $unit"
    }
}
