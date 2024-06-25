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

package me.kalmemarq.minicraft.level.tile;

import me.kalmemarq.minicraft.level.Level;
import me.kalmemarq.minicraft.level.entity.Entity;
import me.kalmemarq.minicraft.level.entity.MobEntity;
import me.kalmemarq.minicraft.level.entity.PlayerEntity;
import me.kalmemarq.minicraft.level.item.ItemStack;

import java.util.Random;

public class Tile {
    protected Random random = new Random();
    public boolean connectsToGrass = false;
    public boolean connectsToSand = false;
    public boolean connectsToLava = false;
    public boolean connectsToWater = false;

    private int numericId = -1;
    private String stringId;

    public Tile() {
    }

    public int getNumericId() {
        if (this.numericId == -1) this.numericId = Tiles.REGISTRY.getNumericId(this);
        return this.numericId;
    }

    public String getStringId() {
        if (this.stringId == null) this.stringId = Tiles.REGISTRY.getStringId(this);
        return this.stringId;
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return true;
    }

    public int getLightRadius(Level level, int x, int y) {
        return 0;
    }

    public void hurt(Level level, int x, int y, MobEntity source, int dmg, int attackDir) {
    }

    public void bumpedInto(Level level, int xt, int yt, Entity entity) {
    }

    public void tick(Level level, int xt, int yt) {
    }

    public boolean interact(Level level, int xt, int yt, PlayerEntity player, ItemStack item, int attackDir) {
        return false;
    }

    public void steppedOn(Level level, int xt, int yt, Entity entity) {
    }

    public boolean use(Level level, int xt, int yt, PlayerEntity player, int attackDir) {
        return false;
    }
}
