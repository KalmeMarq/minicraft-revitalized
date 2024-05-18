package com.mojang.ld22.screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mojang.ld22.Game;
import com.mojang.ld22.crafting.Recipe;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.sound.Sound;

public class CraftingMenu extends Menu {
	private final Player player;
	private int selected = 0;

	private final List<Recipe> recipes;

	public CraftingMenu(List<Recipe> recipes, Player player) {
		this.recipes = new ArrayList<Recipe>(recipes);
		this.player = player;

		for (int i = 0; i < recipes.size(); i++) {
			this.recipes.get(i).checkCanCraft(player);
		}

		Collections.sort(this.recipes, new Comparator<Recipe>() {
			public int compare(Recipe r1, Recipe r2) {
				if (r1.canCraft && !r2.canCraft) return -1;
				if (!r1.canCraft && r2.canCraft) return 1;
				return 0;
			}
		});
	}

	public void tick() {
		if (this.input.menu.clicked) this.game.setMenu(null);

		if (this.input.up.clicked) this.selected--;
		if (this.input.down.clicked) this.selected++;

		int len = this.recipes.size();
		if (len == 0) this.selected = 0;
		if (this.selected < 0) this.selected += len;
		if (this.selected >= len) this.selected -= len;

		if (this.input.attack.clicked && len > 0) {
			Recipe r = this.recipes.get(this.selected);
			r.checkCanCraft(this.player);
			if (r.canCraft) {
				r.deductCost(this.player);
				r.craft(this.player);
				Game.instance.soundManager.play(Sound.craft);
			}
			for (int i = 0; i < this.recipes.size(); i++) {
                this.recipes.get(i).checkCanCraft(this.player);
			}
		}
	}

	public void render(Screen screen) {
		Font.renderFrame(screen, "Have", 12, 1, 19, 3);
		Font.renderFrame(screen, "Cost", 12, 4, 19, 11);
		Font.renderFrame(screen, "Crafting", 0, 1, 11, 11);
        this.renderItemList(screen, 0, 1, 11, 11, this.recipes, this.selected);

		if (this.recipes.size() > 0) {
			Recipe recipe = this.recipes.get(this.selected);
			int hasResultItems = this.player.inventory.count(recipe.resultTemplate);
			int xo = 13 * 8;
			screen.render(xo, 2 * 8, recipe.resultTemplate.getSprite(), recipe.resultTemplate.getColor(), 0);
			Font.draw("" + hasResultItems, screen, xo + 8, 2 * 8, Color.get(-1, 555, 555, 555));

			List<Item> costs = recipe.costs;
			for (int i = 0; i < costs.size(); i++) {
				Item item = costs.get(i);
				int yo = (5 + i) * 8;
				screen.render(xo, yo, item.getSprite(), item.getColor(), 0);
				int requiredAmt = 1;
				if (item instanceof ResourceItem) {
					requiredAmt = ((ResourceItem) item).count;
				}
				int has = this.player.inventory.count(item);
				int color = Color.get(-1, 555, 555, 555);
				if (has < requiredAmt) {
					color = Color.get(-1, 222, 222, 222);
				}
				if (has > 99) has = 99;
				Font.draw(requiredAmt + "/" + has, screen, xo + 8, yo, color);
			}
		}
		// renderItemList(screen, 12, 4, 19, 11, recipes.get(selected).costs, -1);
	}
}
