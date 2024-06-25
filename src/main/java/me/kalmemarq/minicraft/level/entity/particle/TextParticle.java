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

package me.kalmemarq.minicraft.level.entity.particle;

import me.kalmemarq.minicraft.level.Level;

public class TextParticle extends Particle {
    public final String msg;
    public final int col;
    public int time = 0;
    public double xa, ya, za;
    public double xx, yy, zz;

    public TextParticle(Level level, String msg, int x, int y, int col) {
        super(level);
        this.msg = msg;
        this.x = x;
        this.y = y;
        this.col = col;
        this.xx = x;
        this.yy = y;
        this.zz = 2;
        this.xa = this.random.nextGaussian() * 0.3;
        this.ya = this.random.nextGaussian() * 0.2;
        this.za = this.random.nextFloat() * 0.7 + 2;
    }

    public void tick() {
        this.time++;
        if (this.time > 60) {
            this.remove();
        }
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
        this.x = (int) this.xx;
        this.y = (int) this.yy;
    }
}
