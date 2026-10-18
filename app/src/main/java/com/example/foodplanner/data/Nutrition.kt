package com.example.foodplanner.data

/** Rough protein and dessert heuristics used by auto-fill. */
object Nutrition {
    const val MIN_PROTEIN_G = 25.0

    // grams of protein per 100 g, and optionally the weight of one piece
    private class P(val per100: Double, val pieceG: Double? = null)

    private val protein = mapOf(
        "chicken" to P(23.0), "turkey" to P(24.0), "beef" to P(20.0), "steak" to P(24.0), "pork" to P(20.0),
        "lamb" to P(19.0), "bacon" to P(15.0), "ham" to P(18.0), "sausage" to P(13.0), "chorizo" to P(24.0),
        "salmon" to P(20.0), "tuna" to P(24.0), "cod" to P(18.0), "haddock" to P(18.0), "fish" to P(18.0),
        "prawn" to P(20.0), "shrimp" to P(20.0), "mackerel" to P(19.0), "anchov" to P(25.0),
        "egg" to P(12.5, 50.0), "tofu" to P(12.0), "tempeh" to P(19.0),
        "lentil" to P(9.0), "chickpea" to P(8.0), "kidney bean" to P(8.0), "black bean" to P(8.0), "bean" to P(7.0),
        "parmesan" to P(35.0), "mozzarella" to P(22.0), "feta" to P(14.0), "cheddar" to P(25.0),
        "cheese" to P(22.0), "halloumi" to P(22.0), "yogurt" to P(4.0), "yoghurt" to P(4.0),
        "peanut" to P(25.0), "almond" to P(21.0), "cashew" to P(18.0),
    )

    private val notFood = Regex("stock|broth|bouillon|cube|powder|sauce|paste|seasoning|spice")
    private val dessertName = Regex(
        "cake|cookie|brownie|pudding|cheesecake|ice cream|muffin|fudge|crumble|trifle|mousse|biscuit|" +
            "donut|doughnut|flapjack|sundae|cupcake|truffle|pavlova|tiramisu|macaron|eclair|sorbet|custard|" +
            "baklava|scone|shortbread|meringue|syrup tart|treacle|sponge",
        RegexOption.IGNORE_CASE,
    )
    private val sweetIngredient = Regex("cocoa|chocolate|icing sugar|caster sugar|condensed milk|custard|vanilla extract|marshmallow")

    private val BAD_CATEGORIES = setOf("dessert", "breakfast", "starter", "side", "miscellaneous")

    fun categoryAllowed(category: String?) = category == null || category.lowercase() !in BAD_CATEGORIES

    fun isDessert(name: String, ingredients: List<Ingredient>): Boolean =
        dessertName.containsMatchIn(name) || ingredients.any { sweetIngredient.containsMatchIn(it.name.lowercase()) }

    private val sideOrPartName = Regex(
        "\\b(sauce|dip|dressing|marinade|jam|smoothie|drink|spread|bread|loaf|bun|rolls?|dough|pickle|" +
            "relish|chutney|stock|gravy|salsa|slaw|garnish|toast|bruschetta|damper)\\b",
        RegexOption.IGNORE_CASE,
    )
    private val carbs = Regex(
        "rice|pasta|spaghetti|penne|fusilli|noodle|potato|bread|tortilla|wrap|pitta|bun\\b|couscous|quinoa|" +
            "buckwheat|barley|bulgur|oats|polenta|gnocchi|macaroni|lasagn|dumpling|flour|pizza|yam|tagliatelle|bean|lentil|chickpea",
    )
    private val veg = Regex(
        "onion|carrot|pepper|tomato|spinach|broccoli|mushroom|courgette|zucchini|aubergine|eggplant|cabbage|" +
            "pea\\b|peas\\b|green bean|leek|celery|kale|pumpkin|squash|asparagus|cauliflower|sprout|corn|lettuce|cucumber|pak choi|chanterelle",
    )

    /**
     * A complete dinner: not a side/sauce/dessert, has a real protein source plus a carb or vegetables,
     * and is substantial enough (several ingredients) to be a meal rather than a single item.
     */
    fun isFullMeal(name: String, servings: Int, ingredients: List<Ingredient>): Boolean {
        if (isDessert(name, ingredients) || sideOrPartName.containsMatchIn(name)) return false
        val names = ingredients.map { it.name.lowercase() }
        if (names.count { !notFood.containsMatchIn(it) && !it.startsWith("salt") && !it.contains("water") } < 5) return false
        if (proteinPerServing(servings, ingredients) < 15.0) return false
        val hasCarb = names.any { carbs.containsMatchIn(it) }
        val vegCount = names.count { veg.containsMatchIn(it) }
        return hasCarb || vegCount >= 2
    }

    private val groups = mapOf(
        "chicken" to "chicken", "turkey" to "poultry", "duck" to "poultry", "beef" to "beef", "steak" to "beef",
        "pork" to "pork", "bacon" to "pork", "ham" to "pork", "sausage" to "pork", "chorizo" to "pork",
        "lamb" to "lamb", "salmon" to "fish", "tuna" to "fish", "cod" to "fish", "haddock" to "fish",
        "fish" to "fish", "mackerel" to "fish", "anchov" to "fish", "prawn" to "seafood", "shrimp" to "seafood",
        "mussel" to "seafood", "egg" to "egg", "tofu" to "vegetarian", "tempeh" to "vegetarian",
        "lentil" to "legume", "chickpea" to "legume", "kidney bean" to "legume", "black bean" to "legume", "bean" to "legume",
        "parmesan" to "cheese", "mozzarella" to "cheese", "feta" to "cheese", "cheddar" to "cheese",
        "cheese" to "cheese", "halloumi" to "cheese",
    )

    private val dishTypes = listOf(
        "salad" to Regex("\\bsalad|slaw\\b"),
        "soup" to Regex("\\b(soup|chowder|broth|ramen|pho)\\b"),
        "curry" to Regex("\\b(curry|masala|korma|tikka|dal|dhal)\\b"),
        "pasta" to Regex("\\b(pasta|spaghetti|penne|lasagn|macaroni|fettuccine|linguine|noodle|carbonara|gnocchi)"),
        "stir-fry" to Regex("\\b(stir[- ]?fry|fried rice|chow mein|lo mein|pad thai)\\b"),
        "stew" to Regex("\\b(stew|casserole|chili|chilli|goulash|hotpot|pot pie)\\b"),
        "tacos" to Regex("\\b(taco|burrito|fajita|quesadilla|enchilada|tostada|nacho)"),
        "burger" to Regex("\\b(burger|wrap|kebab|gyro|shawarma)"),
        "pizza" to Regex("\\b(pizza|flatbread|calzone)"),
        "bowl" to Regex("\\b(bowl|risotto|pilaf|paella|biryani)\\b"),
        "roast" to Regex("\\b(roast|baked|grilled|steak|schnitzel)\\b"),
        "egg" to Regex("\\b(omelet|omelette|frittata|shakshuka|quiche)\\b"),
    )

    /** Coarse dish type from the recipe name, or null if it is not recognisable. */
    fun dishType(name: String): String? {
        val n = name.lowercase()
        return dishTypes.firstOrNull { it.second.containsMatchIn(n) }?.first
    }

    /** The protein source contributing most protein, as a coarse group (chicken, beef, fish, ...). */
    fun mainProtein(servings: Int, ingredients: List<Ingredient>): String {
        val byGroup = HashMap<String, Double>()
        for (ing in ingredients) {
            val n = ing.name.lowercase()
            if (notFood.containsMatchIn(n)) continue
            val key = protein.keys.filter { n.contains(it) }.maxByOrNull { it.length } ?: continue
            val p = protein.getValue(key)
            val info = LidlPrices.default(n) ?: PriceInfo(0.0, "kg")
            val grams = if (ing.unit.isEmpty() && p.pieceG != null) (ing.quantity ?: 1.0) * p.pieceG
            else LidlPrices.grams(info, ing.quantity, ing.unit)
            val g = groups[key] ?: key
            byGroup[g] = (byGroup[g] ?: 0.0) + grams * p.per100 / 100.0
        }
        return byGroup.maxByOrNull { it.value }?.key ?: "other"
    }

    /** Estimated protein in grams for one serving of the recipe. */
    fun proteinPerServing(servings: Int, ingredients: List<Ingredient>): Double {
        var total = 0.0
        for (ing in ingredients) {
            val n = ing.name.lowercase()
            if (notFood.containsMatchIn(n)) continue
            val key = protein.keys.filter { n.contains(it) }.maxByOrNull { it.length } ?: continue
            val p = protein.getValue(key)
            val info = LidlPrices.default(n) ?: PriceInfo(0.0, "kg")
            val grams = if (ing.unit.isEmpty() && p.pieceG != null) (ing.quantity ?: 1.0) * p.pieceG
            else LidlPrices.grams(info, ing.quantity, ing.unit)
            total += grams * p.per100 / 100.0
        }
        return total / servings.coerceAtLeast(1)
    }
}
