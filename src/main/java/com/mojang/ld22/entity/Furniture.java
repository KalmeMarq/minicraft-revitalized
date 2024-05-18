package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.FurnitureItem;
import com.mojang.ld22.item.PowerGloveItem;

public class Furniture extends Entity {
	private int pushTime = 0;
	private int pushDir = -1;
	public int col, sprite;
	public String name;
	private Player shouldTake;

	public Furniture(String name) {
		this.name = name;
        this.xr = 3;
        this.yr = 3;
	}

	public void tick() {
		if (this.shouldTake != null) {
			if (this.shouldTake.activeItem instanceof PowerGloveItem) {
                this.remove();
                this.shouldTake.inventory.add(0, this.shouldTake.activeItem);
                this.shouldTake.activeItem = new FurnitureItem(this);
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
		screen.render(this.x - 8, this.y - 8 - 4, this.sprite * 2 + 8 * 32, this.col, 0);
		screen.render(this.x, this.y - 8 - 4, this.sprite * 2 + 8 * 32 + 1, this.col, 0);
		screen.render(this.x - 8, this.y - 4, this.sprite * 2 + 8 * 32 + 32, this.col, 0);
		screen.render(this.x, this.y - 4, this.sprite * 2 + 8 * 32 + 33, this.col, 0);
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
}