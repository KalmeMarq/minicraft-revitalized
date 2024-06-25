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

public class ToolItem extends Item {
    public ToolType type;
    public int level;

    public ToolItem(ToolType type, int level) {
        this.type = type;
        this.level = level;
    }

    @Override
    public boolean canAttack() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
