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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.kalmemarq.minicraft.level.entity.PlayerEntity;
import me.kalmemarq.minicraft.level.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    public List<ItemStack> costs = new ArrayList<>();
    public boolean canCraft = false;
    public ItemStack resultTemplate;

    public Recipe(ItemStack resultTemplate) {
        this.resultTemplate = resultTemplate;
    }

    public Recipe addCost(ItemStack stack) {
        this.costs.add(stack);
        return this;
    }

    public void checkCanCraft(PlayerEntity player) {
        for (ItemStack item : this.costs) {
            if (!player.inventory.has(item)) {
                this.canCraft = false;
                return;
            }
        }
        this.canCraft = true;
    }

    public void craft(PlayerEntity player) {
        player.inventory.add(0, this.resultTemplate.copy());
    }

    public void deductCost(PlayerEntity player) {
        for (ItemStack item : this.costs) {
            player.inventory.remove(item);
        }
    }

    public static Recipe fromJson(ObjectNode node) {
        ArrayNode cost = (ArrayNode) node.get("cost");
        ObjectNode result = (ObjectNode) node.get("result");

        Recipe recipe = new Recipe(ItemStack.fromJson(result));

        for (JsonNode item : cost) {
            recipe.addCost(ItemStack.fromJson((ObjectNode) item));
        }

        return recipe;
    }
}
