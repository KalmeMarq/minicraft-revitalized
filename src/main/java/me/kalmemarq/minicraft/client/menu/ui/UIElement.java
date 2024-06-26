package me.kalmemarq.minicraft.client.menu.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.kalmemarq.minicraft.client.Client;
import me.kalmemarq.minicraft.client.util.IOUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UIElement {
	protected Client client;
	protected Anchor anchorFrom = Anchor.CENTER;
	protected Anchor anchorTo = Anchor.CENTER;
	protected int offsetX;
	protected int offsetY;
	protected int width;
	protected int height;
	protected Map<String, Object> propertyBag = new HashMap<>();
	protected boolean visible = true;
	public List<UIElement> controls = new ArrayList<>();

	public void init(Client client, Map<String, Object> menuBindings, JsonNode node) {
		this.client = client;

		if (node.has("bindings") && node.get("bindings").isArray()) {
			JsonNode arrayNode = node.get("bindings");

			for (JsonNode n : arrayNode) {
				String m = n.get("binding_name").asText();


				if (n.has("binding_name_override")) {
					this.propertyBag.put(n.get("binding_name_override").asText(), menuBindings.get(m));
				} else {
					this.propertyBag.put(m, menuBindings.get(m));
				}
			}
		}

		if (this.propertyBag.containsKey("#visible")) {
			this.visible = Boolean.valueOf(String.valueOf(this.propertyBag.get("#visible")));
		}

		if (node.has("offset") && node.get("offset").isArray()) {
			JsonNode n = node.get("offset");
			this.offsetX = n.get(0).asInt();
			this.offsetY = n.get(1).asInt();
		}

		if (node.has("size") && node.get("size").isArray()) {
			JsonNode n = node.get("size");
			this.width = n.get(0).asInt();
			this.height = n.get(1).asInt();
		}
	}

	public void render() {
		if (!this.visible) return;

		for (UIElement element : this.controls) {
			element.render();
		}
	}

	public static UIElement load(Client client, Map<String, Object> menuBindings, String name, JsonNode node) {
		return loadControl(client, menuBindings, node.get(name), node);
	}

	private static UIElement loadControl(Client client, Map<String, Object> menuBindings, JsonNode node, JsonNode root) {
		String type = node.get("type").asText();

		UIElement element = type.equals("label") ? new UILabel() : type.equals("image") ? new UIImage() : type.equals("selection_stack") ? new UISelectionStack() : new UIElement();
		if (node.has("controls")) {
			for (JsonNode n : node.get("controls")) {
				for (Iterator<Map.Entry<String, JsonNode>> it = n.fields(); it.hasNext(); ) {
					var n0 = it.next();

					String controlName = n0.getKey();

					if (controlName.contains("@")) {
						ObjectNode nn = IOUtils.JSON_OBJECT_MAPPER.createObjectNode();

						String superName = controlName.substring(controlName.indexOf("@") + 1);

						for (Iterator<Map.Entry<String, JsonNode>> iter = root.get(superName).fields(); iter.hasNext(); ) {
							var km = iter.next();
							nn.put(km.getKey(), km.getValue());
						}

						for (Iterator<Map.Entry<String, JsonNode>> iter = n0.getValue().fields(); iter.hasNext(); ) {
							var km = iter.next();
							nn.put(km.getKey(), km.getValue());
						}

						element.controls.add(loadControl(client, menuBindings, nn, root));
					} else {
						element.controls.add(loadControl(client, menuBindings, n0.getValue(), root));
					}

					break;
				}
			}
		}
		element.init(client, menuBindings, node);
		return element;
	}

	public enum Anchor {
		TOP_LEFT,
		TOP_MIDDLE,
		TOP_RIGHT,
		LEFT_MIDDLE,
		CENTER,
		RIGHT_MIDDLE,
		BOTTOM_LEFT,
		BOTTOM_MIDDLE,
		BOTTOM_RIGHT
	}
}
