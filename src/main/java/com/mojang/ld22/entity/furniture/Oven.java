package com.mojang.ld22.entity.furniture;

import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.screen.CraftingMenu;

public class Oven extends Furniture {
	public Oven() {
		super("Oven");
        this.col = Color.get(-1, 000, 332, 442);
        this.sprite = 2;
        this.xr = 3;
        this.yr = 2;
	}

	public boolean use(Player player, int attackDir) {
		player.game.setMenu(new CraftingMenu(Crafting.ovenRecipes, player));
		return true;
	}
}
