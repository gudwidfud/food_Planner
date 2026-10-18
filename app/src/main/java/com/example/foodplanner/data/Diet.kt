package com.example.foodplanner.data

/** Dietary preferences. A recipe is allowed only if none of its ingredients match a selected diet's excluded foods. */
enum class Diet(val label: String, private val excluded: Regex) {
    Vegan(
        "Vegan",
        Regex("\\b(chicken|beef|pork|lamb|mutton|veal|turkey|duck|bacon|ham|sausage|chorizo|salami|prosciutto|mince|steak|meat|gelatin|lard|anchov|fish|salmon|tuna|cod|haddock|trout|mackerel|sardine|prawn|shrimp|crab|lobster|mussel|clam|squid|oyster|seafood|egg|milk|cream|butter|cheese|parmesan|mozzarella|feta|cheddar|halloumi|ricotta|yogh?urt|ghee|honey|mayonnaise|mayo|aioli|paneer|whey|fish sauce|worcestershire)"),
    ),
    Vegetarian(
        "Vegetarian",
        Regex("\\b(chicken|beef|pork|lamb|mutton|veal|turkey|duck|bacon|ham|sausage|chorizo|salami|prosciutto|mince|steak|meat|gelatin|lard|anchov|fish|salmon|tuna|cod|haddock|trout|mackerel|sardine|prawn|shrimp|crab|lobster|mussel|clam|squid|oyster|seafood|fish sauce|worcestershire)"),
    ),
    Pescatarian(
        "Pescatarian",
        Regex("\\b(chicken|beef|pork|lamb|mutton|veal|turkey|duck|bacon|ham|sausage|chorizo|salami|prosciutto|mince|steak|meat|gelatin|lard)"),
    ),
    Halal(
        "Halal (no pork or alcohol)",
        Regex("\\b(pork|bacon|ham|prosciutto|pancetta|chorizo|salami|pepperoni|lard|gelatin|wine|beer|rum|brandy|whisky|whiskey|vodka|sherry|mirin|sake|liqueur|cider|marsala|port|bourbon|gin)\\b"),
    ),
    DairyFree(
        "Dairy-free",
        Regex("\\b(milk|cream|butter|cheese|parmesan|mozzarella|feta|cheddar|halloumi|ricotta|yogh?urt|ghee|paneer|whey|mascarpone|creme fraiche)"),
    ),
    GlutenFree(
        "Gluten-free",
        Regex("\\b(flour|bread|pasta|spaghetti|penne|noodle|couscous|bulgur|barley|rye|tortilla|pita|bun|breadcrumb|panko|soy sauce|seitan|lasagn|macaroni|gnocchi|wrap|naan|farro|orzo|semolina|tostada|crouton)"),
    ),
    NutFree(
        "Nut-free",
        Regex("\\b(peanut|almond|walnut|cashew|pecan|pistachio|hazelnut|macadamia|pine nut|nut)"),
    );

    fun allows(ingredientNames: List<String>): Boolean = ingredientNames.none { excluded.containsMatchIn(it.lowercase()) }

    companion object {
        fun allowed(diets: Set<Diet>, ingredientNames: List<String>): Boolean = diets.all { it.allows(ingredientNames) }
    }
}
