package com.mojang.ld22.item;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.furniture.Furniture;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Translation;

public class PowerGloveItem extends Item {
	public int getColor() {
		return Color.get(-1, 100, 320, 430);
	}

	public int getSprite() {
		return 7 + 4 * 32;
	}

	public void renderIcon(Screen screen, int x, int y) {
		screen.render(x, y, this.getSprite(), this.getColor(), 0);
	}

	public void renderInventory(Screen screen, int x, int y, ItemStack stack) {
		screen.render(x, y, this.getSprite(), this.getColor(), 0);
		Font.draw(this.getName(), screen, x + 8, y, Color.get(-1, 555, 555, 555));
	}

	public String getName() {
		return Translation.translate(this.getTranslationKey());
	}

	public boolean interact(Player player, Entity entity, int attackDir) {
		if (entity instanceof Furniture f) {
            f.take(player);
			return true;
		}
		return false;
	}
}
