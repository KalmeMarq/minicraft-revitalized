package com.mojang.ld22.entity.particle;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class TextParticle extends Entity {
	private final String msg;
	private final int col;
	private int time = 0;
	public double xa, ya, za;
	public double xx, yy, zz;

	public TextParticle(String msg, int x, int y, int col) {
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

	public void render(Screen screen) {
//		Font.draw(msg, screen, x - msg.length() * 4, y, Color.get(-1, 0, 0, 0));
		Font.draw(this.msg, screen, this.x - this.msg.length() * 4 + 1, this.y - (int) (this.zz) + 1, Color.get(-1, 0, 0, 0));
		Font.draw(this.msg, screen, this.x - this.msg.length() * 4, this.y - (int) (this.zz), this.col);
	}

}
