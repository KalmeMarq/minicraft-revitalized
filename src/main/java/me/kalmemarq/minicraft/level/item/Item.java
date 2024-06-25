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
import me.kalmemarq.minicraft.level.entity.Entity;
import me.kalmemarq.minicraft.level.entity.ItemEntity;
import me.kalmemarq.minicraft.level.entity.PlayerEntity;
import me.kalmemarq.minicraft.level.tile.Tile;

public class Item {
    private int numericId = -1;
    private String stringId;

    public int getNumericId() {
        if (this.numericId == -1) this.numericId = Items.REGISTRY.getNumericId(this);
        return this.numericId;
    }

    public String getStringId() {
        if (this.stringId == null) this.stringId = Items.REGISTRY.getStringId(this);
        return this.stringId;
    }

    public void onTake(ItemEntity itemEntity) {
    }

    public boolean interact(PlayerEntity player, Entity entity, int attackDir) {
        return false;
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, PlayerEntity player, ItemStack stack, int attackDir) {
        return false;
    }

    public boolean canAttack() {
        return false;
    }

    public int getAttackDamageBonus(Entity e) {
        return 0;
    }

    public String getName() {
        return "";
    }

    public boolean matches(Item item) {
        return item.getClass() == this.getClass();
    }

    public int getMaxStackSize() {
        return 999;
    }
}
