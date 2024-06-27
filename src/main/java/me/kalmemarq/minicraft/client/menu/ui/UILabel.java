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
import me.kalmemarq.minicraft.client.util.Translation;

import java.util.Map;

public class UILabel extends UIElement {
	private String text;
	private int color = 0xFFFFFF;
	private int wrap = -1;

	@Override
	public void init(Client client, Map<String, Observable<?>> menuBindingsMap, JsonNode node) {
		super.init(client, menuBindingsMap, node);

		if (node.has("text")) {
			String n = node.get("text").textValue();

			if (n.startsWith("#") && this.propertyBag.containsKey(n)) {
				this.text = String.valueOf(this.propertyBag.get(n).get());
				this.propertyBag.get(n).cast().observe((val) -> {
					this.text = String.valueOf(val);
					this.markDirty();
				});
			} else {
				this.text = n;
			}
		}

		if (node.has("wrap") && node.get("wrap").isNumber()) {
			this.wrap = node.get("wrap").asInt();
		}

		if (node.has("color")) {
			JsonNode n = node.get("color");

			if (n.isTextual() &&  n.textValue().startsWith("#") && this.propertyBag.containsKey(n.textValue())) {
				int[] col = this.propertyBag.get(n.textValue()).<int[]>cast().get();
				this.color = col[0] << 16 | col[1] << 8 | col[2];

				this.propertyBag.get(n.textValue()).<int[]>cast().observe((val) -> {
					this.color = val[0] << 16 | val[1] << 8 | val[2];
				});
			} else if (n.isArray()) {
				this.color = n.get(0).asInt() << 16 | n.get(1).asInt() << 8 | n.get(2).asInt();
			}
		}

		this.width = Translation.translate(this.text).length() * 8;
		this.height = 8;

		if (this.widthSupplier == this.defaultWidthSupplier) {
			this.widthSupplier = (w, h, dW, dH) -> Translation.translate(this.text).length() * 8;
		}

		if (this.heightSupplier == this.defaultHeightSupplier) {
			this.heightSupplier = (w, h, dW, dH) -> Translation.translate(this.text).split("\n").length * 8;
		}
	}

	@Override
	public void render() {
		if (!this.visible) return;

		if (this.debug != 0) {
			this.renderDebug();
		}

		if (this.wrap > 0) {
			this.client.font.drawWithMaxWidth(Translation.translate(this.text), this.offsetX, this.offsetY, this.color, this.wrap);
		} else {
			this.client.font.draw(Translation.translate(this.text), this.offsetX, this.offsetY, this.color);
		}

		if (this.debug != 0) {
			this.renderDebug();
		}
	}
}
