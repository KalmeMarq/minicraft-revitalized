package com.mojang.ld22.item;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.screen.ListItem;
import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Items;

public class Item implements ListItem {
	private int numericId = -1;
	private String stringId;

	public int getNumericId() {
		if (this.numericId == -1) this.numericId = Items.getNumericId(this);
		return this.numericId;
	}

	public String getStringId() {
		if (this.stringId == null) this.stringId = Items.getStringId(this);
		return this.stringId;
	}

	public String getTranslationKey() {
		return "minicraft.item." + this.getStringId();
	}

	public int getColor() {
		return 0;
	}

	public int getSprite() {
		return 0;
	}

	public void onTake(ItemEntity itemEntity) {
	}

	public void renderInventory(Screen screen, int x, int y, ItemStack stack) {
	}

	public boolean interact(Player player, Entity entity, int attackDir) {
		return false;
	}

	public void renderIcon(Screen screen, int x, int y) {
	}

	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, ItemStack stack, int attackDir) {
		return false;
	}

	public boolean isDepleted() {
		return false;
	}

	public boolean canAttack() {
		return false;
	}

	public int getAttackDamageBonus(Entity e) {
		return 0;
	}

	public String getName() {
		return "";
	}

	public boolean matches(Item item) {
		return item.getClass() == this.getClass();
	}
}
