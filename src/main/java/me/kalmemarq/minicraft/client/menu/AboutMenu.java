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

public class AboutMenu extends Menu {
    private final Menu parent;
    private final boolean isInGame;

    public AboutMenu(Menu parent) {
        this.parent = parent;
        this.isInGame = parent instanceof WorldMenu;
    }

    @Override
    public void keyPressed(int key) {
        if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_C || key == GLFW.GLFW_KEY_SPACE) {
            this.client.setMenu(this.parent);
        }
    }

    public void render() {
        if (this.isInGame) {
            this.client.renderer.renderSpriteNineslice("ui", "frame.png", 8, 4, this.getWidth() - 16, 16, 8, 8, 8, 8);
            this.client.renderer.renderSpriteNineslice("ui", "frame.png", 0, 20, this.getWidth(), this.getHeight() - 20, 8, 8, 8, 8);
        }

        this.client.font.draw("About Minicraft", 2 * 8 + 4, 8, 0xFFFFFF);
        this.client.font.drawWithMaxWidth("Minicraft was made by Markus Persson for the 22'nd ludum dare competition in december 2011.\nIt is dedicated to my father. <3", 4, 4 * 8, 0x808080, 20);
    }
}
