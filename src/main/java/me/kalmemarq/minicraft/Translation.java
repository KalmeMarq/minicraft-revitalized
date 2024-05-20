package me.kalmemarq.minicraft;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Translation {
	private static final Logger LOGGER = LogManager.getLogger(Translation.class);
	private static final Map<String, String> entries = new HashMap<>();
	public static final Map<String, LanguageMetadata> metadata = new HashMap<>();

	public static String currentCode = "en_us";

	public static void load(String code) {
		try {
			if (metadata.isEmpty()) {
				JsonNode node = IOUtils.JSON_OBJECT_MAPPER.readTree(Translation.class.getResource("/langs/languages.json"));
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

			JsonNode node = IOUtils.JSON_OBJECT_MAPPER.readTree(Translation.class.getResource("/langs/" + code + ".json"));
			currentCode = code;
			if (node instanceof ObjectNode objectNode) {
				for (Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields(); it.hasNext();) {
					var entry = it.next();
					String key = entry.getKey();
					String value = entry.getValue().textValue();
					if (value.length() > 9 && key.startsWith("minicraft.item.")) {
						LOGGER.warn(code + "/" + entry.getKey() + " is " + value.length() + " characters long");
					}
					entries.put(entry.getKey(), value);
				}
			}

			if (!"en_us".equals(code)) {
				node = IOUtils.JSON_OBJECT_MAPPER.readTree(Translation.class.getResource("/langs/en_us.json"));
				if (node instanceof ObjectNode objectNode) {
					for (Iterator<Map.Entry<String, JsonNode>> it = objectNode.fields(); it.hasNext();) {
						var entry = it.next();
						String key = entry.getKey();
						String value = entry.getValue().textValue();
						if (value.length() > 9 && key.startsWith("minicraft.item.")) {
							LOGGER.warn("en_us/" + entry.getKey() + " is " + value.length() + " characters long");
						}
						entries.putIfAbsent(entry.getKey(), value);
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error(e);
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
			LOGGER.error(e);
			return k;
		}
	}

	public record LanguageMetadata(String name, String region) {
	}
}
