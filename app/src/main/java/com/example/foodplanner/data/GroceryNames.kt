package com.example.foodplanner.data

/** Reduces recipe ingredient text to the thing you actually buy, so "olive oil, for drizzling" merges with "olive oil". */
object GroceryNames {
    private val prep = Regex(
        "\\b(finely|roughly|thinly|freshly|coarsely|lightly|large|medium|small|big|ripe|fresh|freshly ground|chopped|diced|sliced|minced|" +
            "crushed|grated|shredded|peeled|halved|quartered|cubed|trimmed|drained|rinsed|juiced|zested|melted|softened|cooked|" +
            "boneless|skinless|extra[- ]virgin|good quality|good|quality|optional|plain|whole)\\b",
    )
    private val tail = Regex("\\b(to taste|for (the |a |some )?[a-z ]+|as needed|to serve|to garnish|for garnish|for serving|for frying|for drizzling|for roasting)\\b.*$")
    private val irregular = mapOf(
        "tomatoes" to "tomato", "potatoes" to "potato", "leaves" to "leaf", "cloves" to "clove",
        "anchovies" to "anchovy", "berries" to "berry", "radishes" to "radish", "peaches" to "peach",
    )
    private val keepPlural = Regex("(ss|us|is|hummus|couscous|asparagus|molasses|swiss|lentils|oats|greens|beans|peas|chickpeas|noodles|spices)$")

    fun canonical(raw: String): String {
        var s = raw.lowercase()
        s = s.replace(Regex("\\([^)]*\\)"), " ")
        s = s.substringBefore(",").substringBefore(" - ")
        s = s.replace(Regex("^(of|a|an|the)\\s+"), "")
        s = s.replace(Regex("^of\\s+"), "")
        s = s.replace(tail, " ")
        val canned = Regex("\\b(canned|tinned|tin|can|chopped tomato|chopped tomatoes)\\b").containsMatchIn(s)
        if (!canned) s = s.replace(prep, " ")
        s = s.replace(Regex("[^a-z' -]"), " ").replace(Regex("\\s+"), " ").trim()
        s = s.split(" ").filter { it.isNotEmpty() }.joinToString(" ") { singular(it) }
        s = s.replace(Regex("^(of|and|or|with)\\s+"), "").trim()
        return s.ifEmpty { raw.lowercase().trim() }
    }

    private fun singular(w: String): String = when {
        irregular.containsKey(w) -> irregular.getValue(w)
        w.length <= 3 || keepPlural.containsMatchIn(w) -> w
        w.endsWith("ies") -> w.dropLast(3) + "y"
        w.endsWith("oes") -> w.dropLast(2)
        w.endsWith("s") -> w.dropLast(1)
        else -> w
    }

    fun unit(u: String): String = when (u.lowercase().trimEnd('.')) {
        "tablespoon", "tablespoons", "tbsp" -> "tbsp"
        "teaspoon", "teaspoons", "tsp" -> "tsp"
        "cups" -> "cup"
        "gram", "grams" -> "g"
        "cloves" -> "clove"
        "cans" -> "can"
        "slices" -> "slice"
        "liter", "litre" -> "l"
        "ounce", "ounces" -> "oz"
        "lbs", "pound", "pounds" -> "lb"
        else -> u
    }
}
