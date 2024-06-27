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

import java.util.HashMap;

public class AboutMenu extends Menu {
    private final Menu parent;
    private boolean isInGame;

    public AboutMenu(Menu parent) {
        this.parent = parent;
        this.isInGame = parent instanceof WorldMenu;
    }

	@Override
	public void init(Client client) {
		super.init(client);
		this.bindingsMap = new HashMap<>();
		this.buttonEvents = new HashMap<>();
		this.element = new UIElement();
		this.bindingsMap.put("#is_in_game", UIElement.Observable.of(this.isInGame));
		this.bindingsMap.put("#title", UIElement.Observable.of("minicraft.menu.about.title"));
		this.bindingsMap.put("#message_body", UIElement.Observable.of("minicraft.menu.about.message"));
		this.bindingsMap.put("#message_color", UIElement.Observable.of(new int[] { 128, 128, 128 }));
		this.buttonEvents.put("button.menu_exit", () -> {
			this.client.setMenu(this.parent);
			this.client.soundManager.play("/sounds/craft.wav", 1.0f, 1.0f);
		});
		this.buttonEvents.put("button.test_title_update", () -> this.bindingsMap.get("#title").<String>cast().set("Sup " + (System.currentTimeMillis() % 1000)));
		this.buttonEvents.put("button.test_is_in_game_toggle", () -> {
			this.isInGame = !this.isInGame;
			this.bindingsMap.get("#is_in_game").<Boolean>cast().set(this.isInGame);
		});
		this.buttonEvents.put("button.test_color_to_red", () -> this.bindingsMap.get("#message_color").<int[]>cast().set(new int[] { 255, 0, 0 }));
		this.loadScreen("/test/ui/about_screen.json", "about_screen");
	}

	@Override
    public void keyPressed(int key) {
		this.element.onKeyPress(key, this.buttonEvents);
    }

    public void render() {
		if (this.element.dirty) {
			this.element.relayout(this.getWidth(), this.getHeight(), 0, 0);
		}
		this.element.render();
    }
}
