package com.mojang.ld22.item;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Translation;

import java.util.Random;

public class ToolItem extends Item {
	private final Random random = new Random();

	public static final int[] LEVEL_COLORS = {
			Color.get(-1, 100, 321, 431),
			Color.get(-1, 100, 321, 111),
			Color.get(-1, 100, 321, 555),
			Color.get(-1, 100, 321, 550),
			Color.get(-1, 100, 321, 055)
	};

	public ToolType type;
	public int level = 0;

	public ToolItem(ToolType type, int level) {
		this.type = type;
		this.level = level;
	}

	public int getColor() {
		return LEVEL_COLORS[this.level];
	}

	public int getSprite() {
		return this.type.sprite + 5 * 32;
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

	public void onTake(ItemEntity itemEntity) {
	}

	public boolean canAttack() {
		return true;
	}

	public int getAttackDamageBonus(Entity e) {
		if (this.type == ToolType.AXE) {
			return (this.level + 1) * 2 + this.random.nextInt(4);
		}
		if (this.type == ToolType.SWORD) {
			return (this.level + 1) * 3 + this.random.nextInt(2 + this.level * this.level * 2);
		}
		return 1;
	}

	public boolean matches(Item item) {
		if (item instanceof ToolItem other) {
            if (other.type != this.type) return false;
            return other.level == this.level;
        }
		return false;
	}
}
