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

package me.kalmemarq.minicraft.client.level;

import me.kalmemarq.minicraft.level.Level;
import me.kalmemarq.minicraft.level.tile.Tiles;

import java.util.ArrayList;

public class ClientLevel extends Level {
    @SuppressWarnings("unchecked")
    public void load(int width, int height, byte[] tiles) {
        this.width = width;
        this.height = height;
        this.tiles = tiles;
        this.data = new byte[this.width * this.height];
        this.loaded = true;
        this.entitiesInTiles = new ArrayList[this.width * this.height];
        for (int i = 0; i < this.width * this.height; i++) {
            this.entitiesInTiles[i] = new ArrayList<>();
        }
    }

    public void setTile(int x, int y, int id, int data) {
        super.setTile(x, y, Tiles.REGISTRY.getByNumericId(id), data);
    }

    @Override
    public boolean isClient() {
        return true;
    }
}
