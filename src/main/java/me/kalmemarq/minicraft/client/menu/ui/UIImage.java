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

public class UIImage extends UIElement {
	private String atlas;
	private String sprite;
	private int nx0 = -1;
	private int ny0 = -1;
	private int nx1 = -1;
	private int ny1 = -1;

	@Override
	public void init(Client client, Map<String, Observable<?>> menuBindingsMap, JsonNode node) {
		super.init(client, menuBindingsMap, node);

		if (node.has("texture")) {
			String n = node.get("texture").textValue();

			if (n.startsWith("#") && this.propertyBag.containsKey(n)) {
				n = String.valueOf(this.propertyBag.get(n).get());
				this.atlas = n.substring(0, n.indexOf("#"));
				this.sprite = n.substring(n.indexOf("#") + 1);
				this.propertyBag.get(n).cast().observe((val) -> {
					String value = String.valueOf(val);
					this.atlas = value.substring(0, value.indexOf("#"));
					this.sprite = value.substring(value.indexOf("#") + 1);
				});
			} else {
				this.atlas = n.substring(0, n.indexOf("#"));
				this.sprite = n.substring(n.indexOf("#") + 1);
			}
		}

		if (node.has("nineslice_size")) {
			JsonNode n = node.get("nineslice_size");

			if (n.isNumber()) {
				this.nx0 = n.asInt();
				this.ny0 = n.asInt();
				this.nx1 = n.asInt();
				this.ny1 = n.asInt();
			} else {
				this.nx0 = n.get(0).asInt();
				this.ny0 = n.get(1).asInt();
				this.nx1 = n.get(2).asInt();
				this.ny1 = n.get(3).asInt();
			}
		}
	}

	@Override
	public void render() {
		if (!this.visible) return;

		if (this.nx0 != -1) {
			this.client.renderer.renderSpriteNineslice(this.atlas, this.sprite, this.offsetX, this.offsetY, this.width, this.height, this.nx0, this.ny0, this.nx1, this.ny1);
		} else {
			this.client.renderer.renderSprite(this.atlas, this.sprite, this.offsetX, this.offsetY, this.width, this.height, 0);
		}

		for (UIElement element : this.controls) {
			element.render();
		}
	}
}
