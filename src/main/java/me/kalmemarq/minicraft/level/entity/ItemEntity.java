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

import me.kalmemarq.minicraft.level.Level;
import me.kalmemarq.minicraft.level.item.ItemStack;

public class ItemEntity extends Entity {
    private final int lifeTime;
    protected int walkDist = 0;
    protected int dir = 0;
    public int hurtTime = 0;
    protected int xKnockback, yKnockback;
    public double xa, ya, za;
    public double xx, yy, zz;
    public ItemStack stack;
    private int time = 0;

    public ItemEntity(Level level, ItemStack stack, int x, int y) {
        super(level);
        this.stack = stack;
        this.xx = this.x = x;
        this.yy = this.y = y;
        this.xr = 3;
        this.yr = 3;

        this.zz = 2;
        this.xa = this.random.nextGaussian() * 0.3;
        this.ya = this.random.nextGaussian() * 0.2;
        this.za = this.random.nextFloat() * 0.7 + 1;

        this.lifeTime = 60 * 10 + this.random.nextInt(60);

        this.data.put("time", this.time);
        this.data.put("lifeTime", this.lifeTime);
        this.dataDirty = true;
    }

    public void tick() {
        this.time++;
        if (this.time >= this.lifeTime) {
            this.remove();
            return;
        }
        this.data.put("time", this.time);
        this.dataDirty = true;

        this.xx += this.xa;
        this.yy += this.ya;
        this.zz += this.za;
        if (this.zz < 0) {
            this.zz = 0;
            this.za *= -0.5;
            this.xa *= 0.6;
            this.ya *= 0.6;
        }
        this.za -= 0.15;
        int ox = this.x;
        int oy = this.y;
        int nx = (int) this.xx;
        int ny = (int) this.yy;
        int expectedx = nx - this.x;
        int expectedy = ny - this.y;
        this.move(nx - this.x, ny - this.y);
        int gotx = this.x - ox;
        int goty = this.y - oy;
        this.xx += gotx - expectedx;
        this.yy += goty - expectedy;

        if (this.hurtTime > 0) this.hurtTime--;
    }

    @Override
    public boolean isBlockableBy(MobEntity mob) {
        return false;
    }

    protected void touchedBy(Entity entity) {
        if (this.time > 30) {
            System.out.println("touched by someone bitch");
            entity.touchItem(this);
        }
    }

    public void take(PlayerEntity player) {
        if (this.stack != null) this.stack.getItem().onTake(this);
        this.remove();
    }
}
