package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;
import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Items;

public class Zombie extends Mob {
	private int xa, ya;
	private final int lvl;
	private int randomWalkTime = 0;

	public Zombie(int lvl) {
		this.lvl = lvl;
        this.x = this.random.nextInt(64 * 16);
        this.y = this.random.nextInt(64 * 16);
        this.health = this.maxHealth = lvl * lvl * 10;
	}

	public void tick() {
		super.tick();

		if (this.level.player != null && this.randomWalkTime == 0) {
			int xd = this.level.player.x - this.x;
			int yd = this.level.player.y - this.y;
			if (xd * xd + yd * yd < 50 * 50) {
                this.xa = 0;
                this.ya = 0;
				if (xd < 0) this.xa = -1;
				if (xd > 0) this.xa = +1;
				if (yd < 0) this.ya = -1;
				if (yd > 0) this.ya = +1;
			}
		}

		int speed = this.tickTime & 1;
		if (!this.move(this.xa * speed, this.ya * speed) || this.random.nextInt(200) == 0) {
            this.randomWalkTime = 60;
            this.xa = (this.random.nextInt(3) - 1) * this.random.nextInt(2);
            this.ya = (this.random.nextInt(3) - 1) * this.random.nextInt(2);
		}
		if (this.randomWalkTime > 0) this.randomWalkTime--;
	}

	public void render(Screen screen) {
		int xt = 0;
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

		int col = Color.get(-1, 10, 252, 40);
		if (this.lvl == 2) col = Color.get(-1, 100, 522, 40);
		if (this.lvl == 3) col = Color.get(-1, 111, 444, 40);
		if (this.lvl == 4) col = Color.get(-1, 0, 111, 16);
		if (this.hurtTime > 0) {
			col = Color.get(-1, 555, 555, 555);
		}

		screen.render(xo + 8 * flip1, yo, xt + yt * 32, col, flip1);
		screen.render(xo + 8 - 8 * flip1, yo, xt + 1 + yt * 32, col, flip1);
		screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col, flip2);
		screen.render(xo + 8 - 8 * flip2, yo + 8, xt + 1 + (yt + 1) * 32, col, flip2);
	}

	protected void touchedBy(Entity entity) {
		if (entity instanceof Player) {
			entity.hurt(this, this.lvl + 1, this.dir);
		}
	}

	protected void die() {
		super.die();

		int count = this.random.nextInt(2) + 1;
		for (int i = 0; i < count; i++) {
            this.level.add(new ItemEntity(new ItemStack(Items.CLOTH), this.x + this.random.nextInt(11) - 5, this.y + this.random.nextInt(11) - 5));
		}

		if (this.level.player != null) {
            this.level.player.score += 50 * this.lvl;
		}
	}
}
