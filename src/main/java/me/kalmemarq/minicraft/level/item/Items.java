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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import me.kalmemarq.minicraft.client.util.IOUtils;
import me.kalmemarq.minicraft.level.tile.Tile;
import me.kalmemarq.minicraft.util.Registry;
import me.kalmemarq.minicraft.level.tile.Tiles;

import java.io.IOException;

public class Items {
    public static final Registry<Item> REGISTRY = new Registry<>();
    public static Item WOOD_SHOVEL = REGISTRY.register("wood_shovel", new ToolItem(ToolType.SHOVEL, 0));
    public static Item ROCK_SHOVEL = REGISTRY.register("rock_shovel", new ToolItem(ToolType.SHOVEL, 1));
    public static Item IRON_SHOVEL = REGISTRY.register("iron_shovel", new ToolItem(ToolType.SHOVEL, 2));
    public static Item GOLD_SHOVEL = REGISTRY.register("gold_shovel", new ToolItem(ToolType.SHOVEL, 3));
    public static Item GEM_SHOVEL = REGISTRY.register("gem_shovel", new ToolItem(ToolType.SHOVEL, 4));
    public static Item WOOD_HOE = REGISTRY.register("wood_hoe", new ToolItem(ToolType.HOE, 0));
    public static Item ROCK_HOE = REGISTRY.register("rock_hoe", new ToolItem(ToolType.HOE, 1));
    public static Item IRON_HOE = REGISTRY.register("iron_hoe", new ToolItem(ToolType.HOE, 2));
    public static Item GOLD_HOE = REGISTRY.register("gold_hoe", new ToolItem(ToolType.HOE, 3));
    public static Item GEM_HOE = REGISTRY.register("gem_hoe", new ToolItem(ToolType.HOE, 4));
    public static Item WOOD_PICKAXE = REGISTRY.register("wood_pickaxe", new ToolItem(ToolType.PICKAXE, 0));
    public static Item ROCK_PICKAXE = REGISTRY.register("rock_pickaxe", new ToolItem(ToolType.PICKAXE, 1));
    public static Item IRON_PICKAXE = REGISTRY.register("iron_pickaxe", new ToolItem(ToolType.PICKAXE, 2));
    public static Item GOLD_PICKAXE = REGISTRY.register("gold_pickaxe", new ToolItem(ToolType.PICKAXE, 3));
    public static Item GEM_PICKAXE = REGISTRY.register("gem_pickaxe", new ToolItem(ToolType.PICKAXE, 4));
    public static Item WOOD_AXE = REGISTRY.register("wood_axe", new ToolItem(ToolType.AXE, 0));
    public static Item ROCK_AXE = REGISTRY.register("rock_axe", new ToolItem(ToolType.AXE, 1));
    public static Item IRON_AXE = REGISTRY.register("iron_axe", new ToolItem(ToolType.AXE, 2));
    public static Item GOLD_AXE = REGISTRY.register("gold_axe", new ToolItem(ToolType.AXE, 3));
    public static Item GEM_AXE = REGISTRY.register("gem_axe", new ToolItem(ToolType.AXE, 4));
    public static Item WOOD_SWORD = REGISTRY.register("wood_sword", new ToolItem(ToolType.SWORD, 0));
    public static Item ROCK_SWORD = REGISTRY.register("rock_sword", new ToolItem(ToolType.SWORD, 1));
    public static Item IRON_SWORD = REGISTRY.register("iron_sword", new ToolItem(ToolType.SWORD, 2));
    public static Item GOLD_SWORD = REGISTRY.register("gold_sword", new ToolItem(ToolType.SWORD, 3));
    public static Item GEM_SWORD = REGISTRY.register("gem_sword", new ToolItem(ToolType.SWORD, 4));
    public static Item POWER_GLOVE = REGISTRY.register("power_glove", new PowerGloveItem());
    public static Item WOOD = REGISTRY.register("wood", new ResourceItem());
    public static Item STONE = REGISTRY.register("stone", new ResourceItem());
    public static Item FLOWER = REGISTRY.register("flower", new PlantableItem(Tiles.FLOWER, Tiles.GRASS));
    public static Item ACORN = REGISTRY.register("acorn", new PlantableItem(Tiles.TREE_SAPLING, Tiles.GRASS));
    public static Item DIRT = REGISTRY.register("dirt", new PlantableItem(Tiles.DIRT, Tiles.HOLE, Tiles.WATER, Tiles.LAVA));
    public static Item SAND = REGISTRY.register("sand", new PlantableItem(Tiles.SAND, Tiles.GRASS, Tiles.DIRT));
    public static Item CACTUS_FLOWER = REGISTRY.register("cactus_flower", new PlantableItem(Tiles.CACTUS_SAPLING, Tiles.SAND));
    public static Item SEEDS = REGISTRY.register("seeds", new PlantableItem(Tiles.WHEAT, Tiles.FARMLAND));
    public static Item WHEAT = REGISTRY.register("wheat", new ResourceItem());
    public static Item BREAD = REGISTRY.register("bread", new FoodItem(2, 5));
    public static Item APPLE = REGISTRY.register("apple", new FoodItem(1, 5));
    public static Item COAL = REGISTRY.register("coal", new ResourceItem());
    public static Item IRON_ORE = REGISTRY.register("iron_ore", new ResourceItem());
    public static Item GOLD_ORE = REGISTRY.register("gold_ore", new ResourceItem());
    public static Item IRON_INGOT = REGISTRY.register("iron_ingot", new ResourceItem());
    public static Item GOLD_INGOT = REGISTRY.register("gold_ingot", new ResourceItem());
    public static Item SLIME = REGISTRY.register("slime", new ResourceItem());
    public static Item GLASS = REGISTRY.register("glass", new ResourceItem());
    public static Item CLOTH = REGISTRY.register("cloth", new ResourceItem());
    public static Item CLOUD = REGISTRY.register("cloud", new PlantableItem(Tiles.CLOUD, Tiles.INFINITE_FALL));
    public static Item GEM = REGISTRY.register("gem", new ResourceItem());
    public static Item POTATO = REGISTRY.register("potato", new FoodItem(2, 5));
    public static Item BAKED_POTATO = REGISTRY.register("baked_potato", new FoodItem(2, 5));

    static {
        try {
            ArrayNode node = (ArrayNode) IOUtils.YAML_OBJECT_MAPPER.readTree(Items.class.getResourceAsStream("/items.yaml"));

            for (JsonNode item : node) {
                String id = item.get("id").textValue();
                if (item.has("food")) {
                    JsonNode foodComponent = item.get("food");
                    REGISTRY.register(id, new FoodItem(foodComponent.get("heal").asInt(), foodComponent.get("stamina_cost").asInt()));
                } else if (item.has("plantable")) {
                    JsonNode plantableComponent = item.get("plantable");
                    String target = plantableComponent.get("target").textValue();
                    if (plantableComponent.get("sources").isArray()) {
                        Tile[] tiles = new Tile[plantableComponent.get("sources").size()];
                        int i = 0;
                        for (JsonNode i1 : plantableComponent.get("sources")) {
                            tiles[i] = Tiles.REGISTRY.getByStringId(i1.textValue());
                            ++i;
                        }
                        REGISTRY.register(id, new PlantableItem(Tiles.REGISTRY.getByStringId(target), tiles));
                    } else {
                        String source = plantableComponent.get("sources").textValue();
                        REGISTRY.register(id, new PlantableItem(Tiles.REGISTRY.getByStringId(target), Tiles.REGISTRY.getByStringId(source)));
                    }
                } else if (item.has("resource")) {
                    REGISTRY.register(id, new ResourceItem());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
