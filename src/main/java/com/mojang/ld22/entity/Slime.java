package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;
import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Items;

public class Slime extends Mob {
	private int xa, ya;
	private int jumpTime = 0;
	private final int lvl;

	public Slime(int lvl) {
		this.lvl = lvl;
        this.x = this.random.nextInt(64 * 16);
        this.y = this.random.nextInt(64 * 16);
        this.health = this.maxHealth = lvl * lvl * 5;
	}

	public void tick() {
		super.tick();

		int speed = 1;
		if (!this.move(this.xa * speed, this.ya * speed) || this.random.nextInt(40) == 0) {
			if (this.jumpTime <= -10) {
                this.xa = (this.random.nextInt(3) - 1);
                this.ya = (this.random.nextInt(3) - 1);

				if (this.level.player != null) {
					int xd = this.level.player.x - this.x;
					int yd = this.level.player.y - this.y;
					if (xd * xd + yd * yd < 50 * 50) {
						if (xd < 0) this.xa = -1;
						if (xd > 0) this.xa = +1;
						if (yd < 0) this.ya = -1;
						if (yd > 0) this.ya = +1;
					}

				}

				if (this.xa != 0 || this.ya != 0) this.jumpTime = 10;
			}
		}

        this.jumpTime--;
		if (this.jumpTime == 0) {
            this.xa = this.ya = 0;
		}
	}

	protected void die() {
		super.die();

		int count = this.random.nextInt(2) + 1;
		for (int i = 0; i < count; i++) {
            this.level.add(new ItemEntity(new ItemStack(Items.SLIME), this.x + this.random.nextInt(11) - 5, this.y + this.random.nextInt(11) - 5));
		}

		if (this.level.player != null) {
            this.level.player.score += 25* this.lvl;
		}

	}

	public void render(Screen screen) {
		int xt = 0;
		int yt = 18;

		int xo = this.x - 8;
		int yo = this.y - 11;

		if (this.jumpTime > 0) {
			xt += 2;
			yo -= 4;
		}

		int col = Color.get(-1, 10, 252, 555);
		if (this.lvl == 2) col = Color.get(-1, 100, 522, 555);
		if (this.lvl == 3) col = Color.get(-1, 111, 444, 555);
		if (this.lvl == 4) col = Color.get(-1, 0, 111, 224);

		if (this.hurtTime > 0) {
			col = Color.get(-1, 555, 555, 555);
		}

		screen.render(xo, yo, xt + yt * 32, col, 0);
		screen.render(xo + 8, yo, xt + 1 + yt * 32, col, 0);
		screen.render(xo, yo + 8, xt + (yt + 1) * 32, col, 0);
		screen.render(xo + 8, yo + 8, xt + 1 + (yt + 1) * 32, col, 0);
	}

	protected void touchedBy(Entity entity) {
		if (entity instanceof Player) {
			entity.hurt(this, this.lvl, this.dir);
		}
	}
}
