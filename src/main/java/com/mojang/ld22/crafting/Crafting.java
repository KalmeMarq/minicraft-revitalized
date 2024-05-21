package com.mojang.ld22.crafting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mojang.ld22.item.Item;
import me.kalmemarq.minicraft.IOUtils;
import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Items;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

// TODO: Use dynamic registry
public class Crafting {
	public static final List<Recipe> anvilRecipes = new ArrayList<>();
	public static final List<Recipe> ovenRecipes = new ArrayList<>();
	public static final List<Recipe> furnaceRecipes = new ArrayList<>();
	public static final List<Recipe> workbenchRecipes = new ArrayList<>();

	public static void load() {
		for (Path path : listRecipeFiles()) {
			try {
				loadRecipe(IOUtils.getResourcesPath().relativize(path), (ObjectNode) IOUtils.JSON_OBJECT_MAPPER.readTree(Files.newBufferedReader(path)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void loadRecipe(Path name, ObjectNode node) {
		String forStation = node.get("for").textValue();
		ArrayNode ingredients = (ArrayNode) node.get("cost");
		ObjectNode result = (ObjectNode) node.get("result");

		Item resItem = Items.REGISTRY.getByStringId(result.get("item").textValue());
		if (resItem == null) throw new RuntimeException("resItem is null: " + name);
		ItemStack output = new ItemStack(resItem, result.has("count") ? result.get("count").shortValue() : 1);
		Recipe recipe = new Recipe(output);

		for (JsonNode item : ingredients) {
			ObjectNode obj = (ObjectNode) item;
			Item cItem = Items.REGISTRY.getByStringId(obj.get("item").textValue());
			if (cItem == null) throw new RuntimeException("cItem is null: " + name);
			recipe.addCost(new ItemStack(cItem, obj.has("count") ? obj.get("count").shortValue() : 1));
		}

		switch (forStation) {
			case "workbench" -> workbenchRecipes.add(recipe);
			case "oven" -> ovenRecipes.add(recipe);
			case "anvil" -> anvilRecipes.add(recipe);
			case "furnace" -> furnaceRecipes.add(recipe);
		}
	}

	private static List<Path> listRecipeFiles() {
		List<Path> files = new ArrayList<>();

		try (Stream<Path> paths = Files.walk(IOUtils.getResourcesPath().resolve("recipes"))) {
			for (Iterator<Path> it = paths.iterator(); it.hasNext(); ) {
				Path path = it.next();
				if (!Files.isDirectory(path) && path.toString().endsWith(".json")) {
					files.add(path);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return files;
	}
}
