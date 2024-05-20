package com.mojang.ld22.entity.furniture;

import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.screen.CraftingMenu;

public class Oven extends Furniture {
	public Oven() {
        this.xr = 3;
        this.yr = 2;
	}

	@Override
	public FurnitureType<Oven> getFurnitureType() {
		return FurnitureType.OVEN;
	}

	public boolean use(Player player, int attackDir) {
		player.game.setMenu(new CraftingMenu(Crafting.ovenRecipes, player));
		return true;
	}
}
