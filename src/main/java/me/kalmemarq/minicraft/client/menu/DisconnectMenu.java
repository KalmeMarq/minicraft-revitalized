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
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public class DisconnectMenu extends Menu {
	private final String reason;

	public DisconnectMenu(String reason) {
		this.reason = reason;
	}

	@Override
	public void init(Client client) {
		super.init(client);
		this.bindings = new HashMap<>();
		this.element = new UIElement();
		this.bindings.put("#disconnect_reason", this.reason);
		this.loadScreen("/ui/disconnect_screen.json", "disconnect_screen");
	}

	@Override
	public void keyPressed(int key) {
		if (key == GLFW.GLFW_KEY_ESCAPE) {
			this.client.setMenu(new TitleMenu());
			this.client.soundManager.play("/sounds/craft.wav", 1.0f, 1.0f);
		}
	}

	@Override
	public void render() {
		this.element.render();
	}
}
