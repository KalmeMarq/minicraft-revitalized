package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;

import java.util.List;

public class Spark extends Entity {
	private final int lifeTime;
	public double xa, ya;
	public double xx, yy;
	private int time;
	private final AirWizard owner;

	public Spark(AirWizard owner, double xa, double ya) {
		this.owner = owner;
        this.xx = this.x = owner.x;
        this.yy = this.y = owner.y;
        this.xr = 0;
        this.yr = 0;

		this.xa = xa;
		this.ya = ya;

        this.lifeTime = 60 * 10 + this.random.nextInt(30);
	}

	public void tick() {
        this.time++;
		if (this.time >= this.lifeTime) {
            this.remove();
			return;
		}
        this.xx += this.xa;
        this.yy += this.ya;
        this.x = (int) this.xx;
        this.y = (int) this.yy;
		List<Entity> toHit = this.level.getEntities(this.x, this.y, this.x, this.y);
        for (Entity e : toHit) {
            if (e instanceof Mob && !(e instanceof AirWizard)) {
                e.hurt(this.owner, 1, ((Mob) e).dir ^ 1);
            }
        }
	}

	public boolean isBlockableBy(Mob mob) {
		return false;
	}

	public void render(Screen screen) {
		if (this.time >= this.lifeTime - 6 * 20) {
			if (this.time / 6 % 2 == 0) return;
		}

		int xt = 8;
		int yt = 13;

		screen.render(this.x - 4, this.y - 4 - 2, xt + yt * 32, Color.get(-1, 555, 555, 555), this.random.nextInt(4));
		screen.render(this.x - 4, this.y - 4 + 2, xt + yt * 32, Color.get(-1, 000, 000, 000), this.random.nextInt(4));
	}
}
