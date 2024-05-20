package me.kalmemarq.minicraft;

import com.mojang.ld22.entity.furniture.Anvil;
import com.mojang.ld22.entity.furniture.Chest;
import com.mojang.ld22.entity.furniture.Furnace;
import com.mojang.ld22.entity.furniture.Lantern;
import com.mojang.ld22.entity.furniture.Oven;
import com.mojang.ld22.entity.furniture.Workbench;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.item.FurnitureItem;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.PowerGloveItem;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.FoodResource;
import com.mojang.ld22.item.resource.PlantableResource;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.tile.Tile;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

// TODO: Use dynamic registry
// TODO: Translation for item names
public final class Items {
	private static final ObjectList<Item> REGISTRY_IV = new ObjectArrayList<>();
	private static final Reference2IntMap<Item> REGISTRY_VI = new Reference2IntOpenHashMap<>();
	private static final Map<String, Item> REGISTRY = new HashMap<>();
	private static final Map<Item, String> REGISTRY_REV = new IdentityHashMap<>();

	public static Item register(String stringId, Item item) {
		REGISTRY.put(stringId, item);
		REGISTRY_REV.put(item, stringId);
		REGISTRY_VI.put(item, REGISTRY_VI.size());
		REGISTRY_IV.add(item);
		return item;
	}

	public static Item getByStringId(String id) {
		return REGISTRY.get(id);
	}

	public static Item getByNumericId(int id) {
		return REGISTRY_IV.get(id);
	}

	public static String getStringId(Item item) {
		return REGISTRY_REV.get(item);
	}

	public static int getNumericId(Item item) {
		return REGISTRY_VI.getInt(item);
	}

	public static Item WOOD_SHOVEL = register("wood_shovel", new ToolItem(ToolType.SHOVEL, 0));
	public static Item ROCK_SHOVEL = register("rock_shovel", new ToolItem(ToolType.SHOVEL, 1));
	public static Item IRON_SHOVEL = register("iron_shovel", new ToolItem(ToolType.SHOVEL, 2));
	public static Item GOLD_SHOVEL = register("gold_shovel", new ToolItem(ToolType.SHOVEL, 3));
	public static Item GEM_SHOVEL = register("gem_shovel", new ToolItem(ToolType.SHOVEL, 4));
	public static Item WOOD_HOE = register("wood_hoe", new ToolItem(ToolType.HOE, 0));
	public static Item ROCK_HOE = register("rock_hoe", new ToolItem(ToolType.HOE, 1));
	public static Item IRON_HOE = register("iron_hoe", new ToolItem(ToolType.HOE, 2));
	public static Item GOLD_HOE = register("gold_hoe", new ToolItem(ToolType.HOE, 3));
	public static Item GEM_HOE = register("gem_hoe", new ToolItem(ToolType.HOE, 4));
	public static Item WOOD_PICKAXE = register("wood_pickaxe", new ToolItem(ToolType.PICKAXE, 0));
	public static Item ROCK_PICKAXE = register("rock_pickaxe", new ToolItem(ToolType.PICKAXE, 1));
	public static Item IRON_PICKAXE = register("iron_pickaxe", new ToolItem(ToolType.PICKAXE, 2));
	public static Item GOLD_PICKAXE = register("gold_pickaxe", new ToolItem(ToolType.PICKAXE, 3));
	public static Item GEM_PICKAXE = register("gem_pickaxe", new ToolItem(ToolType.PICKAXE, 4));
	public static Item WOOD_AXE = register("wood_axe", new ToolItem(ToolType.AXE, 0));
	public static Item ROCK_AXE = register("rock_axe", new ToolItem(ToolType.AXE, 1));
	public static Item IRON_AXE = register("iron_axe", new ToolItem(ToolType.AXE, 2));
	public static Item GOLD_AXE = register("gold_axe", new ToolItem(ToolType.AXE, 3));
	public static Item GEM_AXE = register("gem_axe", new ToolItem(ToolType.AXE, 4));
	public static Item WOOD_SWORD = register("wood_sword", new ToolItem(ToolType.SWORD, 0));
	public static Item ROCK_SWORD = register("rock_sword", new ToolItem(ToolType.SWORD, 1));
	public static Item IRON_SWORD = register("iron_sword", new ToolItem(ToolType.SWORD, 2));
	public static Item GOLD_SWORD = register("gold_sword", new ToolItem(ToolType.SWORD, 3));
	public static Item GEM_SWORD = register("gem_sword", new ToolItem(ToolType.SWORD, 4));
	public static Item POWER_GLOVE = register("power_glove", new PowerGloveItem());
	public static Item WOOD = register("wood", new ResourceItem(new Resource("Wood", 1 + 4 * 32,Color.get(-1, 200, 531, 430))));
	public static Item STONE = register("stone", new ResourceItem(new Resource("Stone", 2 + 4 * 32, Color.get(-1, 111, 333, 555))));
	public static Item FLOWER = register("flower", new ResourceItem(new PlantableResource("Flower", 4 * 32, Color.get(-1, 10, 444, 330), Tile.flower, Tile.grass)));
	public static Item ACORN = register("acorn", new ResourceItem(new PlantableResource("Acorn", 3 + 4 * 32, Color.get(-1, 100, 531, 320), Tile.treeSapling, Tile.grass)));
	public static Item DIRT = register("dirt", new ResourceItem(new PlantableResource("Dirt", 2 + 4 * 32, Color.get(-1, 100, 322, 432), Tile.dirt, Tile.hole, Tile.water, Tile.lava)));
	public static Item SAND = register("sand", new ResourceItem(new PlantableResource("Sand", 2 + 4 * 32, Color.get(-1, 110, 440, 550), Tile.sand, Tile.grass, Tile.dirt)));
	public static Item CACTUS_FLOWER = register("cactus_flower", new ResourceItem(new PlantableResource("Cactus", 4 + 4 * 32, Color.get(-1, 10, 40, 50), Tile.cactusSapling, Tile.sand)));
	public static Item SEEDS = register("seeds", new ResourceItem(new PlantableResource("Seeds", 5 + 4 * 32, Color.get(-1, 10, 40, 50), Tile.wheat, Tile.farmland)));
	public static Item WHEAT = register("wheat", new ResourceItem(new Resource("Wheat", 6 + 4 * 32, Color.get(-1, 110, 330, 550))));
	public static Item BREAD = register("bread", new ResourceItem(new FoodResource("Bread", 8 + 4 * 32, Color.get(-1, 110, 330, 550), 2, 5)));
	public static Item APPLE = register("apple", new ResourceItem(new FoodResource("Apple", 9 + 4 * 32, Color.get(-1, 100, 300, 500), 1, 5)));
	public static Item COAL = register("coal", new ResourceItem(new Resource("COAL", 10 + 4 * 32, Color.get(-1, 0, 111, 111))));
	public static Item IRON_ORE = register("iron_ore", new ResourceItem(new Resource("I.ORE", 10 + 4 * 32, Color.get(-1, 100, 322, 544))));
	public static Item GOLD_ORE = register("gold_ore", new ResourceItem(new Resource("G.ORE", 10 + 4 * 32, Color.get(-1, 110, 440, 553))));
	public static Item IRON_INGOT = register("iron_ingot", new ResourceItem(new Resource("IRON", 11 + 4 * 32, Color.get(-1, 100, 322, 544))));
	public static Item GOLD_INGOT = register("gold_ingot", new ResourceItem(new Resource("GOLD", 11 + 4 * 32, Color.get(-1, 110, 330, 553))));
	public static Item SLIME = register("slime", new ResourceItem(new Resource("SLIME", 10 + 4 * 32, Color.get(-1, 10, 30, 50))));
	public static Item GLASS = register("glass", new ResourceItem(new Resource("glass", 12 + 4 * 32, Color.get(-1, 555, 555, 555))));
	public static Item CLOTH = register("cloth", new ResourceItem(new Resource("cloth", 1 + 4 * 32, Color.get(-1, 25, 252, 141))));
	public static Item CLOUD = register("cloud", new ResourceItem(new PlantableResource("cloud", 2 + 4 * 32, Color.get(-1, 222, 555, 444), Tile.cloud, Tile.infiniteFall)));
	public static Item GEM = register("gem", new ResourceItem(new Resource("gem", 13 + 4 * 32, Color.get(-1, 101, 404, 545))));

	// TODO: Use a FurniteType instead of creating an instance of the entity
	public static Item ANVIL = register("anvil", new FurnitureItem(new Anvil()));
	public static Item CHEST = register("chest", new FurnitureItem(new Chest()));
	public static Item FURNACE = register("furnace", new FurnitureItem(new Furnace()));
	public static Item LANTERN = register("lantern", new FurnitureItem(new Lantern()));
	public static Item OVEN = register("oven", new FurnitureItem(new Oven()));
	public static Item WORKBENCH = register("workbench", new FurnitureItem(new Workbench()));
}
