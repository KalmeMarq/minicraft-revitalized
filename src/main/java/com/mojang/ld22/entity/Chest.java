package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.screen.ContainerMenu;

public class Chest extends Furniture {
	public Inventory inventory = new Inventory();

	public Chest() {
		super("Chest");
        this.col = Color.get(-1, 110, 331, 552);
        this.sprite = 1;
	}

	public boolean use(Player player, int attackDir) {
		player.game.setMenu(new ContainerMenu(player, "Chest", this.inventory));
		return true;
	}
}