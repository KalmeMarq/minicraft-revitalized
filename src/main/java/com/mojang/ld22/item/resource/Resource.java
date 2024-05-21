package com.mojang.ld22.item.resource;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

public class Resource {
	public final int sprite;
	public final int color;

	public Resource(int sprite, int color) {
		this.sprite = sprite;
		this.color = color;
	}

	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
		return false;
	}
}
