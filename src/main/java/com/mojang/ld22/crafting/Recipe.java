package com.mojang.ld22.crafting;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.screen.ListItem;

import me.kalmemarq.minicraft.ItemStack;

public class Recipe implements ListItem {
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

	public void checkCanCraft(Player player) {
        for (ItemStack item : this.costs) {
			if (!player.inventory.has(item)) {
				this.canCraft = false;
				return;
			}
        }
        this.canCraft = true;
	}

	public void renderInventory(Screen screen, int x, int y, ItemStack stack) {
		screen.render(x, y, this.resultTemplate.getItem().getSprite(), this.resultTemplate.getItem().getColor(), 0);
		int textColor = this.canCraft ? Color.get(-1, 555, 555, 555) : Color.get(-1, 222, 222, 222);
		Font.draw(this.resultTemplate.getItem().getName(), screen, x + 8, y, textColor);
	}

	public void craft(Player player) {
		player.inventory.add(0, this.resultTemplate.copy());
	}

	public void deductCost(Player player) {
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
