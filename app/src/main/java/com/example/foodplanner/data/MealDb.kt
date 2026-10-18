package com.example.foodplanner.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object MealDb {
    // "1" is TheMealDB's public test key
    private const val BASE = "https://www.themealdb.com/api/json/v1/1"

    suspend fun search(query: String): List<ImportedRecipe> =
        fetch("search.php?s=" + URLEncoder.encode(query.trim(), "UTF-8"))

    /** Every meal in the database, fetched by first letter (a-z) in parallel. */
    suspend fun browseAll(): List<ImportedRecipe> = coroutineScope {
        ('a'..'z').map { c -> async { runCatching { fetch("search.php?f=$c") }.getOrDefault(emptyList()) } }
            .awaitAll().flatten().sortedBy { it.name }
    }

    private suspend fun fetch(path: String): List<ImportedRecipe> = withContext(Dispatchers.IO) {
        val url = URL("$BASE/$path")
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        val body = try {
            conn.inputStream.bufferedReader().use { it.readText() }
        } finally {
            conn.disconnect()
        }
        val meals = JSONObject(body).optJSONArray("meals") ?: return@withContext emptyList()
        (0 until meals.length()).map { i ->
            val m = meals.getJSONObject(i)
            val lines = (1..20).mapNotNull { n ->
                val name = m.optString("strIngredient$n").trim()
                if (name.isEmpty() || name == "null") return@mapNotNull null
                val measure = m.optString("strMeasure$n").trim().takeIf { it != "null" }.orEmpty()
                "$measure $name".trim()
            }
            ImportedRecipe(m.optString("strMeal"), 4, lines, m.optString("strSource").takeIf { it.isNotBlank() && it != "null" }, m.optString("strCategory").takeIf { it.isNotBlank() && it != "null" }, m.optString("strInstructions").lines().map { it.trim() }.filter { it.isNotEmpty() }.joinToString("\n").takeIf { it.isNotBlank() }, m.optString("strArea").takeIf { it.isNotBlank() && it != "null" })
        }
    }
}
