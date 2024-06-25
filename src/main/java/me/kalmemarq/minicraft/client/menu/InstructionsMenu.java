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

public class InstructionsMenu extends Menu {
    private final Menu parent;
    private final boolean isInGame;

    public InstructionsMenu(Menu parent) {
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

        this.client.font.draw("How to Play", 4 * 8 + 4, 8, 0xFFFFFF);
        this.client.font.drawWithMaxWidth("Move your character with the arrow keys press C to attack and X to open the inventory and to use items. Select an item in the inventory to equip it. Kill the air wizard to win the game!", 4, 3 * 8, 0x808080, 19);
    }
}
