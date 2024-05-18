package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;

import java.util.List;
import java.util.Random;

public class Entity {
	protected final Random random = new Random();
	public int x, y;
	public int xr = 6;
	public int yr = 6;
	public boolean removed;
	public Level level;

	public void render(Screen screen) {
	}

	public void tick() {
	}

	public void remove() {
        this.removed = true;
	}

	public final void init(Level level) {
		this.level = level;
	}

	public boolean intersects(int x0, int y0, int x1, int y1) {
		return !(this.x + this.xr < x0 || this.y + this.yr < y0 || this.x - this.xr > x1 || this.y - this.yr > y1);
	}

	public boolean blocks(Entity e) {
		return false;
	}

	public void hurt(Mob mob, int dmg, int attackDir) {
	}

	public void hurt(Tile tile, int x, int y, int dmg) {
	}

	public boolean move(int xa, int ya) {
		if (xa != 0 || ya != 0) {
			boolean stopped = xa == 0 || !this.move2(xa, 0);
            if (ya != 0 && this.move2(0, ya)) stopped = false;
			if (!stopped) {
				int xt = this.x >> 4;
				int yt = this.y >> 4;
                this.level.getTile(xt, yt).steppedOn(this.level, xt, yt, this);
			}
			return !stopped;
		}
		return true;
	}

	protected boolean move2(int xa, int ya) {
		if (xa != 0 && ya != 0) throw new IllegalArgumentException("Move2 can only move along one axis at a time!");

		int xto0 = ((this.x) - this.xr) >> 4;
		int yto0 = ((this.y) - this.yr) >> 4;
		int xto1 = ((this.x) + this.xr) >> 4;
		int yto1 = ((this.y) + this.yr) >> 4;

		int xt0 = ((this.x + xa) - this.xr) >> 4;
		int yt0 = ((this.y + ya) - this.yr) >> 4;
		int xt1 = ((this.x + xa) + this.xr) >> 4;
		int yt1 = ((this.y + ya) + this.yr) >> 4;
		boolean blocked = false;
		for (int yt = yt0; yt <= yt1; yt++)
			for (int xt = xt0; xt <= xt1; xt++) {
				if (xt >= xto0 && xt <= xto1 && yt >= yto0 && yt <= yto1) continue;
                this.level.getTile(xt, yt).bumpedInto(this.level, xt, yt, this);
				if (!this.level.getTile(xt, yt).mayPass(this.level, xt, yt, this)) {
					blocked = true;
					return false;
				}
			}
		if (blocked) return false;

		List<Entity> wasInside = this.level.getEntities(this.x - this.xr, this.y - this.yr, this.x + this.xr, this.y + this.yr);
		List<Entity> isInside = this.level.getEntities(this.x + xa - this.xr, this.y + ya - this.yr, this.x + xa + this.xr, this.y + ya + this.yr);
		for (int i = 0; i < isInside.size(); i++) {
			Entity e = isInside.get(i);
			if (e == this) continue;

			e.touchedBy(this);
		}
		isInside.removeAll(wasInside);
		for (int i = 0; i < isInside.size(); i++) {
			Entity e = isInside.get(i);
			if (e == this) continue;

			if (e.blocks(this)) {
				return false;
			}
		}

        this.x += xa;
        this.y += ya;
		return true;
	}

	protected void touchedBy(Entity entity) {
	}

	public boolean isBlockableBy(Mob mob) {
		return true;
	}

	public void touchItem(ItemEntity itemEntity) {
	}

	public boolean canSwim() {
		return false;
	}

	public boolean interact(Player player, Item item, int attackDir) {
		return item.interact(player, this, attackDir);
	}

	public boolean use(Player player, int attackDir) {
		return false;
	}

	public int getLightRadius() {
		return 0;
	}
}