package com.example.foodplanner.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import kotlin.math.floor

private val UNICODE_FRACTIONS = mapOf(
    '½' to 0.5, '¼' to 0.25, '¾' to 0.75, '⅓' to (1.0 / 3), '⅔' to (2.0 / 3), '⅛' to 0.125,
)

private val UNITS = setOf(
    "cup", "cups", "tbsp", "tablespoon", "tablespoons", "tsp", "teaspoon", "teaspoons",
    "g", "gram", "grams", "kg", "ml", "l", "liter", "litre", "oz", "ounce", "ounces",
    "lb", "lbs", "pound", "pounds", "clove", "cloves", "can", "cans", "pinch", "slice", "slices",
)

/** Parses lines like "1 1/2 cups flour" or "2 eggs". Quantity is null when absent. */
fun parseIngredient(line: String): Ingredient? {
    var text = line.trim().trimStart('-', '*', '•').trim()
    if (text.isEmpty()) return null
    // "200g chicken" -> "200 g chicken"
    text = text.replace(Regex("^(\\d[\\d./]*)([a-zA-Z]+)"), "$1 $2")
    var qty: Double? = null
    val tokens = text.split(Regex("\\s+")).toMutableList()
    while (tokens.isNotEmpty()) {
        val v = parseNumber(tokens.first()) ?: break
        qty = (qty ?: 0.0) + v
        tokens.removeAt(0)
    }
    var unit = ""
    if ((qty != null) && (tokens.size > 1) && (tokens.first().trimEnd('.').lowercase() in UNITS)) {
        unit = tokens.removeAt(0).trimEnd('.').lowercase()
    }
    text = tokens.joinToString(" ").trim()
    if (text.isEmpty()) return null
    return Ingredient(recipeId = 0, name = text, quantity = qty, unit = unit)
}

private fun parseNumber(token: String): Double? {
    if (token.length == 1 && token[0] in UNICODE_FRACTIONS) return UNICODE_FRACTIONS[token[0]]
    token.toDoubleOrNull()?.let { return it }
    val parts = token.split("/")
    if (parts.size == 2) {
        val n = parts[0].toDoubleOrNull()
        val d = parts[1].toDoubleOrNull()
        if (n != null && d != null && d != 0.0) return n / d
    }
    return null
}

fun formatQuantity(q: Double?): String {
    if (q == null) return ""
    return if (q == floor(q)) q.toLong().toString() else String.format(Locale.US, "%.2f", q).trimEnd('0').trimEnd('.')
}

fun Ingredient.display(): String =
    listOf(formatQuantity(quantity), unit, name).filter { it.isNotEmpty() }.joinToString(" ")

data class ImportedRecipe(
    val name: String,
    val servings: Int,
    val ingredients: List<String>,
    val sourceUrl: String? = null,
    val category: String? = null,
    val instructions: String? = null,
    val cuisine: String? = null,
)

object RecipeImporter {
    suspend fun fromUrl(url: String): ImportedRecipe = withContext(Dispatchers.IO) {
        val conn = java.net.URL(url.trim()).openConnection() as java.net.HttpURLConnection
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android) FoodPlanner")
        val html = try {
            conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
        val blocks = Regex(
            "<script[^>]*application/ld\\+json[^>]*>(.*?)</script>",
            setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE),
        ).findAll(html).map { it.groupValues[1].trim() }
        for (block in blocks) {
            val root = runCatching { if (block.startsWith("[")) JSONArray(block) else JSONObject(block) }.getOrNull()
            val recipe = root?.let { findRecipe(it) } ?: continue
            val ings = recipe.optJSONArray("recipeIngredient") ?: continue
            val list = (0 until ings.length()).map { decodeEntities(ings.optString(it)) }
            return@withContext ImportedRecipe(
                name = decodeEntities(recipe.optString("name", "Imported recipe")),
                servings = parseYield(recipe.opt("recipeYield")),
                ingredients = list,
                instructions = parseSteps(recipe.opt("recipeInstructions")),
            )
        }
        throw IllegalStateException("No recipe data found on that page")
    }

    private fun findRecipe(node: Any?): JSONObject? = when (node) {
        is JSONArray -> (0 until node.length()).firstNotNullOfOrNull { findRecipe(node.opt(it)) }
        is JSONObject -> {
            val isRecipe = when (val type = node.opt("@type")) {
                is String -> type == "Recipe"
                is JSONArray -> (0 until type.length()).any { type.optString(it) == "Recipe" }
                null -> false
                else -> false
            }
            if (isRecipe) node else findRecipe(node.opt("@graph"))
        }
        else -> null
    }

    private fun parseSteps(node: Any?): String? {
        val steps = mutableListOf<String>()
        fun walk(n: Any?) {
            when (n) {
                is String -> n.split(Regex("\\r?\\n")).map { decodeEntities(it.replace(Regex("<[^>]+>"), " ")) }
                    .filter { it.isNotBlank() }.forEach { steps.add(it) }
                is JSONArray -> for (i in 0 until n.length()) walk(n.opt(i))
                is JSONObject -> if (n.has("itemListElement")) walk(n.opt("itemListElement")) else walk(n.optString("text"))
            }
        }
        walk(node)
        return steps.takeIf { it.isNotEmpty() }?.joinToString("\n")
    }

    private fun parseYield(value: Any?): Int {
        val text = when (value) {
            is JSONArray -> value.optString(0)
            else -> value?.toString().orEmpty()
        }
        return Regex("\\d+").find(text)?.value?.toIntOrNull()?.coerceAtLeast(1) ?: 2
    }

    private fun decodeEntities(s: String): String = s
        .replace("&amp;", "&").replace("&quot;", "\"").replace("&#39;", "'")
        .replace("&#x27;", "'").replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">")
        .trim()
}
