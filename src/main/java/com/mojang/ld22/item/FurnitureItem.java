package com.mojang.ld22.item;

import com.mojang.ld22.entity.furniture.Furniture;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.furniture.FurnitureType;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Translation;

public class FurnitureItem extends Item {
	public FurnitureType<?> furniture;
	public boolean placed = false;

	public FurnitureItem(FurnitureType<?> furniture) {
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

	public void renderInventory(Screen screen, int x, int y, ItemStack itemStack) {
		screen.render(x, y, this.getSprite(), this.getColor(), 0);
		Font.draw(this.getName(), screen, x + 8, y, Color.get(-1, 555, 555, 555));
	}

	public void onTake(ItemEntity itemEntity) {
	}

	public boolean canAttack() {
		return false;
	}

	@Override
	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, ItemStack stack, int attackDir) {
		Furniture furniture = this.furniture.create();

		if (tile.mayPass(level, xt, yt, furniture)) {
			try {
				furniture.x = xt * 16 + 8;
				furniture.y = yt * 16 + 8;
				if (stack.getData() != null) furniture.read(stack.getData());
				level.add(furniture);
				this.placed = true;
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public boolean isDepleted() {
		return this.placed;
	}

	public String getName() {
		return Translation.translate(this.getTranslationKey());
	}
}
