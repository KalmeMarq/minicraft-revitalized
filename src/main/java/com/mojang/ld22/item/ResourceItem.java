package com.mojang.ld22.item;

import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

public class ResourceItem extends Item {
	public Resource resource;
	public int count = 1;

	public ResourceItem(Resource resource) {
		this.resource = resource;
	}

	public ResourceItem(Resource resource, int count) {
		this.resource = resource;
		this.count = count;
	}

	public int getColor() {
		return this.resource.color;
	}

	public int getSprite() {
		return this.resource.sprite;
	}

	public void renderIcon(Screen screen, int x, int y) {
		screen.render(x, y, this.resource.sprite, this.resource.color, 0);
	}

	public void renderInventory(Screen screen, int x, int y) {
		screen.render(x, y, this.resource.sprite, this.resource.color, 0);
		Font.draw(this.resource.name, screen, x + 32, y, Color.get(-1, 555, 555, 555));
		int cc = this.count;
		if (cc > 999) cc = 999;
		Font.draw("" + cc, screen, x + 8, y, Color.get(-1, 444, 444, 444));
	}

	public String getName() {
		return this.resource.name;
	}

	public void onTake(ItemEntity itemEntity) {
	}

	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
		if (this.resource.interactOn(tile, level, xt, yt, player, attackDir)) {
            this.count--;
			return true;
		}
		return false;
	}

	public boolean isDepleted() {
		return this.count <= 0;
	}

}