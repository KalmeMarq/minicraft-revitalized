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
import me.kalmemarq.minicraft.level.entity.particle.TextParticle;
import me.kalmemarq.minicraft.level.tile.Tile;
import me.kalmemarq.minicraft.level.tile.Tiles;

public class MobEntity extends Entity {
    public int walkDist = 0;
    public int swimTimer = 0;
    public int maxHealth = 10;
    public int health = this.maxHealth;
    public int hurtTime = 0;

    public MobEntity(Level level) {
        super(level);
        this.x = this.y = 8;
        this.xr = 4;
        this.yr = 3;
    }

	@Override
	public void write(BsoMapTag map) {
		super.write(map);
		map.put("maxHealth", this.maxHealth);
		map.put("health", this.health);
		map.put("hurtTime", this.hurtTime);
		map.put("swimTimer", this.swimTimer);
		map.put("walkDist", this.walkDist);
	}

	@Override
    public void tick() {
        super.tick();

        if (this.health <= 0) {
//            this.die();
        }

        if (this.hurtTime > 0) this.hurtTime--;
    }

    public boolean findStartPos(Level level) {
        int x = this.random.nextInt(level.width);
        int y = this.random.nextInt(level.height);
        int xx = x * 16 + 8;
        int yy = y * 16 + 8;

        if (level.getTile(x, y).mayPass(level, x, y, this)) {
            this.x = xx;
            this.y = yy;
            return true;
        }

        return false;
    }

    @Override
    public boolean move(int xa, int ya) {
        if (this.isSwimming()) {
            if (this.swimTimer++ % 2 == 0) return true;
        }
        if (xa != 0 || ya != 0) {
            this.walkDist++;
            if (xa < 0) this.dir = 3;
            if (xa > 0) this.dir = 2;
            if (ya < 0) this.dir = 1;
            if (ya > 0) this.dir = 0;
        }
        return super.move(xa, ya);
    }

    public boolean isSwimming() {
        Tile tile = this.level.getTile(this.x >> 4, this.y >> 4);
        return tile == Tiles.WATER || tile == Tiles.LAVA;
    }

    public boolean isSwimmingInLava() {
        Tile tile = this.level.getTile(this.x >> 4, this.y >> 4);
        return tile == Tiles.LAVA;
    }

    public void hurt(MobEntity mob, int damage, int attackDir) {
        this.doHurt(damage, attackDir);
    }

    protected void doHurt(int damage, int attackDir) {
        System.out.println(this.hurtTime);
        if (this.hurtTime > 0) return;

        try {
            this.level.add(new TextParticle(this.level, "" + damage, this.x, this.y, 0xa83692));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        this.level.add();
        this.health -= damage;
//        if (attackDir == 0) this.yKnockback = +6;
//        if (attackDir == 1) this.yKnockback = -6;
//        if (attackDir == 2) this.xKnockback = -6;
//        if (attackDir == 3) this.xKnockback = +6;
        this.hurtTime = 10;
    }

    public void heal(int heal) {
    }
}
