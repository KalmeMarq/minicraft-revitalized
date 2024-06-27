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
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.kalmemarq.minicraft.client.Client;
import me.kalmemarq.minicraft.client.menu.Menu;
import me.kalmemarq.minicraft.client.util.IOUtils;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class UIElement {
	protected Client client;
	protected Anchor anchorFrom = Anchor.CENTER;
	protected Anchor anchorTo = Anchor.CENTER;
	protected int offsetX;
	protected int offsetY;
	protected int width;
	protected int height;
	protected Map<String, Observable<?>> propertyBag = new HashMap<>();
	protected boolean visible = true;
	public List<UIElement> controls = new ArrayList<>();
	private final List<ButtonMapping> mappings = new ArrayList<>();

	public void init(Client client, Map<String, Observable<?>> menuBindingsMap, JsonNode node) {
		this.client = client;

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

		if (node.has("bindings") && node.get("bindings").isArray()) {
			JsonNode arrayNode = node.get("bindings");

			for (JsonNode n : arrayNode) {
				String m = n.get("binding_name").asText();

				if (n.has("binding_name_override")) {
					Observable<?> mbM = menuBindingsMap.get(m);
					if (mbM != null) {
						Observable<?> b = Observable.of(mbM.get());
						mbM.cast().observe((val) -> b.cast().set(val));
						this.propertyBag.put(n.get("binding_name_override").asText(), b);
					}
				} else {
					Observable<?> mbM = menuBindingsMap.get(m);
					if (mbM != null) {
						Observable<?> b = Observable.of(mbM.get());
						mbM.cast().observe((val) -> b.cast().set(val));
						this.propertyBag.put(m, b);
					}
				}
			}
		}

		if (this.propertyBag.containsKey("#visible")) {
			this.visible = Boolean.valueOf(String.valueOf(this.propertyBag.get("#visible").get()));
			this.propertyBag.get("#visible").<Boolean>cast().observe((val) -> this.visible = val);
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

	public void onKeyPress(int key, Map<String, Runnable> buttonEvents) {
		String buttonId = switch (key) {
			case GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_SPACE -> "button.menu_cancel";
			default -> Menu.keyNames.getOrDefault(key, "button.key_" + key);
		};

		for (ButtonMapping mapping : this.mappings) {
			if (mapping.type.equals("global") && mapping.from.equals(buttonId)) {
				Runnable runnable = buttonEvents.get(mapping.to);
				if (runnable != null) runnable.run();
			}
		}

		for (UIElement element : this.controls) {
			element.onKeyPress(key, buttonEvents);
		}
	}

	public void render() {
		if (!this.visible) return;

		for (UIElement element : this.controls) {
			element.render();
		}
	}

	public static UIElement load(Client client, Map<String, Observable<?>> menuBindingsMap, String name, JsonNode node) {
		return loadControl(client, menuBindingsMap, node.get(name), node);
	}

	private static UIElement loadControl(Client client, Map<String, Observable<?>> menuBindingsMap, JsonNode node, JsonNode root) {
		String type = node.get("type").asText();

		UIElement element = switch (type) {
			case "label" -> new UILabel();
			case "image" -> new UIImage();
			case "selection_stack" -> new UISelectionStack();
			case "gradient" -> new UIGradient();
			default -> new UIElement();
		};

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
							nn.set(km.getKey(), km.getValue());
						}

						for (Iterator<Map.Entry<String, JsonNode>> iter = n0.getValue().fields(); iter.hasNext(); ) {
							var km = iter.next();
							nn.set(km.getKey(), km.getValue());
						}

						element.controls.add(loadControl(client,  menuBindingsMap, nn, root));
					} else {
						element.controls.add(loadControl(client,  menuBindingsMap, n0.getValue(), root));
					}

					break;
				}
			}
		}
		element.init(client,  menuBindingsMap, node);
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

	public static class Observable<T> {

		protected T value;
		protected final List<Consumer<T>> observers;

		protected Observable(T initial) {
			this.value = initial;
			this.observers = new ArrayList<>();
		}

		public static <T> Observable<T> of(T initial) {
			return new Observable<>(initial);
		}

		public T get() {
			return this.value;
		}

		public void observe(Consumer<T> observer) {
			this.observers.add(observer);
		}

		@SuppressWarnings("unchecked")
		public <C> Observable<C> cast() {
			return (Observable<C>) this;
		}

		public void set(T newValue) {
			var oldValue = this.value;
			this.value = newValue;

			if (!Objects.equals(this.value, oldValue)) {
				this.notifyObservers(newValue);
			}
		}

		protected void notifyObservers(T value) {
			for (var observer : this.observers) {
				observer.accept(value);
			}
		}
	}

	public record ButtonMapping(String from, String to, String type) {
	}
}
