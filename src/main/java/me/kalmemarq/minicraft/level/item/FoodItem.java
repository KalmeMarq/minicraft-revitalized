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

public class FoodItem extends Item {
    private final int heal;
    private final int staminaCost;

    public FoodItem(int heal, int staminaCost) {
        this.heal = heal;
        this.staminaCost = staminaCost;
    }

    @Override
    public boolean interactOn(Tile tile, Level level, int xt, int yt, PlayerEntity player, ItemStack stack, int attackDir) {
        if (player.health < player.maxHealth && player.payStamina(this.staminaCost)) {
            player.heal(this.heal);
            stack.decrement(1);
            return true;
        }
        return false;
    }
}
