package com.example.foodplanner.data

/** Supermarket-style sections for the grocery list, in shopping order. */
enum class GroceryCategory(val label: String) {
    Produce("Fruit & vegetables"),
    Meat("Meat, poultry & fish"),
    Dairy("Dairy & eggs"),
    Bakery("Bread & bakery"),
    Pantry("Rice, pasta & canned goods"),
    Sauces("Oils, sauces & condiments"),
    Spices("Salt, pepper, herbs & spices"),
    Baking("Baking & sweet"),
    Other("Other"),
}

object GroceryCategories {
    // First match wins, so specific phrases come before generic ones.
    private val rules = listOf(
        GroceryCategory.Pantry to Regex("coconut milk|peanut butter|tomato paste|tomato puree|chopped tomato|tinned|canned|passata|stock|broth|bouillon"),
        GroceryCategory.Sauces to Regex("olive oil|vegetable oil|sunflower oil|\\boil\\b|vinegar|soy sauce|sauce|ketchup|mayonnaise|mayo|mustard|dressing|pesto|wine|tahini|worcestershire|sriracha"),
        GroceryCategory.Spices to Regex("\\bsalt\\b|pepper(?!s)|black pepper|chilli powder|chili powder|cumin|paprika|cinnamon|oregano|thyme|rosemary|turmeric|curry|spice|herb|bay leaf|nutmeg|coriander seed|cayenne|chilli flakes|chili flakes|garam masala|seasoning|saffron|cardamom"),
        GroceryCategory.Meat to Regex("chicken|turkey|duck|beef|steak|pork|lamb|bacon|ham\\b|sausage|chorizo|mince|salami|prosciutto|salmon|tuna|cod\\b|haddock|fish|prawn|shrimp|mackerel|anchov|mussel|squid|meatball|veal|liver|quail|belly"),
        GroceryCategory.Dairy to Regex("\\beggs?\\b|milk|cream|yogurt|yoghurt|butter(?!nut)|cheese|parmesan|mozzarella|feta|cheddar|halloumi|ricotta|mascarpone|curd|kefir"),
        GroceryCategory.Bakery to Regex("bread|baguette|\\bbun|rolls?\\b|tortilla|wrap|pitta|pita|naan|toast|croissant|bagel"),
        GroceryCategory.Baking to Regex("flour|sugar|baking|yeast|honey|syrup|chocolate|cocoa|vanilla|raisin|sultana|dried fruit|jam|almond|walnut|cashew|hazelnut|pecan|\\bnuts?\\b|seed"),
        GroceryCategory.Produce to Regex("onion|garlic|ginger|carrot|potato|yam|tomato|pepper|chilli|chili|mushroom|chanterelle|courgette|zucchini|aubergine|eggplant|broccoli|cauliflower|cabbage|spinach|lettuce|rocket|arugula|salad|kale|cucumber|leek|celery|pea\\b|peas\\b|corn|bean|asparagus|sprout|pumpkin|squash|beetroot|radish|daikon|microgreen|herbs?|parsley|basil|mint|dill|cilantro|lemon|lime|orange|apple|banana|avocado|berr|grape|plum|persimmon|pear|mango|cranberr|olive|scallion|spring onion|fennel|turnip|swede|parsnip|chard"),
        GroceryCategory.Pantry to Regex("rice|pasta|spaghetti|penne|fusilli|noodle|macaroni|lasagn|tagliatelle|couscous|quinoa|buckwheat|barley|bulgur|oats?\\b|polenta|lentil|chickpea|groats|dumpling|hummus"),
    )

    fun of(name: String): GroceryCategory {
        val n = name.lowercase()
        // Fresh herbs and peppers are produce; dried/ground spices go with spices.
        if (Regex("fresh|bell pepper|red pepper|green pepper|sweet pepper|chilli pepper|chili pepper|peppers").containsMatchIn(n)) {
            return GroceryCategory.Produce
        }
        return rules.firstOrNull { it.second.containsMatchIn(n) }?.first ?: GroceryCategory.Other
    }
}
