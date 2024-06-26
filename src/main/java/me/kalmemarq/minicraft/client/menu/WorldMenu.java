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

public class WorldMenu extends Menu {
    private int selected = 0;
    private String[] options;
    private boolean[] optionsEnabled = {true, true, true, true, true};

    @Override
    public void init(Client client) {
        super.init(client);
        this.options = new String[]{
                "minicraft.menu.continue",
                "minicraft.menu.how_to_play",
                "minicraft.menu.about",
                "minicraft.menu.host_world",
                "minicraft.menu.language",
                "minicraft.menu.quit"
        };
    }

    @Override
    public void keyPressed(int key) {
        if (key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_UP) {
            this.selected--;
        }
        if (key == GLFW.GLFW_KEY_S || key == GLFW.GLFW_KEY_DOWN) {
            this.selected++;
        }

        int len = this.options.length;
        if (this.selected < 0) this.selected += len;
        if (this.selected >= len) this.selected -= len;

        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_C || key == GLFW.GLFW_KEY_SPACE) {
            if (this.selected == 0) {
                this.client.setMenu(null);
            }
            if (this.selected == 1)  this.client.setMenu(new InstructionsMenu(this));
            if (this.selected == 2) this.client.setMenu(new AboutMenu(this));
            if (this.selected == 3 && this.optionsEnabled[3]) {
                if (this.client.integratedServer != null) {
                    int port = this.client.integratedServer.getAvailablePort();
                    this.options[3] = "Port:" + port;
                    this.optionsEnabled[3] = false;
                    this.client.integratedServer.openToLan(port);
                }
            }
            if (this.selected == 4) {
                this.client.setMenu(new LanguageMenu(this));
            }
            if (this.selected == 5) {
                this.client.disconnect();
                this.client.setMenu(new TitleMenu());
            }
        }
    }

    @Override
    public void render() {
        this.client.renderer.renderSprite("textures/ui", "title.png", (this.getWidth() - 120) / 2, 24, 120, 16, false, false);

        this.client.renderer.renderSpriteNineslice("textures/ui", "frame.png", 16, 56, this.getWidth() - 32, (this.options.length + 2) * 8, 8, 8, 8, 8);

        for (int i = 0; i < this.options.length; i++) {
            String msg = Translation.translate(this.options[i]);
            int col = 0x808080;
            if (i == this.selected) {
                msg = "> " + msg + " <";
                col = 0xFFFFFF;
            }
            this.client.font.draw(msg, (this.getWidth() - msg.length() * 8) / 2, (8 + i) * 8, col);
        }
    }
}
