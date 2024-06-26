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
	public void init(Client client, Map<String, Object> menuBindings, JsonNode node) {
		super.init(client, menuBindings, node);

		if (node.has("texture")) {
			String n = node.get("texture").textValue();
			this.atlas = n.substring(0, n.indexOf("#"));
			this.sprite = n.substring(n.indexOf("#") + 1);
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
