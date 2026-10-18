package com.example.foodplanner.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

/**
 * Recipes from OpenStove (https://github.com/mearashadowfax/OpenStove, CC BY-NC-SA 4.0), bundled as
 * assets/openstove_recipes.json by tools/build-openstove.js. Quantities there are for a single portion.
 */
object OpenStove {
    private val skipTags = setOf("dessert", "snack", "breakfast", "dip", "side dish", "bread", "sandwich", "camping", "porridge")

    suspend fun load(context: Context): List<ImportedRecipe> = withContext(Dispatchers.IO) {
        runCatching {
            val arr = JSONArray(context.assets.open("openstove_recipes.json").bufferedReader().use { it.readText() })
            (0 until arr.length()).mapNotNull { i ->
                val o = arr.getJSONObject(i)
                val tags = o.getJSONArray("tags").let { t -> (0 until t.length()).map { t.getString(it).lowercase() } }
                if (tags.any { it in skipTags }) return@mapNotNull null
                val ings = o.getJSONArray("ingredients").let { a -> (0 until a.length()).map { a.getString(it) } }
                val steps = o.optJSONArray("steps")?.let { a -> (0 until a.length()).map { a.getString(it) } }.orEmpty()
                ImportedRecipe(o.getString("name"), 1, ings, o.optString("url").takeIf { it.isNotBlank() }, instructions = steps.joinToString("\n").takeIf { it.isNotBlank() })
            }
        }.getOrDefault(emptyList())
    }
}
