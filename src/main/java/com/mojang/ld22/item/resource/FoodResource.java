package com.mojang.ld22.item.resource;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

public class FoodResource extends Resource {
	private final int heal;
	private final int staminaCost;

	public FoodResource(int sprite, int color, int heal, int staminaCost) {
		super(sprite, color);
		this.heal = heal;
		this.staminaCost = staminaCost;
	}

	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
		if (player.health < player.maxHealth && player.payStamina(this.staminaCost)) {
			player.heal(this.heal);
			return true;
		}
		return false;
	}
}
