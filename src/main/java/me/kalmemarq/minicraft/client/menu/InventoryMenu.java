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

import me.kalmemarq.minicraft.client.InputHandler;
import me.kalmemarq.minicraft.client.util.Translation;
import me.kalmemarq.minicraft.level.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class InventoryMenu extends Menu {
    private int selected;

    @Override
    public void tick() {
        if (InputHandler.Key.MENU.clicked) this.client.setMenu(null);
    }

    @Override
    public void keyPressed(int key) {
        if (InputHandler.Key.MENU.test(key)) this.client.setMenu(null);

        if (key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_UP) this.selected--;
        if (key == GLFW.GLFW_KEY_S || key == GLFW.GLFW_KEY_DOWN) this.selected++;

        int len = this.client.player.inventory.itemStacks.size();
        if (this.selected < 0) this.selected += len;
        if (this.selected >= len) this.selected -= len;
    }

    @Override
    public void render() {
        this.client.renderer.renderSpriteNineslice("ui", "frame.png", 8, 8, 12 * 8, 11 * 8, 8, 8, 8, 8);
        this.renderItemStackList(1, 1, 12, 11, this.client.player.inventory.itemStacks, this.selected);
    }

    public void renderItemStackList(int xo, int yo, int x1, int y1, List<ItemStack> listItems, int selected) {
        boolean renderCursor = true;
        if (selected < 0) {
            selected = -selected - 1;
            renderCursor = false;
        }
        int w = x1 - xo;
        int h = y1 - yo - 1;
        int i0 = 0;
        int i1 = listItems.size();
        if (i1 > h) i1 = h;
        int io = selected - h / 2;
        if (io > listItems.size() - h) io = listItems.size() - h;
        if (io < 0) io = 0;

        for (int i = i0; i < i1; i++) {
            int rx = (1 + xo) * 8;
            int ry = (i + 1 + yo) * 8;

            ItemStack stack = listItems.get(i + io);
            if (stack.isStackable()) {
                this.client.font.draw("" + stack.getCount(), rx + 8, ry, 0x999999);
                this.client.renderer.renderItem(stack.getItem(), rx + 26, ry);
                this.client.font.draw(Translation.translate("minicraft.item." + stack.getItem().getStringId()), rx + 34, ry, 0xFFFFFF);
            } else {
                this.client.renderer.renderItem(stack.getItem(), rx, ry);
                this.client.font.draw(Translation.translate("minicraft.item." + stack.getItem().getStringId()), rx + 8, ry, 0xFFFFFF);
            }
        }

        if (renderCursor) {
            int yy = selected + 1 - io + yo;
            this.client.font.draw(">", (xo) * 8, yy * 8, 0xFFFFFF);
            this.client.font.draw("<", (xo + w) * 8, yy * 8, 0xFFFFFF);
        }
    }
}
