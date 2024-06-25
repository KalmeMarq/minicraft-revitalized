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

import org.lwjgl.glfw.GLFW;

public class SingleplayerMenu extends Menu {
    private int selected = 0;
    private final String[] options = {"Load World", "Create World"};
    private final Menu parent;

    public SingleplayerMenu(Menu parent) {
        this.parent = parent;
    }

    @Override
    public void keyPressed(int key) {
        if (key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_UP) this.selected--;
        if (key == GLFW.GLFW_KEY_S || key == GLFW.GLFW_KEY_DOWN) this.selected++;

        int len = this.options.length;
        if (this.selected < 0) this.selected += len;
        if (this.selected >= len) this.selected -= len;

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setMenu(this.parent);
        }

        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_C || key == GLFW.GLFW_KEY_SPACE) {
            if (this.selected == 0) this.client.setMenu(new WorldSelectMenu(this));
            if (this.selected == 1)  this.client.setMenu(new WorldGenMenu(this));
        }
    }

    @Override
    public void render() {
        for (int i = 0; i < this.options.length; i++) {
            String msg = this.options[i];
            int col = 0x808080;
            if (i == this.selected) {
                msg = "> " + msg + " <";
                col = 0xFFFFFF;
            }
            this.client.font.draw(msg, (this.getWidth() - msg.length() * 8) / 2, 8 * 8 + i * 12 - 8, col);
        }
    }
}
