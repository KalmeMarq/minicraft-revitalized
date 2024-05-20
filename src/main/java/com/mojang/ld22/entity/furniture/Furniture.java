package com.mojang.ld22.entity.furniture;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.PowerGloveItem;
import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Items;
import me.kalmemarq.minicraft.bso.BsoMapTag;

public class Furniture extends Entity {
	private int pushTime = 0;
	private int pushDir = -1;

	private Player shouldTake;

	public Furniture() {
        this.xr = 3;
        this.yr = 3;
	}

	public FurnitureType<?> getFurnitureType() {
		return null;
	}

	public ItemStack beforeGivenItem(ItemStack stack) {
		return stack;
	}

	public void read(BsoMapTag data) {
	}

	public void tick() {
		if (this.shouldTake != null) {
			if (this.shouldTake.activeItem.getItem() instanceof PowerGloveItem) {
                this.remove();
                this.shouldTake.inventory.add(0, this.shouldTake.activeItem);
				if (this instanceof Anvil) {
					this.shouldTake.activeItem = this.beforeGivenItem(new ItemStack(Items.ANVIL));
				}
				if (this instanceof Chest) {
					this.shouldTake.activeItem = this.beforeGivenItem(new ItemStack(Items.CHEST));
				}
				if (this instanceof Furnace) {
					this.shouldTake.activeItem = this.beforeGivenItem(new ItemStack(Items.FURNACE));
				}
				if (this instanceof Lantern) {
					this.shouldTake.activeItem = this.beforeGivenItem(new ItemStack(Items.LANTERN));
				}
				if (this instanceof Oven) {
					this.shouldTake.activeItem = this.beforeGivenItem(new ItemStack(Items.OVEN));
				}
				if (this instanceof Workbench) {
					this.shouldTake.activeItem = this.beforeGivenItem(new ItemStack(Items.WORKBENCH));
				}
			}
            this.shouldTake = null;
		}
		if (this.pushDir == 0) this.move(0, +1);
		if (this.pushDir == 1) this.move(0, -1);
		if (this.pushDir == 2) this.move(-1, 0);
		if (this.pushDir == 3) this.move(+1, 0);
        this.pushDir = -1;
		if (this.pushTime > 0) this.pushTime--;
	}

	public void render(Screen screen) {
		this.getFurnitureType().render(screen, this.x, this.y);
	}

	public boolean blocks(Entity e) {
		return true;
	}

	protected void touchedBy(Entity entity) {
		if (entity instanceof Player && this.pushTime == 0) {
            this.pushDir = ((Player) entity).dir;
            this.pushTime = 10;
		}
	}

	public void take(Player player) {
        this.shouldTake = player;
	}

	@Override
	public int getLightRadius() {
		return this.getFurnitureType().lightRadius;
	}
}
