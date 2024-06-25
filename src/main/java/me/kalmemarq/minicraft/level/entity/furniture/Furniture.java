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

package me.kalmemarq.minicraft.level.entity.furniture;

import me.kalmemarq.minicraft.level.Level;
import me.kalmemarq.minicraft.level.entity.Entity;
import me.kalmemarq.minicraft.level.entity.PlayerEntity;
import me.kalmemarq.minicraft.level.item.ItemStack;

public abstract class Furniture extends Entity {
    private int pushTime = 0;
    private int pushDir = -1;

    public Furniture(Level level) {
        super(level);
        this.xr = 3;
        this.yr = 3;
    }

    @Override
    public void tickAi() {
        if (this.pushDir == 0) this.move(0, +1);
        if (this.pushDir == 1) this.move(0, -1);
        if (this.pushDir == 2) this.move(-1, 0);
        if (this.pushDir == 3) this.move(+1, 0);
        this.pushDir = -1;
        if (this.pushTime > 0) this.pushTime--;
    }

    abstract public FurnitureType<?> getFurnitureType();

    public ItemStack beforeGivenItem(ItemStack stack) {
        return stack;
    }

    @Override
    public boolean blocks(Entity e) {
        return true;
    }

    @Override
    protected void touchedBy(Entity entity) {
        if (entity instanceof PlayerEntity player && this.pushTime == 0) {
            this.pushDir = player.dir;
            this.pushTime = 10;
        }
    }
}
