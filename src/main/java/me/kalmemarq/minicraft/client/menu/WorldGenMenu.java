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
import org.lwjgl.glfw.GLFW;

public class WorldGenMenu extends Menu {
    private int selected;
    private final String[] options = {
            "",
            "Create World"
    };
    private final Menu parent;

    public WorldGenMenu(Menu parent) {
        this.parent = parent;
    }

    @Override
    public void init(Client client) {
        super.init(client);
//        this.client.setMenu(null);
//        this.client.connectLocal();
    }

    @Override
    public void keyPressed(int key) {
        if (key == GLFW.GLFW_KEY_UP) this.selected--;
        if (key == GLFW.GLFW_KEY_DOWN) this.selected++;

        int len = this.options.length;
        if (this.selected < 0) this.selected += len;
        if (this.selected >= len) this.selected -= len;

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setMenu(this.parent);
        }

        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_C || key == GLFW.GLFW_KEY_SPACE) {
            if (this.selected == 1) {
                this.client.setMenu(null);
                this.client.connectLocal();
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
        this.client.font.draw("World Gen Options", (this.getWidth() - 17 * 8) / 2, 8, 0xFFFFFF);

        for (int i = 0; i < this.options.length; i++) {
            String msg = this.options[i];

            if (i == 0) {
                msg = "Name: " + msg;
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
