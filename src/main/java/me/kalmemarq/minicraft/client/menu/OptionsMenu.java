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

import me.kalmemarq.minicraft.client.util.Translation;
import org.lwjgl.glfw.GLFW;

public class OptionsMenu extends Menu {
	private int selected = 0;
	final boolean isInGame;
	private String[] options = {
		"minicraft.menu.language",
		"",
		"",
		""
	};
	private final Menu parent;

	public OptionsMenu(Menu parent) {
		this.parent = parent;
		this.isInGame = parent instanceof WorldMenu;
	}

	@Override
	public void keyPressed(int key) {
		if (key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_UP) {
			this.selected--;
			this.client.soundManager.play("/sounds/test.wav", 1.0f, 1.0f);
		}
		if (key == GLFW.GLFW_KEY_S || key == GLFW.GLFW_KEY_DOWN) {
			this.selected++;
			this.client.soundManager.play("/sounds/test.wav", 1.0f, 1.0f);
		}

		int len = this.options.length;
		if (this.selected < 0) this.selected += len;
		if (this.selected >= len) this.selected -= len;

		if (key == GLFW.GLFW_KEY_ESCAPE) {
			this.client.setMenu(this.parent);
			this.client.soundManager.play("/sounds/craft.wav", 1.0f, 1.0f);
		}

		if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_C || key == GLFW.GLFW_KEY_SPACE) {
			boolean clicked = true;

			if (this.selected == 0) this.client.setMenu(new LanguageMenu(this));
			else if (this.selected == 2) {
				this.client.vsync = !this.client.vsync;
				this.client.window.setVsync(this.client.vsync);
			} else if (this.selected == 3) {
				this.client.sound = !this.client.sound;
			} else {
				clicked = false;
			}

			if (clicked) {
				this.client.soundManager.play("/sounds/craft.wav", 1.0f, 1.0f);
			}
		}
	}

	@Override
	public void render() {
		if (this.isInGame) this.client.renderer.renderSpriteNineslice("textures/ui", "frame.png", 8, 48, this.getWidth() - 16, (this.options.length + 2) * 8 + 16, 8, 8, 8, 8);

		for (int i = 0; i < this.options.length; i++) {
			String msg = Translation.translate(this.options[i]);

			if (i == 1) {
				msg = "User: " + this.client.username;
			} else if (i == 2) {
				msg = "V-Sync: " + (this.client.vsync ? "ON" : "OFF");
			} else if (i == 3) {
				msg = "Sound: " + (this.client.sound ? "ON" : "OFF");
			}

			int col = 0x808080;
			if (i == this.selected) {
				msg = "> " + msg + " <";
				col = 0xFFFFFF;
			}
			this.client.font.draw(msg, (this.getWidth() - msg.length() * 8) / 2, 8 * 8 + i * 12 - 8, col);
		}
	}
}
