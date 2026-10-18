// Builds app/src/main/assets/hofer_prices.json from the Heisse Preise dataset
// (https://github.com/badlogic/heissepreise). Lidl is not in that dataset, so Hofer
// (a similarly priced discounter) is used as a stand-in.
//
// Usage: node tools/build-prices.js [path-to-latest-canonical.json]
// Without an argument the dataset (~200 MB) is downloaded first.
const fs = require("fs");
const path = require("path");

const URL = "https://heisse-preise.io/data/latest-canonical.json";
const OUT = path.join(__dirname, "..", "app", "src", "main", "assets", "hofer_prices.json");
const STORE = "hofer";

// key = first keyword of the matching entry in Prices.kt; value = [include regex, exclude regex?]
const RULES = {
    "chicken breast": [/h(ü|ue)hnerbrust|hendlbrust|h(ü|u)hnerfilet|hendlfilet/i, /pan|nugget|schnitzel|knusper|gewürz|aufschnitt/i],
    "chicken thigh": [/(h(ü|u)hner|hendl).*(keule|schenkel|unterkeule)/i],
    "chicken": [/^(\w+ )?(h(ü|u)hner|hendl)/i, /suppe|nudel|salat|aufstrich|nugget|würst/i],
    "minced beef": [/faschiertes.*rind|rind.*faschiert|hackfleisch.*rind/i],
    "minced pork": [/faschiertes|hackfleisch/i, /rind|veg/i],
    "pork": [/schwein/i, /würst|schinken|speck|aufschnitt/i],
    "bacon": [/\bspeck\b|bacon/i],
    "sausage": [/bratw(ü|u)rst|w(ü|u)rstel|wurst/i],
    "salmon": [/lachs/i, /räucher|aufstrich|salat/i],
    "tuna": [/thunfisch/i],
    "egg": [/\beier\b|bodenhaltung|freiland/i, /nudel|likör|salat|färb|schoko/i],
    "milk": [/^(\w+ )*(h-)?milch\b|vollmilch|frischmilch/i, /schoko|kokos|reis|hafer|soja|mandel|shake|pulver|kondens|butter|joghurt/i],
    "cream": [/obers|schlag/i, /sauer|creme|kaffee/i],
    "yogurt": [/naturjoghurt|joghurt natur/i],
    "butter": [/^(\w+ )*butter\b/i, /milch|keks|brot|schmalz|kräuter/i],
    "parmesan": [/parmesan|grana|parmigiano/i],
    "mozzarella": [/mozzarella/i],
    "feta": [/feta|hirtenk/i],
    "cheddar": [/gouda|cheddar|edamer|emmentaler|bergk/i, /scheiben|aufstrich|schmelz/i],
    "flour": [/\bmehl\b/i, /pizza|kuchen|brot|back|pfann/i],
    "sugar": [/kristallzucker|feinkristall|zucker/i, /vanill|puder|braun|würfel|rohr/i],
    "honey": [/honig/i],
    "rice": [/\breis\b/i, /milch|waffel|cracker|nudel|pudding/i],
    "spaghetti": [/spaghetti|penne|fusilli|nudeln|makkaroni/i, /vollkorn|glas|suppe|ei/i],
    "oats": [/haferflocken/i],
    "potato": [/kartoffel|erdäpfel/i, /chips|pommes|püree|knödel|salat|gratin|kroket|rösti/i],
    "onion": [/zwiebel/i, /röst|ring|suppe|salat/i],
    "garlic": [/knoblauch/i, /pulver|granulat|sauce|zehen/i],
    "carrot": [/karotten|möhren/i, /saft|kuchen|salat|suppe|weck/i],
    "chopped tomato": [/tomaten.*(stückig|gehackt|passiert)|passata|stückige tomaten/i],
    "tomato paste": [/tomatenmark/i],
    "tomato": [/^(\w+ )?tomaten\b/i, /sauce|suppe|ketchup|mark|getrock|stück|passiert|dose/i],
    "pepper": [/paprika/i, /pulver|gewürz|chips|sauce|salat|gefüllt/i],
    "mushroom": [/champignon|pilz/i, /sauce|suppe|dose|creme|reis/i],
    "courgette": [/zucchini/i],
    "broccoli": [/brokkoli|broccoli/i, /creme|suppe|auflauf|tiefk/i],
    "cabbage": [/kraut|kohl/i, /salat|sauer|rot|blau|suppe|rolle/i],
    "lettuce": [/salat\b/i, /dressing|sauce|mix|nudel|kraut|bowl|kartoffel/i],
    "cucumber": [/gurke/i, /essig|salz|süß|eingelegt|glas|sauer/i],
    "pea": [/erbsen/i, /suppe|creme|chips|snack/i],
    "corn": [/mais\b/i, /chips|flips|stärke|griess|popcorn|mehl/i],
    "chickpea": [/kichererbsen|kidney|bohnen|linsen/i, /salat|sauce|suppe|chips/i],
    "coconut milk": [/kokosn?ussmilch|kokosmilch/i],
    "olive oil": [/oliven(ö|o)l/i],
    "oil": [/sonnenblumen(ö|o)l|raps(ö|o)l|pflanzen(ö|o)l|speise(ö|o)l/i],
    "vinegar": [/essig/i, /balsamico creme|dressing/i],
    "soy sauce": [/sojasauce|sojasoße/i],
    "ketchup": [/ketchup/i],
    "mayonnaise": [/mayonnaise|majonäse/i, /salat|fix/i],
    "mustard": [/senf/i],
    "salt": [/^(\w+ )*(speise|meer|jod)?salz\b/i, /mühle|gewürz|salzstangen|brezel|knabber|nuss|kräuter|bad/i],
    "lemon": [/zitronen?\b/i, /saft|eistee|limo|tee|kuchen|dressing/i],
    "orange": [/orangen\b/i, /saft|limo|marmelade|schokol|nektar/i],
    "apple": [/^(\w+ )?äpfel|apfel\b/i, /saft|mus|kuchen|strudel|chips|essig|schorle/i],
    "banana": [/bananen/i, /chips|schoko|joghurt/i],
    "avocado": [/avocado/i, /creme|aufstrich/i],
    "berry": [/erdbeeren|heidelbeeren|himbeeren|beeren/i, /marmelade|joghurt|saft|kuchen|eis/i],
    "almond": [/mandeln|walnüsse|cashew|erdnüsse|nüsse/i, /schoko|riegel|öl|milch|butter|mus/i],
    "chocolate": [/kochschokolade|backschokolade|schokolade/i, /riegel|aufstrich|drops|eis/i],
    "baking powder": [/backpulver|trockengerm|germ\b/i],
};

function perUnit(item) {
    const q = item.quantity, u = item.unit;
    if (!item.price || !q) return null;
    if (u === "g") return { basis: "kg", value: (item.price / q) * 1000 };
    if (u === "ml") return { basis: "l", value: (item.price / q) * 1000 };
    if (u === "stk") return { basis: "each", value: item.price / q };
    return null;
}

(async () => {
    let data;
    if (process.argv[2]) {
        data = JSON.parse(fs.readFileSync(process.argv[2], "utf8"));
    } else {
        console.log("Downloading", URL);
        data = await (await fetch(URL)).json();
    }
    // The hosted dataset is a stale snapshot where every item is flagged unavailable, so that flag is ignored.
    const items = data.filter((i) => i.store === STORE);
    const out = {};
    for (const [key, [inc, exc]] of Object.entries(RULES)) {
        const candidates = items
            .filter((i) => inc.test(i.name) && !(exc && exc.test(i.name)))
            .map((i) => ({ i, p: perUnit(i) }))
            .filter((c) => c.p);
        if (!candidates.length) { console.log("no match:", key); continue; }
        // Group by basis, keep the most common, take the median of the cheapest few.
        const byBasis = {};
        for (const c of candidates) (byBasis[c.p.basis] ||= []).push(c);
        const basis = Object.keys(byBasis).sort((a, b) => byBasis[b].length - byBasis[a].length)[0];
        const vals = byBasis[basis].map((c) => c.p.value).sort((a, b) => a - b).slice(0, 5);
        const price = vals[Math.floor(vals.length / 2)];
        out[key] = { price: Math.round(price * 100) / 100, basis, n: byBasis[basis].length };
        console.log(key.padEnd(16), basis.padEnd(5), out[key].price, `(${out[key].n} items)`);
    }
    fs.mkdirSync(path.dirname(OUT), { recursive: true });
    fs.writeFileSync(OUT, JSON.stringify(out, null, 1));
    console.log("Wrote", OUT);
})();
