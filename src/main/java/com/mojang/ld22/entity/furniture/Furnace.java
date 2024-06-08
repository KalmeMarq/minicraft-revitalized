package com.mojang.ld22.entity.furniture;

import com.mojang.ld22.Game;
import com.mojang.ld22.crafting.Crafting;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.screen.CraftingMenu;

public class Furnace extends Furniture {
	public Furnace() {
        this.xr = 3;
        this.yr = 2;
	}

	@Override
	public FurnitureType<Furnace> getFurnitureType() {
		return FurnitureType.FURNACE;
	}

	public boolean use(Player player, int attackDir) {
		if (Game.instance == null) {
			System.out.println("Calling from server. It's suppose to but it aint done yet");
		} else {
			Game.instance.setMenu(new CraftingMenu(Crafting.furnaceRecipes, player));
		}
		return true;
	}
}
