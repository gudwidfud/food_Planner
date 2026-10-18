@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.foodplanner.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List as AutoMirroredList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodplanner.data.*
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private enum class Tab(val label: String) { Plan("Plan"), Recipes("Recipes"), Grocery("Grocery") }

@Composable
fun FoodPlannerApp(vm: PlannerViewModel = viewModel()) {
    var tab by remember { mutableStateOf(Tab.Plan) }
    var showPrefs by remember { mutableStateOf(false) }
    FoodPlannerTheme {
        if (showPrefs) PreferencesDialog(vm) { showPrefs = false }
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            when (tab) { Tab.Plan -> "Weekly plan"; Tab.Recipes -> "Recipes"; Tab.Grocery -> "Shopping list" },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                    actions = {
                        IconButton(onClick = { showPrefs = true }) { Icon(Icons.Default.Settings, "Food preferences") }
                    },
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    Tab.entries.forEach {
                        NavigationBarItem(
                            selected = tab == it,
                            onClick = { tab = it },
                            icon = {
                                Icon(
                                    when (it) {
                                        Tab.Plan -> Icons.Default.DateRange
                                        Tab.Recipes -> Icons.AutoMirrored.Filled.AutoMirroredList
                                        Tab.Grocery -> Icons.Default.ShoppingCart
                                    }, it.label,
                                )
                            },
                            label = { Text(it.label) },
                        )
                    }
                }
            },
        ) { padding ->
            Box(Modifier.padding(padding)) {
                when (tab) {
                    Tab.Plan -> PlanScreen(vm)
                    Tab.Recipes -> RecipesScreen(vm)
                    Tab.Grocery -> GroceryScreen(vm)
                }
            }
        }
    }
}

@Composable
private fun PreferencesDialog(vm: PlannerViewModel, onDismiss: () -> Unit) {
    val selected by vm.diets.collectAsState()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Food preferences") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    "Auto-fill only picks dinners that fit all selected options. Meals already planned are not changed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Diet.entries.forEach { d ->
                    Row(
                        Modifier.fillMaxWidth().clickable { vm.setDiet(d, d !in selected) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(checked = d in selected, onCheckedChange = { vm.setDiet(d, it) })
                        Text(d.label)
                    }
                }
                Text(
                    "Matching uses ingredient names, so check labels for halal meat certification.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Done") } },
    )
}

@Composable
private fun PlanScreen(vm: PlannerViewModel) {
    val start by vm.weekStart.collectAsState()
    val meals by vm.meals.collectAsState()
    val recipes by vm.recipes.collectAsState()
    var addForDate by remember { mutableStateOf<java.time.LocalDate?>(null) }
    var viewing by remember { mutableStateOf<Recipe?>(null) }
    val allIngredients by vm.ingredients.collectAsState()
    val fmt = DateTimeFormatter.ofPattern("MMM d")

    Column(Modifier.fillMaxSize()) {
        Surface(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Row(Modifier.padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { vm.shiftWeek(-1) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Previous week") }
                Text(
                    "${start.format(fmt)} – ${start.plusDays(6).format(fmt)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { vm.shiftWeek(1) }) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next week") }
            }
        }
        val busy by vm.autoBusy.collectAsState()
        val status by vm.autoStatus.collectAsState()
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = { vm.autoFill() }, enabled = !busy,
                modifier = Modifier.weight(1f).height(48.dp),
            ) {
                Icon(Icons.Default.Star, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (busy) "Planning…" else "Auto-fill dinners")
            }
            FilledTonalButton(onClick = { vm.autoFill(refresh = true) }, enabled = !busy, modifier = Modifier.height(48.dp)) {
                Icon(Icons.Default.Refresh, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Refresh")
            }
        }
        if (busy) LinearProgressIndicator(Modifier.fillMaxWidth().padding(horizontal = 16.dp))
        status?.let {
            Text(
                it, Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items((0..6).map { start.plusDays(it.toLong()) }) { day ->
                val dayMeals = meals.filter { it.date == day.toString() }
                    .sortedBy { MEAL_TYPES.indexOf(it.mealType) }
                val isToday = day == java.time.LocalDate.now()
                Card(
                    Modifier.fillMaxWidth().clickable(enabled = dayMeals.isNotEmpty()) {
                        viewing = recipes.find { it.id == dayMeals.first().recipeId }
                    },
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isToday) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceContainerLowest,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isToday) 0.dp else 1.dp),
                ) {
                    Column(Modifier.padding(start = 16.dp, top = 8.dp, end = 4.dp, bottom = 12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    day.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    day.format(fmt),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            FilledTonalIconButton(onClick = { addForDate = day }) { Icon(Icons.Default.Add, "Add meal") }
                            Spacer(Modifier.width(8.dp))
                        }
                        if (dayMeals.isEmpty()) {
                            Text(
                                "Nothing planned",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                        dayMeals.forEach { m ->
                            Row(
                                Modifier.fillMaxWidth().padding(top = 6.dp).clickable {
                                    viewing = recipes.find { it.id == m.recipeId }
                                },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.tertiaryContainer) {
                                    Text(
                                        m.mealType,
                                        Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    )
                                }
                                Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                    Text(
                                        recipes.find { it.id == m.recipeId }?.name ?: "?",
                                        style = MaterialTheme.typography.bodyLarge,
                                        maxLines = 2,
                                    )
                                    recipes.find { it.id == m.recipeId }?.let { r ->
                                        val place = Cuisine.place(
                                            Cuisine.of(r.cuisine, r.name, allIngredients.filter { it.recipeId == r.id }.map { it.name }),
                                        )
                                        if (place != null) Text(
                                            "📍 $place",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                                IconButton(onClick = { vm.swapMeal(m) }) {
                                    Icon(Icons.Default.Refresh, "Swap for a random recipe", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { vm.removeMeal(m.id) }) {
                                    Icon(Icons.Default.Close, "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    viewing?.let { r ->
        RecipeViewDialog(r, allIngredients.filter { it.recipeId == r.id }) { viewing = null }
    }

    addForDate?.let { date ->
        var type by remember { mutableStateOf("Dinner") }
        AlertDialog(
            onDismissRequest = { addForDate = null },
            confirmButton = { TextButton(onClick = { addForDate = null }) { Text("Cancel") } },
            title = { Text("Add meal") },
            text = {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        MEAL_TYPES.forEach {
                            FilterChip(selected = type == it, onClick = { type = it }, label = { Text(it) })
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    if (recipes.isEmpty()) Text("Add a recipe first in the Recipes tab.")
                    LazyColumn(Modifier.heightIn(max = 300.dp)) {
                        items(recipes) { r ->
                            Text(
                                r.name,
                                Modifier.fillMaxWidth().clickable {
                                    vm.addMeal(date, type, r.id); addForDate = null
                                }.padding(vertical = 12.dp),
                            )
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun RecipeViewDialog(recipe: Recipe, ingredients: List<Ingredient>, onDismiss: () -> Unit) {
    val uri = LocalUriHandler.current
    val steps = recipe.instructions?.lines()?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(recipe.name)
                Cuisine.place(Cuisine.of(recipe.cuisine, recipe.name, ingredients.map { it.name }))?.let {
                    Text("📍 $it", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Ingredients (1 serving)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                val scale = recipe.servings.coerceAtLeast(1)
                ingredients.forEach { i ->
                    val q = i.quantity?.div(scale)
                    Text("• " + listOf(formatQuantity(q), i.unit, i.name).filter { it.isNotEmpty() }.joinToString(" "))
                }
                Spacer(Modifier.height(8.dp))
                Text("Instructions", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                if (steps.isEmpty()) {
                    Text(
                        if (recipe.sourceUrl != null) "No instructions saved. Open the source below."
                        else "No instructions saved. Add them by editing the recipe in the Recipes tab.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                steps.forEachIndexed { n, s -> Text("${n + 1}. $s") }
                recipe.sourceUrl?.let { url ->
                    TextButton(onClick = { runCatching { uri.openUri(url) } }) { Text("Open source") }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}

@Composable
private fun RecipesScreen(vm: PlannerViewModel) {
    val recipes by vm.recipes.collectAsState()
    val ingredients by vm.ingredients.collectAsState()
    var editing by remember { mutableStateOf<RecipeDraft?>(null) }
    var importing by remember { mutableStateOf(false) }
    var browsing by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        if (recipes.isEmpty()) {
            Text("No recipes yet. Add one or import from a URL.", Modifier.align(Alignment.Center))
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 112.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(recipes) { r ->
                val lines = ingredients.filter { it.recipeId == r.id }.joinToString("\n") { it.display() }
                Card(
                    Modifier.fillMaxWidth().clickable {
                        editing = RecipeDraft(r.id, r.name, r.servings.toString(), r.sourceUrl, lines, r.instructions.orEmpty())
                    },
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Row(Modifier.padding(start = 16.dp, top = 12.dp, end = 4.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(r.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
                            Text(
                                "${lines.lines().count { it.isNotBlank() }} ingredients · serves ${r.servings}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = { vm.deleteRecipe(r.id) }) {
                            Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        Column(Modifier.align(Alignment.BottomEnd).padding(16.dp), horizontalAlignment = Alignment.End) {
            SmallFloatingActionButton(
                onClick = { browsing = true },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ) { Icon(Icons.Default.Search, "Search TheMealDB") }
            Spacer(Modifier.height(8.dp))
            SmallFloatingActionButton(
                onClick = { importing = true },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ) { Icon(Icons.Default.Share, "Import from URL") }
            Spacer(Modifier.height(8.dp))
            FloatingActionButton(
                onClick = { editing = RecipeDraft(0, "", "2", null, "", "") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Add, "Add recipe")
            }
        }
    }

    editing?.let { d ->
        RecipeEditor(d, onDismiss = { editing = null }) { saved ->
            vm.saveRecipe(saved.id, saved.name, saved.servings.toIntOrNull() ?: 2, saved.url, saved.ingredients, saved.instructions)
            editing = null
        }
    }
    if (browsing) {
        MealDbDialog(onDismiss = { browsing = false }) { r ->
            browsing = false
            editing = RecipeDraft(0, r.name, r.servings.toString(), r.sourceUrl, r.ingredients.joinToString("\n"), r.instructions.orEmpty())
        }
    }
    if (importing) {
        ImportDialog(onDismiss = { importing = false }) { imported, url ->
            importing = false
            editing = RecipeDraft(0, imported.name, imported.servings.toString(), url, imported.ingredients.joinToString("\n"), imported.instructions.orEmpty())
        }
    }
}

private data class RecipeDraft(val id: Long, val name: String, val servings: String, val url: String?, val ingredients: String, val instructions: String)

@Composable
private fun RecipeEditor(draft: RecipeDraft, onDismiss: () -> Unit, onSave: (RecipeDraft) -> Unit) {
    var name by remember { mutableStateOf(draft.name) }
    var servings by remember { mutableStateOf(draft.servings) }
    var ings by remember { mutableStateOf(draft.ingredients) }
    var steps by remember { mutableStateOf(draft.instructions) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (draft.id == 0L) "New recipe" else "Edit recipe") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(name, { name = it }, label = { Text("Name") }, singleLine = true)
                OutlinedTextField(servings, { servings = it.filter(Char::isDigit) }, label = { Text("Servings") }, singleLine = true)
                OutlinedTextField(
                    ings, { ings = it }, label = { Text("Ingredients (one per line)") },
                    placeholder = { Text("2 cups flour\n3 eggs") }, minLines = 4, maxLines = 8,
                )
                OutlinedTextField(
                    steps, { steps = it }, label = { Text("Instructions (one step per line)") },
                    minLines = 3, maxLines = 8,
                )
            }
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank(), onClick = {
                onSave(draft.copy(name = name, servings = servings, ingredients = ings, instructions = steps))
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun MealDbDialog(onDismiss: () -> Unit, onPick: (ImportedRecipe) -> Unit) {
    var query by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var results by remember { mutableStateOf<List<ImportedRecipe>>(emptyList()) }
    val scope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search TheMealDB") },
        text = {
            Column {
                OutlinedTextField(query, { query = it }, label = { Text("e.g. chicken, pasta") }, singleLine = true)
                if (loading) LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp))
                message?.let { Text(it, Modifier.padding(top = 8.dp)) }
                LazyColumn(Modifier.heightIn(max = 280.dp)) {
                    items(results) { r ->
                        Text(
                            r.name,
                            Modifier.fillMaxWidth().clickable { onPick(r) }.padding(vertical = 12.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = query.isNotBlank() && !loading, onClick = {
                loading = true; message = null
                scope.launch {
                    runCatching { MealDb.search(query) }
                        .onSuccess { results = it; message = if (it.isEmpty()) "No results" else null }
                        .onFailure { message = it.message ?: "Search failed" }
                    loading = false
                }
            }) { Text("Search") }
        },
        dismissButton = {
            Row {
                TextButton(enabled = !loading, onClick = {
                    loading = true; message = null
                    scope.launch {
                        runCatching { MealDb.browseAll() }
                            .onSuccess { results = it; message = if (it.isEmpty()) "No results" else "${it.size} recipes" }
                            .onFailure { message = it.message ?: "Load failed" }
                        loading = false
                    }
                }) { Text("Browse all") }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        },
    )
}

@Composable
private fun ImportDialog(onDismiss: () -> Unit, onDone: (ImportedRecipe, String) -> Unit) {
    var url by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = { Text("Import from URL") },
        text = {
            Column {
                OutlinedTextField(url, { url = it }, label = { Text("Recipe page URL") }, singleLine = true)
                if (loading) LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp))
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(enabled = url.isNotBlank() && !loading, onClick = {
                loading = true; error = null
                scope.launch {
                    runCatching { RecipeImporter.fromUrl(url) }
                        .onSuccess { onDone(it, url.trim()) }
                        .onFailure { error = it.message ?: "Import failed"; loading = false }
                }
            }) { Text("Import") }
        },
        dismissButton = { TextButton(enabled = !loading, onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun GroceryScreen(vm: PlannerViewModel) {
    val items by vm.grocery.collectAsState()
    val start by vm.weekStart.collectAsState()
    val fmt = DateTimeFormatter.ofPattern("MMM d")
    var editing by remember { mutableStateOf<GroceryItem?>(null) }
    val total = items.sumOf { it.cost ?: 0.0 }
    val unpriced = items.count { it.cost == null }
    Column(Modifier.fillMaxSize()) {
        Card(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Week of ${start.format(fmt)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    if (items.isEmpty()) "No items yet" else money(total),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    if (items.isEmpty()) "Plan some meals to build your list."
                    else "Estimated at Lidl · ${items.size} items" + if (unpriced > 0) " · $unpriced unpriced" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            items.groupBy { it.category }.forEach { (category, group) ->
                item(key = "header-${category.name}") {
                    Text(
                        "${category.label.uppercase()} · ${group.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 16.dp, top = 20.dp, bottom = 4.dp),
                    )
                }
                items(group, key = { it.key }) { item ->
                Row(
                    Modifier.fillMaxWidth().clickable { vm.toggle(item) }.padding(horizontal = 16.dp, vertical = 4.dp)
                        .alpha(if (item.checked) 0.5f else 1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(item.checked, { vm.toggle(item) })
                    Column(Modifier.weight(1f)) {
                        Text(
                            listOf(formatQuantity(item.quantity), item.unit, item.name).filter { it.isNotEmpty() }.joinToString(" "),
                            textDecoration = if (item.checked) TextDecoration.LineThrough else null,
                        )
                        item.packs?.let { Text("Buy $it", style = MaterialTheme.typography.bodySmall) }
                    }
                    TextButton(onClick = { editing = item }) {
                        Text(
                            when {
                                item.cost == null -> "set price"
                                item.cost == 0.0 -> "incl."
                                else -> money(item.cost)
                            },
                        )
                    }
                }
            }
            }
        }
    }
    editing?.let { item ->
        var text by remember(item.key) { mutableStateOf(item.price?.price?.let { "%.2f".format(Locale.US, it) } ?: "") }
        var basis by remember(item.key) { mutableStateOf(item.price?.basis ?: "kg") }
        AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text("Lidl price: ${item.name}") },
            text = {
                Column {
                    OutlinedTextField(
                        text, { text = it }, singleLine = true,
                        label = { Text("Price (${LidlPrices.CURRENCY})") },
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOf("kg", "l", "each").forEach { b ->
                            RadioButton(basis == b, { basis = b })
                            Text(if (b == "each") "each" else "per $b")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(enabled = text.replace(',', '.').toDoubleOrNull() != null, onClick = {
                    vm.setPrice(item.name, text.replace(',', '.').toDouble(), basis); editing = null
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { vm.resetPrice(item.name); editing = null }) { Text("Reset") } },
        )
    }
}

private fun money(v: Double) = LidlPrices.CURRENCY + String.format(Locale.US, "%.2f", v)
