package com.mojang.ld22.screen;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.crafting.Recipe;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import me.kalmemarq.minicraft.ItemStack;

import java.util.List;

public class Menu {
	protected Game game;
	protected InputHandler input;

	public void init(Game game, InputHandler input) {
		this.input = input;
		this.game = game;
	}

	public void tick() {
	}

	public void render(Screen screen) {
	}

	public void renderItemStackList(Screen screen, int xo, int yo, int x1, int y1, List<ItemStack> listItems, int selected) {
		boolean renderCursor = true;
		if (selected < 0) {
			selected = -selected - 1;
			renderCursor = false;
		}
		int w = x1 - xo;
		int h = y1 - yo - 1;
		int i0 = 0;
		int i1 = listItems.size();
		if (i1 > h) i1 = h;
		int io = selected - h / 2;
		if (io > listItems.size() - h) io = listItems.size() - h;
		if (io < 0) io = 0;

		for (int i = i0; i < i1; i++) {
			int rx = (1 + xo) * 8;
			int ry = (i + 1 + yo) * 8;

			ItemStack stack = listItems.get(i + io);
			if (stack.isStackable()) {
				Font.draw("" + stack.getCount(), screen, rx + 8, ry, Color.get(-1, 444, 444, 444));
				stack.getItem().renderInventory(screen, rx + 26, ry, stack);
			} else {
				stack.getItem().renderInventory(screen, rx, ry, stack);
			}
		}

		if (renderCursor) {
			int yy = selected + 1 - io + yo;
			Font.draw(">", screen, (xo) * 8, yy * 8, Color.get(5, 555, 555, 555));
			Font.draw("<", screen, (xo + w) * 8, yy * 8, Color.get(5, 555, 555, 555));
		}
	}

	public void renderRecipeList(Screen screen, int xo, int yo, int x1, int y1, List<Recipe> listItems, int selected) {
		boolean renderCursor = true;
		if (selected < 0) {
			selected = -selected - 1;
			renderCursor = false;
		}
		int w = x1 - xo;
		int h = y1 - yo - 1;
		int i0 = 0;
		int i1 = listItems.size();
		if (i1 > h) i1 = h;
		int io = selected - h / 2;
		if (io > listItems.size() - h) io = listItems.size() - h;
		if (io < 0) io = 0;

		for (int i = i0; i < i1; i++) {
			int rx = (1 + xo) * 8;
			int ry = (i + 1 + yo) * 8;

			Recipe recipe = listItems.get(i + io);
			ItemStack stack = recipe.resultTemplate;

			if (stack.isStackable()) {
				Font.draw("" + stack.getCount(), screen, rx + 8, ry, Color.get(-1, 444, 444, 444));
				recipe.renderInventory(screen, rx, ry, stack);
			} else {
				recipe.renderInventory(screen, rx, ry, stack);
			}
		}

		if (renderCursor) {
			int yy = selected + 1 - io + yo;
			Font.draw(">", screen, (xo) * 8, yy * 8, Color.get(5, 555, 555, 555));
			Font.draw("<", screen, (xo + w) * 8, yy * 8, Color.get(5, 555, 555, 555));
		}
	}
}
