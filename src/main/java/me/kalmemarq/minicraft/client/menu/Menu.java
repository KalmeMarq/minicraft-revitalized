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

public class Menu {
    protected Client client;

    public void init(Client client) {
        this.client = client;
    }

    public int getWidth() {
        return this.client.window.getWidth() / 3;
    }

    public int getHeight() {
        return this.client.window.getHeight() / 3;
    }

    public void keyPressed(int key) {
    }

    public void charTyped(int codepoint) {
    }

    public void tick() {
    }

    public void render() {
    }
}
