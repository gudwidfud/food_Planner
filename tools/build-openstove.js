// Builds app/src/main/assets/openstove_recipes.json from the OpenStove repo
// (https://github.com/mearashadowfax/OpenStove, recipes licensed CC BY-NC-SA 4.0).
// Usage: cd tools; npm install; node build-openstove.js
const fs = require("fs");
const path = require("path");
const YAML = require("yaml");

const API = "https://api.github.com/repos/mearashadowfax/OpenStove/git/trees/main?recursive=1";
const RAW = "https://raw.githubusercontent.com/mearashadowfax/OpenStove/main/";
const OUT = path.join(__dirname, "..", "app", "src", "main", "assets", "openstove_recipes.json");

(async () => {
    const tree = (await (await fetch(API)).json()).tree;
    const files = tree.filter((f) => /^src\/content\/recipes\/.+\.md$/.test(f.path));
    const out = [];
    for (const f of files) {
        const text = await (await fetch(RAW + f.path)).text();
        const front = text.match(/^---\r?\n([\s\S]*?)\r?\n---/);
        if (!front) continue;
        let doc;
        try { doc = YAML.parse(front[1]); } catch { continue; }
        const lines = [];
        for (const group of doc.ingredients ?? []) {
            for (const it of group.items ?? []) {
                const line = [it.quantity, it.unit, it.name].map((x) => String(x ?? "").trim()).filter(Boolean).join(" ");
                if (line) lines.push(line);
            }
        }
        if (!doc.title || !lines.length) continue;
        const steps = (doc.steps ?? []).map((s) => {
            const body = (s.actions ?? []).map((a) => String(a).trim()).filter(Boolean).join(" ");
            const title = String(s.title ?? "").trim();
            return title && body ? title + ": " + body : body || title;
        }).filter(Boolean);
        out.push({
            name: doc.cardTitle || doc.title,
            url: "https://openstove.org/recipes/" + path.basename(f.path, ".md"),
            tags: doc.tags ?? [],
            ingredients: lines,
            steps,
        });
    }
    fs.mkdirSync(path.dirname(OUT), { recursive: true });
    fs.writeFileSync(OUT, JSON.stringify(out, null, 1));
    console.log("Wrote", out.length, "recipes to", OUT);
})();
