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

package me.kalmemarq.minicraft.client.menu.ui;

import com.fasterxml.jackson.databind.JsonNode;
import me.kalmemarq.minicraft.client.Client;

import java.util.Map;

public class UIScreen extends UIElement {
	@Override
	public void init(Client client, Map<String, Observable<?>> menuBindingsMap, JsonNode node) {
		super.init(client, menuBindingsMap, node);

		if (node.has("button_mappings") && node.get("button_mappings").isArray()) {
			JsonNode arrayNode = node.get("button_mappings");

			for (JsonNode n : arrayNode) {
				if (n.has("ignored") && n.get("ignored").isBoolean()) {
					if (!n.get("ignored").booleanValue()) break;
				}

				if (n.has("from_button_id") && n.has("to_button_id") && n.has("mapping_type")) {
					this.mappings.add(new ButtonMapping(n.get("from_button_id").asText(), n.get("to_button_id").asText(), n.get("mapping_type").asText()));
				}
			}
		}
	}
}
