package com.mojang.ld22.item.resource;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

import java.util.Arrays;
import java.util.List;

public class PlantableResource extends Resource {
	private final List<Tile> sourceTiles;
	private final Tile targetTile;

	public PlantableResource( int sprite, int color, Tile targetTile, Tile... sourceTiles1) {
		this( sprite, color, targetTile, Arrays.asList(sourceTiles1));
	}

	public PlantableResource(int sprite, int color, Tile targetTile, List<Tile> sourceTiles) {
		super(sprite, color);
		this.sourceTiles = sourceTiles;
		this.targetTile = targetTile;
	}

	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
		if (this.sourceTiles.contains(tile)) {
			level.setTile(xt, yt, this.targetTile, 0);
			return true;
		}
		return false;
	}
}
