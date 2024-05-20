package me.kalmemarq.minicraft;

import com.mojang.ld22.entity.Anvil;
import com.mojang.ld22.entity.Chest;
import com.mojang.ld22.entity.Furnace;
import com.mojang.ld22.entity.Lantern;
import com.mojang.ld22.entity.Oven;
import com.mojang.ld22.entity.Workbench;
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

// TODO: Use dynamic registry
// TODO: Translation for item names
public final class Items {
	private static final ObjectList<Item> REGISTRY_IV = new ObjectArrayList<>();
	private static final Reference2IntMap<Item> REGISTRY_VI = new Reference2IntOpenHashMap<>();

	public static Item register(Item item) {
		REGISTRY_VI.put(item, REGISTRY_VI.size());
		REGISTRY_IV.add(item);
		return item;
	}

	public static Item getByNumericId(int id) {
		return REGISTRY_IV.get(id);
	}

	public static int getNumericId(Item item) {
		return REGISTRY_VI.getInt(item);
	}

	public static Item WOOD_SHOVEL = register(new ToolItem(ToolType.SHOVEL, 0));
	public static Item ROCK_SHOVEL = register(new ToolItem(ToolType.SHOVEL, 1));
	public static Item IRON_SHOVEL = register(new ToolItem(ToolType.SHOVEL, 2));
	public static Item GOLD_SHOVEL = register(new ToolItem(ToolType.SHOVEL, 3));
	public static Item GEM_SHOVEL = register(new ToolItem(ToolType.SHOVEL, 4));
	public static Item WOOD_HOE = register(new ToolItem(ToolType.HOE, 0));
	public static Item ROCK_HOE = register(new ToolItem(ToolType.HOE, 1));
	public static Item IRON_HOE = register(new ToolItem(ToolType.HOE, 2));
	public static Item GOLD_HOE = register(new ToolItem(ToolType.HOE, 3));
	public static Item GEM_HOE = register(new ToolItem(ToolType.HOE, 4));
	public static Item WOOD_PICKAXE = register(new ToolItem(ToolType.PICKAXE, 0));
	public static Item ROCK_PICKAXE = register(new ToolItem(ToolType.PICKAXE, 1));
	public static Item IRON_PICKAXE = register(new ToolItem(ToolType.PICKAXE, 2));
	public static Item GOLD_PICKAXE = register(new ToolItem(ToolType.PICKAXE, 3));
	public static Item GEM_PICKAXE = register(new ToolItem(ToolType.PICKAXE, 4));
	public static Item WOOD_AXE = register(new ToolItem(ToolType.AXE, 0));
	public static Item ROCK_AXE = register(new ToolItem(ToolType.AXE, 1));
	public static Item IRON_AXE = register(new ToolItem(ToolType.AXE, 2));
	public static Item GOLD_AXE = register(new ToolItem(ToolType.AXE, 3));
	public static Item GEM_AXE = register(new ToolItem(ToolType.AXE, 4));
	public static Item WOOD_SWORD = register(new ToolItem(ToolType.SWORD, 0));
	public static Item ROCK_SWORD = register(new ToolItem(ToolType.SWORD, 1));
	public static Item IRON_SWORD = register(new ToolItem(ToolType.SWORD, 2));
	public static Item GOLD_SWORD = register(new ToolItem(ToolType.SWORD, 3));
	public static Item GEM_SWORD = register(new ToolItem(ToolType.SWORD, 4));
	public static Item POWER_GLOVE = register(new PowerGloveItem());
	public static Item WOOD = register(new ResourceItem(new Resource("Wood", 1 + 4 * 32,Color.get(-1, 200, 531, 430))));
	public static Item STONE = register(new ResourceItem(new Resource("Stone", 2 + 4 * 32, Color.get(-1, 111, 333, 555))));
	public static Item FLOWER = register(new ResourceItem(new PlantableResource("Flower", 4 * 32, Color.get(-1, 10, 444, 330), Tile.flower, Tile.grass)));
	public static Item ACORN = register(new ResourceItem(new PlantableResource("Acorn", 3 + 4 * 32, Color.get(-1, 100, 531, 320), Tile.treeSapling, Tile.grass)));
	public static Item DIRT = register(new ResourceItem(new PlantableResource("Dirt", 2 + 4 * 32, Color.get(-1, 100, 322, 432), Tile.dirt, Tile.hole, Tile.water, Tile.lava)));
	public static Item SAND = register(new ResourceItem(new PlantableResource("Sand", 2 + 4 * 32, Color.get(-1, 110, 440, 550), Tile.sand, Tile.grass, Tile.dirt)));
	public static Item CACTUS_FLOWER = register(new ResourceItem(new PlantableResource("Cactus", 4 + 4 * 32, Color.get(-1, 10, 40, 50), Tile.cactusSapling, Tile.sand)));
	public static Item SEEDS = register(new ResourceItem(new PlantableResource("Seeds", 5 + 4 * 32, Color.get(-1, 10, 40, 50), Tile.wheat, Tile.farmland)));
	public static Item WHEAT = register(new ResourceItem(new Resource("Wheat", 6 + 4 * 32, Color.get(-1, 110, 330, 550))));
	public static Item BREAD = register(new ResourceItem(new FoodResource("Bread", 8 + 4 * 32, Color.get(-1, 110, 330, 550), 2, 5)));
	public static Item APPLE = register(new ResourceItem(new FoodResource("Apple", 9 + 4 * 32, Color.get(-1, 100, 300, 500), 1, 5)));
	public static Item COAL = register(new ResourceItem(new Resource("COAL", 10 + 4 * 32, Color.get(-1, 0, 111, 111))));
	public static Item IRON_ORE = register(new ResourceItem(new Resource("I.ORE", 10 + 4 * 32, Color.get(-1, 100, 322, 544))));
	public static Item GOLD_ORE = register(new ResourceItem(new Resource("G.ORE", 10 + 4 * 32, Color.get(-1, 110, 440, 553))));
	public static Item IRON_INGOT = register(new ResourceItem(new Resource("IRON", 11 + 4 * 32, Color.get(-1, 100, 322, 544))));
	public static Item GOLD_INGOT = register(new ResourceItem(new Resource("GOLD", 11 + 4 * 32, Color.get(-1, 110, 330, 553))));
	public static Item SLIME = register(new ResourceItem(new Resource("SLIME", 10 + 4 * 32, Color.get(-1, 10, 30, 50))));
	public static Item GLASS = register(new ResourceItem(new Resource("glass", 12 + 4 * 32, Color.get(-1, 555, 555, 555))));
	public static Item CLOTH = register(new ResourceItem(new Resource("cloth", 1 + 4 * 32, Color.get(-1, 25, 252, 141))));
	public static Item CLOUD = register(new ResourceItem(new PlantableResource("cloud", 2 + 4 * 32, Color.get(-1, 222, 555, 444), Tile.cloud, Tile.infiniteFall)));
	public static Item GEM = register(new ResourceItem(new Resource("gem", 13 + 4 * 32, Color.get(-1, 101, 404, 545))));

	// TODO: Use a FurniteType instead of creating an instance of the entity
	public static Item ANVIL = register(new FurnitureItem(new Anvil()));
	public static Item CHEST = register(new FurnitureItem(new Chest()));
	public static Item FURNACE = register(new FurnitureItem(new Furnace()));
	public static Item LANTERN = register(new FurnitureItem(new Lantern()));
	public static Item OVEN = register(new FurnitureItem(new Oven()));
	public static Item WORKBENCH = register(new FurnitureItem(new Workbench()));
}
