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
import me.kalmemarq.minicraft.level.entity.ItemEntity;
import me.kalmemarq.minicraft.level.entity.MobEntity;
import me.kalmemarq.minicraft.level.entity.particle.TextParticle;
import me.kalmemarq.minicraft.level.item.ItemStack;
import me.kalmemarq.minicraft.level.item.Items;

public class TreeTile extends Tile {
    public TreeTile() {
        this.connectsToGrass = true;
    }

    @Override
    public boolean mayPass(Level level, int x, int y, Entity e) {
        return false;
    }

    @Override
    public void hurt(Level level, int x, int y, MobEntity source, int dmg, int attackDir) {
        {
            int count = this.random.nextInt(10) == 0 ? 1 : 0;
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(level, new ItemStack(Items.APPLE), x * 16 + this.random.nextInt(10) + 3, y * 16 + this.random.nextInt(10) + 3));
            }
        }
        int damage = level.getData(x, y) + dmg;
//        level.add(new SmashParticle(level, x * 16 + 8, y * 16 + 8));
        level.add(new TextParticle(level, "" + dmg, x * 16 + 8, y * 16 + 8, 0x9e2c2c));
        if (damage >= 20) {
            int count = this.random.nextInt(2) + 1;
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(level, new ItemStack(Items.WOOD), x * 16 + this.random.nextInt(10) + 3, y * 16 + this.random.nextInt(10) + 3));
            }
            count = this.random.nextInt(this.random.nextInt(4) + 1);
            for (int i = 0; i < count; i++) {
                level.add(new ItemEntity(level, new ItemStack(Items.ACORN), x * 16 + this.random.nextInt(10) + 3, y * 16 + this.random.nextInt(10) + 3));
            }
            level.setTile(x, y, Tiles.GRASS, 0);
        } else {
            level.setData(x, y, damage);
        }
    }
}
