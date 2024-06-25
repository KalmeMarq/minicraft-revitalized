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

import java.util.function.Function;

public record FurnitureType<T extends Furniture>(Function<Level, T> creator, String name, int lightRadius) {
    public static final FurnitureType<AnvilFurniture> ANVIL = new FurnitureType<>(AnvilFurniture::new, "Anvil", 0);
    public static final FurnitureType<ChestFurniture> CHEST = new FurnitureType<>(ChestFurniture::new, "Chest", 0);
    public static final FurnitureType<OvenFurniture> OVEN = new FurnitureType<>(OvenFurniture::new, "Oven", 0);
    public static final FurnitureType<FurnaceFurniture> FURNACE = new FurnitureType<>(FurnaceFurniture::new, "Furnace", 0);
    public static final FurnitureType<WorkbenchFurniture> WORKBENCH = new FurnitureType<>(WorkbenchFurniture::new, "Workbench", 0);
    public static final FurnitureType<LanternFurniture> LANTERN = new FurnitureType<>(LanternFurniture::new, "Lantern", 8);

    public T create(Level level) {
        return this.creator.apply(level);
    }
}
