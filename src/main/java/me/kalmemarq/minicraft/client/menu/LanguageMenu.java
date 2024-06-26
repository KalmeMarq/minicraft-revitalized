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
import me.kalmemarq.minicraft.client.InputHandler;
import me.kalmemarq.minicraft.client.util.Translation;
import org.lwjgl.glfw.GLFW;

public class LanguageMenu extends Menu {
    private final Menu parent;
    private final boolean isInGame;
    private int selected;

    public LanguageMenu(Menu parent) {
        this.parent = parent;
        this.isInGame = parent instanceof WorldMenu || (parent instanceof OptionsMenu optionsMenu && optionsMenu.isInGame);
    }

    @Override
    public void init(Client client) {
        super.init(client);
        int i = 0;
        for (String code : Translation.metadata.keySet()) {
            if (Translation.currentCode.equals(code)) {
                this.selected = i;
                break;
            }
            ++i;
        }
    }

    @Override
    public void keyPressed(int key) {
        if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_C || key == GLFW.GLFW_KEY_SPACE) {
            this.client.setMenu(this.parent);
			this.client.soundManager.play("/sounds/craft.wav", 1.0f, 1.0f);
        }

		if (key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_UP) {
			this.selected--;
			this.client.soundManager.play("/sounds/test.wav", 1.0f, 1.0f);
		}
		if (key == GLFW.GLFW_KEY_S || key == GLFW.GLFW_KEY_DOWN) {
			this.selected++;
			this.client.soundManager.play("/sounds/test.wav", 1.0f, 1.0f);
		}

        int len = Translation.metadata.size();
        if (this.selected < 0) this.selected += len;
        if (this.selected >= len) this.selected -= len;

        if (InputHandler.Key.ATTACK.clicked) {
            this.client.setMenu(this.parent);
        }

        if (key == GLFW.GLFW_KEY_X) {
            int i = 0;
            for (String code : Translation.metadata.keySet()) {
                if (this.selected == i) {
                    Translation.load(code);
                    this.client.setMenu(this.parent);
					this.client.soundManager.play("/sounds/craft.wav", 1.0f, 1.0f);
                    break;
                }
                ++i;
            }
        }
    }

    @Override
    public void render() {
        if (this.isInGame) {
            this.client.renderer.renderSpriteNineslice("textures/ui", "frame.png", 8, 4, this.getWidth() - 16, 16, 8, 8, 8, 8);
            this.client.renderer.renderSpriteNineslice("textures/ui", "frame.png", 0, 20, this.getWidth(), this.getHeight() - 20, 8, 8, 8, 8);
        }

        this.client.font.draw(Translation.translate("minicraft.menu.language"), this.getWidth() / 2, 8, 0xFFFFFF, 0, Font.TextAlignment.CENTER);

        int i = 0;
        for (Translation.LanguageMetadata metadata : Translation.metadata.values()) {
            int col = 0x808080;
            String msg = metadata.name() + " (" + metadata.region() + ")";
            if (i == this.selected) {
                msg = "> " + msg + " <";
                col = 0xFFFFFF;
            }
            this.client.font.draw(msg, this.getWidth() / 2, (8 + i) * 8, col, 0, Font.TextAlignment.CENTER);
            ++i;
        }
    }
}
