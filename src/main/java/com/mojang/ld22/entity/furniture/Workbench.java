package com.mojang.ld22.entity.furniture;

import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.screen.CraftingMenu;

public class Workbench extends Furniture {
	public Workbench() {
        this.xr = 3;
        this.yr = 2;
	}

	@Override
	public FurnitureType<Workbench> getFurnitureType() {
		return FurnitureType.WORKBENCH;
	}

	public boolean use(Player player, int attackDir) {
		player.game.setMenu(new CraftingMenu(Crafting.workbenchRecipes, player));
		return true;
	}
}
