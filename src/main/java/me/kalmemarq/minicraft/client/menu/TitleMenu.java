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
import me.kalmemarq.minicraft.level.item.Item;
import me.kalmemarq.minicraft.level.item.Items;
import org.lwjgl.glfw.GLFW;

public class TitleMenu extends Menu {
    private int selected = 0;

    private String[] options;

    public TitleMenu() {
    }

    @Override
    public void init(Client client) {
        super.init(client);
        this.options = new String[]{
                Translation.translate("minicraft.menu.play"),
                Translation.translate("minicraft.menu.how_to_play"),
                Translation.translate("minicraft.menu.about"),
                Translation.translate("minicraft.menu.language"),
                Translation.translate("minicraft.menu.quit")
        };
    }

    @Override
    public void keyPressed(int key) {
        if (key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_UP) this.selected--;
        if (key == GLFW.GLFW_KEY_S || key == GLFW.GLFW_KEY_DOWN) this.selected++;

        int len = this.options.length;
        if (this.selected < 0) this.selected += len;
        if (this.selected >= len) this.selected -= len;

        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_C || key == GLFW.GLFW_KEY_SPACE) {
            if (this.selected == 0) this.client.setMenu(new PlayMenu(this));
            if (this.selected == 1)  this.client.setMenu(new InstructionsMenu(this));
            if (this.selected == 2) this.client.setMenu(new AboutMenu(this));
            if (this.selected == 3) this.client.setMenu(new LanguageMenu(this));
            if (this.selected == 4) this.client.running = false;
        }
    }

    @Override
    public void render() {
        this.client.renderer.renderSprite("ui", "title.png", (this.getWidth() - 120) / 2, 24, 120, 16, false, false);

        for (int i = 0; i < this.options.length; i++) {
            String msg = this.options[i];
            int col = 0x808080;
            if (i == this.selected) {
                msg = "> " + msg + " <";
                col = 0xFFFFFF;
            }
            this.client.font.draw(msg, (this.getWidth() - msg.length() * 8) / 2, 8 * 8 + (i * 9), col);
        }

        this.client.font.draw("(Arrow keys,X and C)", 0, this.getHeight() - 8, 0x404040);

//        int i = 0;
//        for (Item item : Items.REGISTRY.getAll()) {
//            int c = i / (this.getWidth() / 8);
//            int r = i % (this.getWidth() / 8);
//            this.client.renderer.renderItem(item, c * 8, r * 8);
//            ++i;
//        }
    }
}
