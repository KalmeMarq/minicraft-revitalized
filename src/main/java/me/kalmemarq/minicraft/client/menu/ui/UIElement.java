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
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class UIElement {
	protected UIElement parent;
	protected int childIndex;
	protected Client client;
	protected Anchor anchorFrom = Anchor.TOP_LEFT;
	protected Anchor anchorTo = Anchor.TOP_LEFT;
	protected int offsetX;
	protected int offsetY;
	protected int width;
	protected int height;
	protected Map<String, Observable<?>> propertyBag = new HashMap<>();
	protected boolean visible = true;
	public List<UIElement> controls = new ArrayList<>();
	protected final List<ButtonMapping> mappings = new ArrayList<>();

	protected SizingFunction defaultWidthSupplier = (w, h, dW, dH) -> w;
	protected SizingFunction defaultHeightSupplier = (w, h, dW, dH) -> h;

	protected SizingFunction widthSupplier = this.defaultWidthSupplier;
	protected SizingFunction heightSupplier = this.defaultHeightSupplier;
	protected SizingFunction xSupplier = (w, h, dW, dH) -> 0f;
	protected SizingFunction ySupplier = (w, h, dW, dH) -> 0f;
	protected int zIndex;
	public boolean root;
	private List<UIElement> sorted = new ArrayList<>();

	public boolean dirty;

	protected int debug = 0x00000000;

	public void markDirty() {
		if (this.parent != null) this.parent.markDirty();
		else {
			this.dirty = true;
		}
	}

	public void init(Client client, Map<String, Observable<?>> menuBindingsMap, JsonNode node) {
		this.client = client;

		if (node.has("debug")) {
			String debugColor = node.get("debug").asText();
			this.debug = switch (debugColor) {
				case "white" -> 0xFFFFFFFF;
				case "red" -> 0xFFFF0000;
				case "green" -> 0xFF00FF00;
				case "blue" -> 0xFF0000FF;
				case "yellow" -> 0xFFFFFF00;
				case "magenta" -> 0xFFFF00FF;
				case "cyan" -> 0xFF00FFFF;
				default -> 0x00000000;
			};
		}

		if (node.has("anchor_from")) {
			this.anchorFrom = switch (node.get("anchor_from").asText()) {
				case "top_middle" -> Anchor.TOP_MIDDLE;
				case "top_right" -> Anchor.TOP_RIGHT;
				case "left_middle" -> Anchor.LEFT_MIDDLE;
				case "center" -> Anchor.CENTER;
				case "right_middle" -> Anchor.RIGHT_MIDDLE;
				case "bottom_left" -> Anchor.BOTTOM_LEFT;
				case "bottom_middle" -> Anchor.BOTTOM_MIDDLE;
				case "bottom_right" -> Anchor.BOTTOM_RIGHT;
				default -> Anchor.TOP_LEFT;
			};
		}

		if (node.has("anchor_to")) {
			this.anchorTo = switch (node.get("anchor_to").asText()) {
				case "top_middle" -> Anchor.TOP_MIDDLE;
				case "top_right" -> Anchor.TOP_RIGHT;
				case "left_middle" -> Anchor.LEFT_MIDDLE;
				case "center" -> Anchor.CENTER;
				case "right_middle" -> Anchor.RIGHT_MIDDLE;
				case "bottom_left" -> Anchor.BOTTOM_LEFT;
				case "bottom_middle" -> Anchor.BOTTOM_MIDDLE;
				case "bottom_right" -> Anchor.BOTTOM_RIGHT;
				default -> Anchor.TOP_LEFT;
			};
		}

		if (node.has("layer")) {
			this.zIndex = node.get("layer").asInt(0);
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

		if (node.has("size") && node.get("size").isArray()) {
			JsonNode n = node.get("size");

			if (n.get(0).isTextual()) {
				this.widthSupplier = parseSizingInDumbWay(n.get(0).textValue(), true);
			} else {
				int val = n.get(0).asInt();
				this.widthSupplier = (w, h, dW, dH) -> val;
			}

			if (n.get(1).isTextual()) {
				this.heightSupplier = parseSizingInDumbWay(n.get(1).textValue(), false);
			} else {
				int val = n.get(1).asInt();
				this.heightSupplier = (w, h, dW, dH) -> val;
			}
		}

		if (node.has("offset") && node.get("offset").isArray()) {
			JsonNode n = node.get("offset");

			if (n.get(0).isTextual()) {
				this.xSupplier = parseSizingInDumbWay(n.get(0).textValue(), true);
			} else {
				int val = n.get(0).asInt();
				this.xSupplier = (w, h, dW, dH) -> val;
			}

			if (n.get(1).isTextual()) {
				this.ySupplier = parseSizingInDumbWay(n.get(1).textValue(), false);
			} else {
				int val = n.get(1).asInt();
				this.ySupplier = (w, h, dW, dH) -> val;
			}
		}
	}

	public void relayout(int width, int height, int x, int y) {
		int dW = (int) this.defaultWidthSupplier.apply(width, height, 0, 0);
		int dH = (int) this.defaultHeightSupplier.apply(width, height, 0, 0);
		this.width = (int) this.widthSupplier.apply(width, height, dW, dH);
		this.height = (int) this.heightSupplier.apply(width, height, dW, dH);

		this.offsetX = (int) this.xSupplier.apply(width, height, 0, 0) + x;
		this.offsetY = (int) this.ySupplier.apply(width, height, 0, 0) + y;

		if (this.anchorFrom == Anchor.TOP_MIDDLE || this.anchorFrom == Anchor.CENTER || this.anchorFrom == Anchor.BOTTOM_MIDDLE) {
			this.offsetX -= this.width / 2;
		}

		if (this.anchorFrom == Anchor.TOP_RIGHT || this.anchorFrom == Anchor.RIGHT_MIDDLE || this.anchorFrom == Anchor.BOTTOM_RIGHT) {
			this.offsetX -= this.width;
		}

		if (this.anchorFrom == Anchor.LEFT_MIDDLE || this.anchorFrom == Anchor.CENTER || this.anchorFrom == Anchor.RIGHT_MIDDLE) {
			this.offsetY -= this.height / 2;
		}

		if (this.anchorFrom == Anchor.BOTTOM_LEFT || this.anchorFrom == Anchor.BOTTOM_MIDDLE || this.anchorFrom == Anchor.BOTTOM_RIGHT) {
			this.offsetY -= this.height;
		}

		if (this.anchorTo == Anchor.TOP_MIDDLE || this.anchorTo == Anchor.CENTER || this.anchorTo == Anchor.BOTTOM_MIDDLE) {
			this.offsetX += width / 2;
		}

		if (this.anchorTo == Anchor.TOP_RIGHT || this.anchorTo == Anchor.RIGHT_MIDDLE || this.anchorTo == Anchor.BOTTOM_RIGHT) {
			this.offsetX += width;
		}

		if (this.anchorTo == Anchor.LEFT_MIDDLE || this.anchorTo == Anchor.CENTER || this.anchorTo == Anchor.RIGHT_MIDDLE) {
			this.offsetY += height / 2;
		}

		if (this.anchorTo == Anchor.BOTTOM_LEFT || this.anchorTo == Anchor.BOTTOM_MIDDLE || this.anchorTo == Anchor.BOTTOM_RIGHT) {
			this.offsetY += height;
		}

		for (UIElement element : this.controls) {
			element.relayout(this.width, this.height, this.offsetX, this.offsetY);
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

	protected void renderDebug() {
		float a = (this.debug >> 24 & 0xFF) / 255.0f;
		float r = (this.debug >> 16 & 0xFF) / 255.0f;
		float g = (this.debug >> 8 & 0xFF) / 255.0f;
		float b = (this.debug & 0xFF) / 255.0f;

		this.client.textureManager.bind("White");
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glColor4f(r, g, b, a);
		GL11.glVertex3f(this.offsetX, this.offsetY, 0);
		GL11.glTexCoord2f(0, 1);
		GL11.glColor4f(r, g, b, a);
		GL11.glVertex3f(this.offsetX, this.offsetY + this.height, 0);
		GL11.glTexCoord2f(1, 1);
		GL11.glColor4f(r, g, b, a);
		GL11.glVertex3f(this.offsetX + this.width, this.offsetY + this.height, 0);
		GL11.glTexCoord2f(1, 0);
		GL11.glColor4f(r, g, b, a);
		GL11.glVertex3f(this.offsetX + this.width, this.offsetY, 0);
		GL11.glEnd();
		GL11.glColor4f(1f, 1f, 1f, 1f);
	}

	public int getAbsoluteZIndex() {
		return this.zIndex + (this.parent == null ? (this.childIndex + 1) : (this.childIndex + 1) + this.parent.getAbsoluteZIndex());
	}

	private void gather(Consumer<UIElement> consumer) {
		if (!this.visible) return;

		if (!this.root) consumer.accept(this);

		for (UIElement element : this.controls) {
			element.gather(consumer);
		}
	}

	public void render() {
		if (!this.visible) return;

		if (this.root) {
			this.sorted.clear();
			this.gather(this.sorted::add);
			this.sorted.sort((a, b) -> b.getAbsoluteZIndex() - a.getAbsoluteZIndex());
		}

		if (this.debug != 0) {
			this.renderDebug();
		}

		if (this.root) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
			for (UIElement element : this.sorted) {
				element.render();
			}
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}
	}

	public static UIElement load(Client client, Map<String, Observable<?>> menuBindingsMap, String name, JsonNode node) {
		return loadControl(client, menuBindingsMap, node.get(name), node);
	}

	private static UIElement loadControl(Client client, Map<String, Observable<?>> menuBindingsMap, JsonNode node, JsonNode root) {
		String type = node.get("type").asText();

		UIElement element = switch (type) {
			case "screen" -> new UIScreen();
			case "label" -> new UILabel();
			case "image" -> new UIImage();
			case "selection_stack" -> new UISelectionStack();
			case "gradient" -> new UIGradient();
			case "panel", "none" -> new UIElement();
			default -> throw new RuntimeException("Invalid type" + type);
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

						UIElement el = loadControl(client,  menuBindingsMap, nn, root);
						el.parent = element;
						el.childIndex = element.controls.size();
						element.controls.add(el);
					} else {
						UIElement el = loadControl(client,  menuBindingsMap, n0.getValue(), root);
						el.parent = element;
						el.childIndex = element.controls.size();
						element.controls.add(el);
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

	public static SizingFunction parseSizingInDumbWay(String content, boolean isWidth) {
		SizingFunction sup = (w, h, dW, dH) -> 0f;

		char op = 0;
		for (int i = 0; i < content.length(); ++i) {
			char chr = content.charAt(i);

			if (chr >= 'a' && chr <= 'z') {
				int o = i;

				do {
					++i;
					if (i >= content.length()) break;
					chr = content.charAt(i);
				} while (chr >= 'a' && chr <= 'z');

				if (content.substring(o, i).equals("default")) {
					if (isWidth) {
						sup = (w, h, dW, dH) -> dW;
					} else {
						sup = (w, h, dW, dH) -> dH;
					}
				}
			} else if (chr >= '0' && chr <= '9') {
				int o = i;

				do {
					++i;
					if (i >= content.length()) break;
					chr = content.charAt(i);
				} while (chr >= '0' && chr <= '9');

				if (chr == '%') {
					float v = Float.parseFloat(content.substring(o, i)) / 100.0f;
					if (op == 0)  sup = (w, h, dW, dH) -> (isWidth ? w : h) * v;
					else {
						SizingFunction finalSup = sup;
						if (op == '+') sup = (w, h, dW, dH) -> finalSup.apply(w, h, dW, dH) + ((isWidth ? w : h) + v);
						else if (op == '-') sup = (w, h, dW, dH) -> finalSup.apply(w, h, dW, dH) + ((isWidth ? w : h) - v);
						else if (op == '*') sup = (w, h, dW, dH) -> finalSup.apply(w, h, dW, dH) + ((isWidth ? w : h) * v);
						else sup = (w, h, dW, dH) -> finalSup.apply(w, h, dW, dH) + ((isWidth ? w : h) / v);
					}
				} else if (chr == 'p') {
					float v = Float.parseFloat(content.substring(o, i));
					if (op == 0) sup = (w, h, dW, dH) -> v;
					else {
						SizingFunction finalSup = sup;
						if (op == '+') sup = (w, h, dW, dH) -> finalSup.apply(w, h, dW, dH) + v;
						else if (op == '-') sup = (w, h, dW, dH) -> finalSup.apply(w, h, dW, dH) - v;
						else if (op == '*') sup = (w, h, dW, dH) -> finalSup.apply(w, h, dW, dH) * v;
						else sup = (w, h, dW, dH) -> finalSup.apply(w, h, dW, dH) / v;
					}
					++i;
				}
			} else if (chr == '+' || chr == '-' || chr == '/' || chr == '*')  {
				op = chr;
			}
		}

		return sup;
	}

	@FunctionalInterface
	public interface SizingFunction {
		float apply(int width, int height, int defaultWidth, int defaultHeight);
	}
}
