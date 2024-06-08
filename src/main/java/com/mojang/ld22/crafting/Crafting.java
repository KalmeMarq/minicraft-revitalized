package com.mojang.ld22.crafting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import me.kalmemarq.minicraft.IOUtils;
import me.kalmemarq.minicraft.resource.DefaultResourcePack;
import me.kalmemarq.minicraft.resource.ResourcePack;

// TODO: Use dynamic registry
public class Crafting {
	public static final List<Recipe> anvilRecipes = new ArrayList<>();
	public static final List<Recipe> ovenRecipes = new ArrayList<>();
	public static final List<Recipe> furnaceRecipes = new ArrayList<>();
	public static final List<Recipe> workbenchRecipes = new ArrayList<>();

	public static void load() {
		for (ResourcePack.ResourceSupplier file : DefaultResourcePack.INSTANCE.list("recipes", p -> p.endsWith(".json"))) {
			try {
				loadRecipe((ObjectNode) IOUtils.JSON_OBJECT_MAPPER.readTree(file.getReader()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println((anvilRecipes.size() + ovenRecipes.size() + furnaceRecipes.size() + workbenchRecipes.size()) + " recipes");
	}

	private static void loadRecipe(ObjectNode node) {
		String forStation = node.get("for").textValue();
		Recipe recipe = Recipe.fromJson(node);
		switch (forStation) {
			case "workbench" -> workbenchRecipes.add(recipe);
			case "oven" -> ovenRecipes.add(recipe);
			case "anvil" -> anvilRecipes.add(recipe);
			case "furnace" -> furnaceRecipes.add(recipe);
		}
	}
}
