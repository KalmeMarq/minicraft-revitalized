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
			this.client.soundManager.play("/sounds/craft.wav", 1.0f, 1.0f);
        }
    }

    public void render() {
        if (this.isInGame) {
            this.client.renderer.renderSpriteNineslice("textures/ui", "frame.png", 8, 4, this.getWidth() - 16, 16, 8, 8, 8, 8);
            this.client.renderer.renderSpriteNineslice("textures/ui", "frame.png", 0, 20, this.getWidth(), this.getHeight() - 20, 8, 8, 8, 8);
        }

        this.client.font.draw(Translation.translate("minicraft.menu.how_to_play"), 4 * 8 + 4, 8, 0xFFFFFF);
        this.client.font.drawWithMaxWidth(Translation.translate("minicraft.menu.how_to_play.message"), 4, 3 * 8, 0x808080, 19);
    }
}
