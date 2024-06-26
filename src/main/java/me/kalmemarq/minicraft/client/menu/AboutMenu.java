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

public class AboutMenu extends Menu {
    private final Menu parent;
    private final boolean isInGame;

    public AboutMenu(Menu parent) {
        this.parent = parent;
        this.isInGame = parent instanceof WorldMenu;
    }

	@Override
	public void init(Client client) {
		super.init(client);
		this.bindings = new HashMap<>();
		this.element = new UIElement();
		this.bindings.put("#is_in_game", this.isInGame);
		this.bindings.put("#title", "minicraft.menu.about.title");
		this.bindings.put("#message_body", "minicraft.menu.about.message");
		this.loadScreen("/ui/about_screen.json", "about_screen");
	}

	@Override
    public void keyPressed(int key) {
        if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_C || key == GLFW.GLFW_KEY_SPACE) {
            this.client.setMenu(this.parent);
			this.client.soundManager.play("/sounds/craft.wav", 1.0f, 1.0f);
        }
    }

    public void render() {
		this.element.render();
    }
}
