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

package me.kalmemarq.minicraft.level.item;

import me.kalmemarq.minicraft.level.Level;
import me.kalmemarq.minicraft.level.entity.PlayerEntity;
import me.kalmemarq.minicraft.level.tile.Tile;

import java.util.Arrays;
import java.util.List;

public class PlantableItem extends Item {
    private final List<Tile> sourceTiles;
    private final Tile targetTile;

    public PlantableItem(Tile targetTile, Tile... sourceTiles1) {
        this(targetTile, Arrays.asList(sourceTiles1));
    }

    public PlantableItem(Tile targetTile, List<Tile> sourceTiles) {
        this.sourceTiles = sourceTiles;
        this.targetTile = targetTile;
    }

    @Override
    public boolean interactOn(Tile tile, Level level, int xt, int yt, PlayerEntity player, ItemStack stack, int attackDir) {
        if (this.sourceTiles.contains(tile)) {
            level.setTile(xt, yt, this.targetTile, 0);
            stack.decrement(1);
            return true;
        }
        return false;
    }
}
