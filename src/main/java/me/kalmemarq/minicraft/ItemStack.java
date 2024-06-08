package me.kalmemarq.minicraft;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import me.kalmemarq.minicraft.bso.BsoMapTag;

public class ItemStack {
	private final Item item;
	private int count;
	private BsoMapTag data;

	public ItemStack(Item item) {
		this(item, 1);
	}

	public ItemStack(Item item, int count) {
		this.item = item;
		this.count = count;
	}

	public Item getItem() {
		return this.item;
	}

	public BsoMapTag getData() {
		return this.data;
	}

	public BsoMapTag getOrCreateData() {
		if (this.data == null) {
			this.data = new BsoMapTag();
		}
		return this.data;
	}

	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
		if (this.item != null) {
			return this.item.interactOn(tile, level, xt, yt, player, this, attackDir);
		}
		return false;
	}

	public boolean interact(Player player, Entity entity, int attackDir) {
		return this.item != null && this.item.interact(player, entity, attackDir);
	}

	public boolean canAttack() {
		return this.item != null && this.item.canAttack();
	}

	public void renderIcon(Screen screen, int x, int y) {
		if (this.item != null) {
			this.item.renderIcon(screen, x, y);
		}
	}

	public void renderInventory(Screen screen, int x, int y) {
		if (this.item != null) {
			this.item.renderInventory(screen, x, y, this);
		}
	}

	public int getAttackDamageBonus(Entity entity) {
		return this.item == null ? 0 : this.item.getAttackDamageBonus(entity);
	}

	public int getMaxStackSize() {
		return this.item instanceof ResourceItem ? 999 : 1;
	}

	public boolean isStackable() {
		return this.getMaxStackSize() > 1;
	}

	public ItemStack copy() {
		return new ItemStack(this.item, this.count);
	}

	public void increment(int amount) {
		this.count += amount;
	}
	public void decrement(int amount) {
		this.count -= amount;
	}

	public int getCount() {
		return this.count;
	}

	public BsoMapTag write(BsoMapTag obj) {
		obj.put("id", Items.REGISTRY.getNumericId(this.item));
		obj.put("count", (short) this.count);
		if (this.data != null) obj.put("data", this.data);
		return obj;
	}

	public static ItemStack fromJson(ObjectNode node) {
		Item resItem = Items.REGISTRY.getByStringId(node.get("item").textValue());
		return new ItemStack(resItem, node.has("count") ? node.get("count").shortValue() : 1);
	}

	public boolean isOfSame(ItemStack other) {
		if (this.item != other.item) {
			return false;
		}

		if (this.count <= 0 || other.count <= 0) {
			return false;
		}

		if ((this.data == null && other.data != null) || this.data != null && other.data == null) {
			return false;
		}

		if (this.data != null) {
			return this.data.equals(other.data);
		}

		return true;
	}

	public boolean isDepleted() {
		return this.count <= 0 || this.item.isDepleted();
	}
}
