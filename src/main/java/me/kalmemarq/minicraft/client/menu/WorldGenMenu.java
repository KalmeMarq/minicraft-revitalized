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
import me.kalmemarq.minicraft.client.util.Translation;
import org.lwjgl.glfw.GLFW;

public class WorldGenMenu extends Menu {
    private int selected;
    private final String[] options = {
            "",
            "minicraft.menu.create_world"
    };
    private final Menu parent;

    public WorldGenMenu(Menu parent) {
        this.parent = parent;
    }

    @Override
    public void keyPressed(int key) {
		if (key == GLFW.GLFW_KEY_UP) {
			this.selected--;
			this.client.soundManager.play("/sounds/test.wav", 1.0f, 1.0f);
		}
		if (key == GLFW.GLFW_KEY_DOWN) {
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
            if (this.selected == 1) {
                this.client.setMenu(null);
                this.client.connectLocal();
				this.client.soundManager.play("/sounds/craft.wav", 1.0f, 1.0f);
            }
        }

        if (this.selected == 0) {
            if (key == GLFW.GLFW_KEY_BACKSPACE && !this.options[this.selected].isEmpty()) {
                this.options[this.selected] = this.options[this.selected].substring(0, this.options[this.selected].length() - 1);
            }
        }
    }

    @Override
    public void charTyped(int codepoint) {
        if (this.selected == 0) {
            this.options[this.selected] += Character.toString(codepoint);
        }
    }


    @Override
    public void render() {
        this.client.font.draw(Translation.translate("minicraft.menu.world_gen_options"), this.getWidth() / 2, 8, 0xFFFFFF, 0, Font.TextAlignment.CENTER);

        for (int i = 0; i < this.options.length; i++) {
            String msg = this.options[i];

            if (i == 0) {
                msg = Translation.translate("minicraft.menu.name") + msg;
            } else {
				msg = Translation.translate(msg);
			}

            int col = 0x808080;
            if (i == this.selected) {
                msg = "> " + msg + " <";
                col = 0xFFFFFF;
            }
            this.client.font.draw(msg, (this.getWidth() - msg.length() * 8) / 2, 8 * 8 + (i * 20), col);
        }
    }
}
