/*
 * Minicraft Revitalized.
 * Copyright (C) 2024 KalmeMarq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */

package me.kalmemarq.minicraft.level.entity;

import me.kalmemarq.minicraft.bso.BsoMapTag;
import me.kalmemarq.minicraft.level.Level;
import me.kalmemarq.minicraft.level.item.ItemStack;
import me.kalmemarq.minicraft.level.tile.Tile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Entity {
    public UUID uuid = UUID.randomUUID();
    protected final Random random = new Random();
    public int x = 80;
    public int y = 60;
    public int xr = 6;
    public int yr = 6;
    public int dir = 0;
    public int xa = 0;
    public int ya = 0;
    public int tickTime;
    public Level level;
    public Map<String, Integer> data = new HashMap<>();
    public boolean dataDirty = false;
    public boolean removed;

    public Entity(Level level) {
        this.level = level;
    }

    public void tickAi() {
    }

    public void tick() {
        this.tickTime++;

        if (!this.level.isClient()) {
            this.tickAi();
        }
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

    public void remove() {
        this.removed = true;
    }

    public boolean canSwim() {
        return false;
    }

    public boolean blocks(Entity e) {
        return false;
    }

    public boolean intersects(int x0, int y0, int x1, int y1) {
        return !(this.x + this.xr < x0 || this.y + this.yr < y0 || this.x - this.xr > x1 || this.y - this.yr > y1);
    }

    public void hurt(MobEntity mob, int dmg, int attackDir) {
    }

    public void hurt(Tile tile, int x, int y, int dmg) {
    }

    protected void touchedBy(Entity entity) {
    }

    public boolean isBlockableBy(MobEntity mob) {
        return true;
    }

    public void touchItem(ItemEntity itemEntity) {
    }

    public boolean interact(PlayerEntity player, ItemStack item, int attackDir) {
        return item.interact(player, this, attackDir);
    }

    public boolean use(PlayerEntity player, int attackDir) {
        return false;
    }

	public void write(BsoMapTag map) {
		map.put("x", this.x);
		map.put("y", this.y);
		map.put("dir", this.dir);
		map.put("xr", this.xr);
		map.put("yr", this.yr);
		map.put("removed", this.removed);

		BsoMapTag data = new BsoMapTag();
		for (Map.Entry<String, Integer> entry : this.data.entrySet()) {
			data.put(entry.getKey(), entry.getValue());
		}

		map.put("data", data);
	}

    public int getLightRadius() {
        return 0;
    }
}
