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

package me.kalmemarq.minicraft.client.menu;

import me.kalmemarq.minicraft.client.Client;
import me.kalmemarq.minicraft.client.menu.ui.UIElement;
import me.kalmemarq.minicraft.client.util.IOUtils;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Menu {
    protected Client client;
	protected Map<String, UIElement.Observable<?>> bindingsMap;
	protected Map<String, Runnable> buttonEvents;
	protected UIElement element;

    public void init(Client client) {
        this.client = client;
    }

    public int getWidth() {
        return this.client.window.getWidth() / 3;
    }

    public int getHeight() {
        return this.client.window.getHeight() / 3;
    }

    public void keyPressed(int key) {
    }

    public void charTyped(int codepoint) {
    }

    public void tick() {
    }

    public void render() {
    }

	protected void loadScreen(String path, String name) {
		try {
			this.element = UIElement.load(this.client, this.bindingsMap, name, IOUtils.JSON_OBJECT_MAPPER.readTree(AboutMenu.class.getResourceAsStream(path)));
			this.element.root = true;
			this.element.relayout(this.getWidth(), this.getHeight(), 0, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<Integer, String> keyNames = new HashMap<>();

	static {
		for (Field field : GLFW.class.getFields()) {
			if (!field.getName().startsWith("GLFW_KEY_")) continue;
			try {
				keyNames.put(field.getInt(null), "button." + field.getName().substring(5).toLowerCase());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public void resize() {
		if (this.element != null) {
			this.element.relayout(this.getWidth(), this.getHeight(), 0, 0);
		}
	}
}
