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

import me.kalmemarq.minicraft.util.Registry;

public final class Tiles {
    public static final Registry<Tile> REGISTRY = new Registry<>();
    public static Tile GRASS = REGISTRY.register("grass", new GrassTile());
    public static Tile ROCK = REGISTRY.register("rock", new RockTile());
    public static Tile WATER = REGISTRY.register("water", new WaterTile());
    public static Tile FLOWER = REGISTRY.register("flower", new FlowerTile());
    public static Tile TREE = REGISTRY.register("tree", new TreeTile());
    public static Tile DIRT = REGISTRY.register("dirt", new DirtTile());
    public static Tile SAND = REGISTRY.register("sand", new SandTile());
    public static Tile CACTUS = REGISTRY.register("cactus", new CactusTile());
    public static Tile HOLE = REGISTRY.register("hole", new HoleTile());
    public static Tile TREE_SAPLING = REGISTRY.register("tree_sapling", new SaplingTile(GRASS, TREE));
    public static Tile CACTUS_SAPLING = REGISTRY.register("cactus_sapling", new SaplingTile(SAND, CACTUS));
    public static Tile FARMLAND = REGISTRY.register("farmland", new FarmTile());
    public static Tile WHEAT = REGISTRY.register("wheat", new WheatTile());
    public static Tile LAVA = REGISTRY.register("lava", new LavaTile());
    public static Tile STAIRS_DOWN = REGISTRY.register("stairs_down", new StairsTile(false));
    public static Tile STAIRS_UP = REGISTRY.register("stairs_up", new StairsTile(true));
    public static Tile INFINITE_FALL = REGISTRY.register("infinite_fall", new InfiniteFallTile());
    public static Tile CLOUD = REGISTRY.register("cloud", new CloudTile());
    public static Tile HARD_ROCK = REGISTRY.register("hard_rock", new HardRockTile());
    public static Tile IRON_ORE = REGISTRY.register("iron_ore", new OreTile());
    public static Tile GOLD_ORE = REGISTRY.register("gold_ore", new OreTile());
    public static Tile GEM_ORE = REGISTRY.register("gem_ore", new OreTile());
    public static Tile CLOUD_CACTUS = REGISTRY.register("cloud_cactus", new CloudCactusTile());
}
