package com.mojang.ld22.item;

import com.mojang.ld22.entity.Furniture;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

public class FurnitureItem extends Item {
	public Furniture furniture;
	public boolean placed = false;

	public FurnitureItem(Furniture furniture) {
		this.furniture = furniture;
	}

	public int getColor() {
		return this.furniture.col;
	}

	public int getSprite() {
		return this.furniture.sprite + 10 * 32;
	}

	public void renderIcon(Screen screen, int x, int y) {
		screen.render(x, y, this.getSprite(), this.getColor(), 0);
	}

	public void renderInventory(Screen screen, int x, int y) {
		screen.render(x, y, this.getSprite(), this.getColor(), 0);
		Font.draw(this.furniture.name, screen, x + 8, y, Color.get(-1, 555, 555, 555));
	}

	public void onTake(ItemEntity itemEntity) {
	}

	public boolean canAttack() {
		return false;
	}

	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
		if (tile.mayPass(level, xt, yt, this.furniture)) {
            this.furniture.x = xt * 16 + 8;
            this.furniture.y = yt * 16 + 8;
			level.add(this.furniture);
            this.placed = true;
			return true;
		}
		return false;
	}

	public boolean isDepleted() {
		return this.placed;
	}
	
	public String getName() {
		return this.furniture.name;
	}
}