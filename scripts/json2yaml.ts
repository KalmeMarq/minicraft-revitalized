import { parse, stringify } from "jsr:@std/yaml";
import { walkSync } from "jsr:@std/fs";

if (import.meta.main) {
    for (const file of walkSync('.')) {
        if (file.path.endsWith('.json')) {
            const content = JSON.parse(Deno.readTextFileSync(file.path));
            const yamlContent = stringify(content, { lineWidth: -1 });
            Deno.writeTextFileSync(file.path.replace('.json', '.yaml'), yamlContent);
            Deno.removeSync(file.path);
        }
    }
}
