/*
 * Minicraft Revitalized.
 * Copyright (C) 2024 KalmeMarq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */

package me.kalmemarq.minicraft.level;

import me.kalmemarq.minicraft.level.tile.Tile;

public class Chunk {
	private final int x;
	private final int y;
	private int[] tiles;
	private int[] data;

	public Chunk(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setTile(int x, int y, Tile tile) {
	}

	public void setData(int x, int y, int data) {
	}

	public void setTileAndData(int x, int y, Tile tile, int data) {
	}

	public int getTile(int x, int y) {
		return 0;
	}

	public int getData(int x, int y) {
		return 0;
	}
}
