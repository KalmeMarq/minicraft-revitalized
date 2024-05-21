package com.mojang.ld22.entity;

import com.mojang.ld22.Game;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Sound;

public class AirWizard extends Mob {
	private int xa, ya;
	private int randomWalkTime = 0;
	private int attackDelay = 0;
	private int attackTime = 0;
	private int attackType = 0;

	public AirWizard() {
        this.x = this.random.nextInt(64 * 16);
        this.y = this.random.nextInt(64 * 16);
        this.health = this.maxHealth = 2000;
	}

	public void tick() {
		super.tick();

		if (this.attackDelay > 0) {
            this.dir = (this.attackDelay - 45) / 4 % 4;
            this.dir = (this.dir * 2 % 4) + (this.dir / 2);
			if (this.attackDelay < 45) {
                this.dir = 0;
			}
            this.attackDelay--;
			if (this.attackDelay == 0) {
                this.attackType = 0;
				if (this.health < 1000) this.attackType = 1;
				if (this.health < 200) this.attackType = 2;
                this.attackTime = 60 * 2;
			}
			return;
		}

		if (this.attackTime > 0) {
            this.attackTime--;
			double dir = this.attackTime * 0.25 * (this.attackTime % 2 * 2 - 1);
			double speed = (0.7) + this.attackType * 0.2;
            this.level.add(new Spark(this, Math.cos(dir) * speed, Math.sin(dir) * speed));
			return;
		}

		if (this.level.player != null && this.randomWalkTime == 0) {
			int xd = this.level.player.x - this.x;
			int yd = this.level.player.y - this.y;
			if (xd * xd + yd * yd < 32 * 32) {
                this.xa = 0;
                this.ya = 0;
				if (xd < 0) this.xa = +1;
				if (xd > 0) this.xa = -1;
				if (yd < 0) this.ya = +1;
				if (yd > 0) this.ya = -1;
			} else if (xd * xd + yd * yd > 80 * 80) {
                this.xa = 0;
                this.ya = 0;
				if (xd < 0) this.xa = -1;
				if (xd > 0) this.xa = +1;
				if (yd < 0) this.ya = -1;
				if (yd > 0) this.ya = +1;
			}
		}

		int speed = (this.tickTime % 4) == 0 ? 0 : 1;
		if (!this.move(this.xa * speed, this.ya * speed) || this.random.nextInt(100) == 0) {
            this.randomWalkTime = 30;
            this.xa = (this.random.nextInt(3) - 1);
            this.ya = (this.random.nextInt(3) - 1);
		}
		if (this.randomWalkTime > 0) {
            this.randomWalkTime--;
			if (this.level.player != null && this.randomWalkTime == 0) {
				int xd = this.level.player.x - this.x;
				int yd = this.level.player.y - this.y;
				if (this.random.nextInt(4) == 0 && xd * xd + yd * yd < 50 * 50) {
					if (this.attackDelay == 0 && this.attackTime == 0) {
                        this.attackDelay = 60 * 2;
					}
				}
			}
		}
	}

	protected void doHurt(int damage, int attackDir) {
		super.doHurt(damage, attackDir);
		if (this.attackDelay == 0 && this.attackTime == 0) {
            this.attackDelay = 60 * 2;
		}
	}

	public void render(Screen screen) {
		int xt = 8;
		int yt = 14;

		int flip1 = (this.walkDist >> 3) & 1;
		int flip2 = (this.walkDist >> 3) & 1;

		if (this.dir == 1) {
			xt += 2;
		}
		if (this.dir > 1) {

			flip1 = 0;
			flip2 = ((this.walkDist >> 4) & 1);
			if (this.dir == 2) {
				flip1 = 1;
			}
			xt += 4 + ((this.walkDist >> 3) & 1) * 2;
		}

		int xo = this.x - 8;
		int yo = this.y - 11;

		int col1 = Color.get(-1, 100, 500, 555);
		int col2 = Color.get(-1, 100, 500, 532);
		if (this.health < 200) {
			if (this.tickTime / 3 % 2 == 0) {
				col1 = Color.get(-1, 500, 100, 555);
				col2 = Color.get(-1, 500, 100, 532);
			}
		} else if (this.health < 1000) {
			if (this.tickTime / 5 % 4 == 0) {
				col1 = Color.get(-1, 500, 100, 555);
				col2 = Color.get(-1, 500, 100, 532);
			}
		}
		if (this.hurtTime > 0) {
			col1 = Color.get(-1, 555, 555, 555);
			col2 = Color.get(-1, 555, 555, 555);
		}

		screen.render(xo + 8 * flip1, yo, xt + yt * 32, col1, flip1);
		screen.render(xo + 8 - 8 * flip1, yo, xt + 1 + yt * 32, col1, flip1);
		screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col2, flip2);
		screen.render(xo + 8 - 8 * flip2, yo + 8, xt + 1 + (yt + 1) * 32, col2, flip2);
	}

	protected void touchedBy(Entity entity) {
		if (entity instanceof Player) {
			entity.hurt(this, 3, this.dir);
		}
	}

	protected void die() {
		super.die();
		if (this.level.player != null) {
            this.level.player.score += 1000;
            this.level.player.gameWon();
		}
		if (Game.USE_OPENAL) Game.instance.soundManager.play("/sounds/bossdeath.ogg", 1.0f, 1.0f);
		else Game.instance.soundManager.play(Sound.bossdeath);
	}
}
