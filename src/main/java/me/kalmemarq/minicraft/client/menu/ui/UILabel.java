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
	public void init(Client client, Map<String, Object> menuBindings, JsonNode node) {
		super.init(client, menuBindings, node);

		if (node.has("text")) {
			String n = node.get("text").textValue();

			if (n.startsWith("#") && this.propertyBag.containsKey(n)) {
				this.text = String.valueOf(this.propertyBag.get(n));
			} else {
				this.text = n;
			}
		}

		if (node.has("wrap") && node.get("wrap").isNumber()) {
			this.wrap = node.get("wrap").asInt();
		}

		if (node.has("color") && node.get("color").isArray()) {
			JsonNode n = node.get("color");
			this.color = n.get(0).asInt() << 16 | n.get(1).asInt() << 8 | n.get(2).asInt();
		}

		this.width = Translation.translate(this.text).length() * 8;
		this.height = 8;
	}

	@Override
	public void render() {
		if (!this.visible) return;

		if (this.wrap > 0) {
			this.client.font.drawWithMaxWidth(Translation.translate(this.text), this.offsetX, this.offsetY, this.color, this.wrap);
		} else {
			this.client.font.draw(Translation.translate(this.text), this.offsetX, this.offsetY, this.color);
		}

		for (UIElement element : this.controls) {
			element.render();
		}
	}
}
