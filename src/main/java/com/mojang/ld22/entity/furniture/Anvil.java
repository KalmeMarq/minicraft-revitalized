package com.mojang.ld22.entity.furniture;

import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.screen.CraftingMenu;

public class Anvil extends Furniture {
	public Anvil() {
        this.xr = 3;
        this.yr = 2;
	}

	public FurnitureType<Anvil> getFurnitureType() {
		return FurnitureType.ANVIL;
	}

	public boolean use(Player player, int attackDir) {
		player.game.setMenu(new CraftingMenu(Crafting.anvilRecipes, player));
		return true;
	}
}
