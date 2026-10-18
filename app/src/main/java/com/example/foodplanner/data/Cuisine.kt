package com.example.foodplanner.data

/** Where a dish comes from: TheMealDB's area when known, otherwise a guess from the recipe name and ingredients. */
object Cuisine {
    private val countries = mapOf(
        "american" to "United States", "british" to "United Kingdom", "canadian" to "Canada", "chinese" to "China",
        "croatian" to "Croatia", "dutch" to "Netherlands", "egyptian" to "Egypt", "filipino" to "Philippines",
        "french" to "France", "greek" to "Greece", "indian" to "India", "irish" to "Ireland", "italian" to "Italy",
        "jamaican" to "Jamaica", "japanese" to "Japan", "kenyan" to "Kenya", "malaysian" to "Malaysia",
        "mexican" to "Mexico", "moroccan" to "Morocco", "polish" to "Poland", "portuguese" to "Portugal",
        "russian" to "Russia", "spanish" to "Spain", "thai" to "Thailand", "tunisian" to "Tunisia", "turkish" to "Turkey",
        "ukrainian" to "Ukraine", "uruguayan" to "Uruguay", "vietnamese" to "Vietnam", "korean" to "South Korea",
        "german" to "Germany", "austrian" to "Austria", "swedish" to "Sweden", "lebanese" to "Lebanon",
        "israeli" to "Israel", "persian" to "Iran", "ethiopian" to "Ethiopia", "peruvian" to "Peru",
        "brazilian" to "Brazil", "argentinian" to "Argentina", "indonesian" to "Indonesia", "hungarian" to "Hungary",
        "cuban" to "Cuba", "caribbean" to "Caribbean", "nigerian" to "Nigeria", "georgian" to "Georgia",
    )

    private val hints = listOf(
        "italian" to Regex("\\b(pasta|spaghetti|lasagn|risotto|carbonara|bolognese|parmigiana|parmesan|gnocchi|pesto|penne|pizza|bruschetta|tagliatelle|ragu|pancetta|prosciutto|focaccia|arrabbiata|puttanesca|minestrone)"),
        "mexican" to Regex("\\b(taco|burrito|enchilada|fajita|quesadilla|tostada|nacho|chipotle|guacamole|salsa|jalape|chilaquiles|carnitas|tortilla)"),
        "indian" to Regex("\\b(curry|masala|tikka|korma|biryani|dal|dhal|paneer|tandoori|naan|garam|samosa|saag|vindaloo|chana|aloo)"),
        "thai" to Regex("\\b(thai|pad thai|lemongrass|galangal|tom yum|green curry|red curry)"),
        "chinese" to Regex("\\b(chow mein|lo mein|kung pao|szechuan|sichuan|hoisin|bok choy|wonton|dumpling|sweet and sour|stir[- ]?fry|chinese)"),
        "japanese" to Regex("\\b(teriyaki|sushi|ramen|miso|udon|katsu|tempura|yakitori|donburi|japanese)"),
        "korean" to Regex("\\b(kimchi|bulgogi|gochujang|bibimbap|korean)"),
        "vietnamese" to Regex("\\b(pho|banh mi|vietnamese)"),
        "greek" to Regex("\\b(greek|gyro|souvlaki|moussaka|tzatziki|feta|halloumi)"),
        "turkish" to Regex("\\b(turkish|kofte|shakshuka|menemen|doner)"),
        "moroccan" to Regex("\\b(tagine|moroccan|harissa|couscous|ras el hanout)"),
        "lebanese" to Regex("\\b(hummus|falafel|shawarma|tabbouleh|lebanese|za'atar)"),
        "spanish" to Regex("\\b(paella|chorizo|tortilla espa|gazpacho|spanish|patatas bravas)"),
        "french" to Regex("\\b(french|ratatouille|coq au vin|bourguignon|quiche|croque|cassoulet|dijon|gratin|nicoise|ni\u00e7oise)"),
        "german" to Regex("\\b(schnitzel|bratwurst|sauerkraut|spaetzle|spätzle|german)"),
        "british" to Regex("\\b(shepherd|cottage pie|fish and chips|bangers|yorkshire|british)"),
        "american" to Regex("\\b(burger|bbq|barbecue|buffalo|mac and cheese|cajun|chili con|meatloaf)"),
        "jamaican" to Regex("\\b(jerk|jamaican)"),
    )

    fun of(area: String?, name: String, ingredientNames: List<String>): String? {
        val known = area?.trim()?.lowercase()?.takeIf { it.isNotEmpty() && it != "unknown" }
        if (known != null) return known
        val text = (listOf(name) + ingredientNames).joinToString(" ").lowercase()
        return hints.firstOrNull { it.second.containsMatchIn(name.lowercase()) }?.first
            ?: hints.firstOrNull { it.second.containsMatchIn(text) }?.first
    }

    /** "Italy" for "italian"; falls back to the capitalised cuisine name. */
    fun place(cuisine: String?): String? = cuisine?.lowercase()?.let { countries[it] ?: it.replaceFirstChar(Char::uppercase) }
}
