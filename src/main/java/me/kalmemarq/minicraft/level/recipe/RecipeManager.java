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

package me.kalmemarq.minicraft.level.recipe;

import com.fasterxml.jackson.databind.node.ObjectNode;
import me.kalmemarq.minicraft.client.util.IOUtils;
import me.kalmemarq.minicraft.resource.DefaultResourcePack;
import me.kalmemarq.minicraft.resource.ResourcePack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecipeManager {
    public static final RecipeManager INSTANCE = new RecipeManager();

    public final List<Recipe> anvilRecipes = new ArrayList<>();
    public final List<Recipe> ovenRecipes = new ArrayList<>();
    public final List<Recipe> furnaceRecipes = new ArrayList<>();
    public final List<Recipe> workbenchRecipes = new ArrayList<>();

    public RecipeManager() {
        this.load();
    }

    public void load() {
        for (ResourcePack.ResourceSupplier file : DefaultResourcePack.INSTANCE.list("recipes", p -> p.endsWith(".yaml"))) {
            try {
                this.loadRecipe((ObjectNode) IOUtils.YAML_OBJECT_MAPPER.readTree(file.getReader()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println((this.anvilRecipes.size() + this.ovenRecipes.size() + this.furnaceRecipes.size() + this.workbenchRecipes.size()) + " recipes");
    }

    private void loadRecipe(ObjectNode node) {
        String forStation = node.get("for").textValue();
        Recipe recipe = Recipe.fromJson(node);
        switch (forStation) {
            case "workbench" -> this.workbenchRecipes.add(recipe);
            case "oven" -> this.ovenRecipes.add(recipe);
            case "anvil" -> this.anvilRecipes.add(recipe);
            case "furnace" -> this.furnaceRecipes.add(recipe);
        }
    }
}
