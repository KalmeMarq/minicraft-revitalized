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

public class WorkbenchFurniture extends Furniture {
    public WorkbenchFurniture(Level level) {
        super(level);
    }

    @Override
    public FurnitureType<?> getFurnitureType() {
        return FurnitureType.WORKBENCH;
    }
}
