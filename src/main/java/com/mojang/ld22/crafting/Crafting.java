package com.mojang.ld22.crafting;

import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Items;

import java.util.ArrayList;
import java.util.List;

// TODO: Data-driven/dynamic registry
public class Crafting {
	public static final List<Recipe> anvilRecipes = new ArrayList<>();
	public static final List<Recipe> ovenRecipes = new ArrayList<>();
	public static final List<Recipe> furnaceRecipes = new ArrayList<>();
	public static final List<Recipe> workbenchRecipes = new ArrayList<>();

	static {
		workbenchRecipes.add(new Recipe(new ItemStack(Items.LANTERN))
			.addCost(new ItemStack(Items.WOOD, 5))
			.addCost(new ItemStack(Items.SLIME, 10))
			.addCost(new ItemStack(Items.GLASS, 4)));

		workbenchRecipes.add(new Recipe(new ItemStack(Items.OVEN)).addCost(new ItemStack(Items.STONE, 15)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.FURNACE)).addCost(new ItemStack(Items.STONE, 20)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.WORKBENCH)).addCost(new ItemStack(Items.WOOD, 20)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.CHEST)).addCost(new ItemStack(Items.WOOD, 20)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.ANVIL)).addCost(new ItemStack(Items.IRON_INGOT, 5)));

		workbenchRecipes.add(new Recipe(new ItemStack(Items.WOOD_SWORD)).addCost(new ItemStack(Items.WOOD, 5)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.WOOD_AXE)).addCost(new ItemStack(Items.WOOD, 5)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.WOOD_HOE)).addCost(new ItemStack(Items.WOOD, 5)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.WOOD_PICKAXE)).addCost(new ItemStack(Items.WOOD, 5)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.WOOD_SHOVEL)).addCost(new ItemStack(Items.WOOD, 5)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.ROCK_SWORD)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.STONE, 5)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.ROCK_AXE)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.STONE, 5)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.ROCK_HOE)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.STONE, 5)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.ROCK_PICKAXE)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.STONE, 5)));
		workbenchRecipes.add(new Recipe(new ItemStack(Items.ROCK_SHOVEL)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.STONE, 5)));

		anvilRecipes.add(new Recipe(new ItemStack(Items.IRON_SWORD)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.IRON_INGOT, 5)));
		anvilRecipes.add(new Recipe(new ItemStack(Items.IRON_AXE)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.IRON_INGOT, 5)));
		anvilRecipes.add(new Recipe(new ItemStack(Items.IRON_HOE)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.IRON_INGOT, 5)));
		anvilRecipes.add(new Recipe(new ItemStack(Items.IRON_PICKAXE)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.IRON_INGOT, 5)));
		anvilRecipes.add(new Recipe(new ItemStack(Items.IRON_SHOVEL)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.IRON_INGOT, 5)));

		anvilRecipes.add(new Recipe(new ItemStack(Items.GOLD_SWORD)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.GOLD_INGOT, 5)));
		anvilRecipes.add(new Recipe(new ItemStack(Items.GOLD_AXE)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.GOLD_INGOT, 5)));
		anvilRecipes.add(new Recipe(new ItemStack(Items.GOLD_HOE)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.GOLD_INGOT, 5)));
		anvilRecipes.add(new Recipe(new ItemStack(Items.GOLD_PICKAXE)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.GOLD_INGOT, 5)));
		anvilRecipes.add(new Recipe(new ItemStack(Items.GOLD_SHOVEL)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.GOLD_INGOT, 5)));

		anvilRecipes.add(new Recipe(new ItemStack(Items.GEM_SWORD)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.GEM, 50)));
		anvilRecipes.add(new Recipe(new ItemStack(Items.GEM_AXE)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.GEM, 50)));
		anvilRecipes.add(new Recipe(new ItemStack(Items.GEM_HOE)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.GEM, 50)));
		anvilRecipes.add(new Recipe(new ItemStack(Items.GEM_PICKAXE)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.GEM, 50)));
		anvilRecipes.add(new Recipe(new ItemStack(Items.GEM_SHOVEL)).addCost(new ItemStack(Items.WOOD, 5)).addCost(new ItemStack(Items.GEM, 50)));

		furnaceRecipes.add(new Recipe(new ItemStack(Items.IRON_INGOT)).addCost(new ItemStack(Items.IRON_ORE, 4)).addCost(new ItemStack(Items.COAL, 1)));
		furnaceRecipes.add(new Recipe(new ItemStack(Items.GOLD_INGOT)).addCost(new ItemStack(Items.GOLD_ORE, 4)).addCost(new ItemStack(Items.COAL, 1)));
		furnaceRecipes.add(new Recipe(new ItemStack(Items.GLASS)).addCost(new ItemStack(Items.SAND, 4)).addCost(new ItemStack(Items.COAL, 1)));

		ovenRecipes.add(new Recipe(new ItemStack(Items.BREAD)).addCost(new ItemStack(Items.WHEAT, 4)));
	}
}
