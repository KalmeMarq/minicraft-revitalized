package com.mojang.ld22.screen;

import com.mojang.ld22.Game;
import com.mojang.ld22.crafting.Recipe;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Sound;
import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Translation;

import java.util.ArrayList;
import java.util.List;

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

		this.recipes.sort((r1, r2) -> {
			if (r1.canCraft && !r2.canCraft) return -1;
			if (!r1.canCraft && r2.canCraft) return 1;
			return 0;
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
			for (Recipe recipe : this.recipes) {
				recipe.checkCanCraft(this.player);
			}
		}
	}

	public void render(Screen screen) {
		Font.renderFrame(screen, Translation.translate("minicraft.menu.crafting.have"), 12, 1, 19, 3);
		Font.renderFrame(screen, Translation.translate("minicraft.menu.crafting.cost"), 12, 4, 19, 11);
		Font.renderFrame(screen, Translation.translate("minicraft.menu.crafting"), 0, 1, 11, 11);
        this.renderRecipeList(screen, 0, 1, 11, 11, this.recipes, this.selected);

		if (!this.recipes.isEmpty()) {
			Recipe recipe = this.recipes.get(this.selected);
			int hasResultItems = this.player.inventory.count(recipe.resultTemplate);
			int xo = 13 * 8;
			screen.render(xo, 2 * 8, recipe.resultTemplate.getItem().getSprite(), recipe.resultTemplate.getItem().getColor(), 0);
			Font.draw("" + hasResultItems, screen, xo + 8, 2 * 8, Color.get(-1, 555, 555, 555));

			List<ItemStack> costs = recipe.costs;
			for (int i = 0; i < costs.size(); i++) {
				ItemStack item = costs.get(i);
				int yo = (5 + i) * 8;
				screen.render(xo, yo, item.getItem().getSprite(), item.getItem().getColor(), 0);
				int requiredAmt = item.getCount();
				int has = this.player.inventory.count(item);
				int color = Color.get(-1, 555, 555, 555);
				if (has < requiredAmt) {
					color = Color.get(-1, 222, 222, 222);
				}
				if (has > 99) has = 99;
				Font.draw(requiredAmt + "/" + has, screen, xo + 8, yo, color);
			}
		}
	}
}
