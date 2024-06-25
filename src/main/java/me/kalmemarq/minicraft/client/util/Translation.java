/*
 * Minicraft Revitalized.
 * Copyright (C) 2024 KalmeMarq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */

package me.kalmemarq.minicraft.client.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Translation {
    private static final Map<String, String> entries = new HashMap<>();
    public static final Map<String, LanguageMetadata> metadata = new HashMap<>();

    public static String currentCode = "en_us";

    public static void load(String code) {
        try {
            if (metadata.isEmpty()) {
                JsonNode node = IOUtils.YAML_OBJECT_MAPPER.readTree(Translation.class.getResource("/langs/languages.yaml"));
                if (node instanceof ArrayNode arrayNode) {
                    for (JsonNode item : arrayNode) {
                        if (item instanceof ObjectNode objectNode) {
                            String langCode = objectNode.get("code").textValue();
                            ObjectNode langMetadata = (ObjectNode) objectNode.get("metadata");
                            metadata.put(langCode, new LanguageMetadata(langMetadata.get("name").textValue(), langMetadata.get("region").textValue()));
                        }
                    }
                }
            }

            JsonNode node = IOUtils.YAML_OBJECT_MAPPER.readTree(Translation.class.getResource("/langs/" + code + ".yaml"));
            currentCode = code;
            if (node instanceof ObjectNode objectNode) {
                for (Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields(); it.hasNext();) {
                    var entry = it.next();
                    String key = entry.getKey();
                    String value = entry.getValue().textValue();
                    if (value.length() > 9 && key.startsWith("minicraft.item.")) {
                        System.out.println(code + "/" + entry.getKey() + " is " + value.length() + " characters long");
                    }
                    entries.put(entry.getKey(), value);
                }
            }

            if (!"en_us".equals(code)) {
                node = IOUtils.YAML_OBJECT_MAPPER.readTree(Translation.class.getResource("/langs/en_us.yaml"));
                if (node instanceof ObjectNode objectNode) {
                    for (Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields(); it.hasNext();) {
                        var entry = it.next();
                        String key = entry.getKey();
                        String value = entry.getValue().textValue();
                        if (value.length() > 9 && key.startsWith("minicraft.item.")) {
                            System.out.println("en_us/" + entry.getKey() + " is " + value.length() + " characters long");
                        }
                        entries.putIfAbsent(entry.getKey(), value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String translate(String key) {
        return entries.getOrDefault(key, key);
    }

    public static String translate(String key, Object... args) {
        String k = entries.get(key);
        if (k == null) return key;
        try {
            return String.format(k, args);
        } catch (Exception e) {
            e.printStackTrace();
            return k;
        }
    }

    public record LanguageMetadata(String name, String region) {
    }
}
