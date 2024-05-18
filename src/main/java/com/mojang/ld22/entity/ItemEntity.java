package com.mojang.ld22.entity;

import com.mojang.ld22.Game;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.sound.Sound;

public class ItemEntity extends Entity {
	private final int lifeTime;
	protected int walkDist = 0;
	protected int dir = 0;
	public int hurtTime = 0;
	protected int xKnockback, yKnockback;
	public double xa, ya, za;
	public double xx, yy, zz;
	public Item item;
	private int time = 0;

	public ItemEntity(Item item, int x, int y) {
		this.item = item;
        this.xx = this.x = x;
        this.yy = this.y = y;
        this.xr = 3;
        this.yr = 3;

        this.zz = 2;
        this.xa = this.random.nextGaussian() * 0.3;
        this.ya = this.random.nextGaussian() * 0.2;
        this.za = this.random.nextFloat() * 0.7 + 1;

        this.lifeTime = 60 * 10 + this.random.nextInt(60);
	}

	public void tick() {
        this.time++;
		if (this.time >= this.lifeTime) {
            this.remove();
			return;
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

	public boolean isBlockableBy(Mob mob) {
		return false;
	}

	public void render(Screen screen) {
		if (this.time >= this.lifeTime - 6 * 20) {
			if (this.time / 6 % 2 == 0) return;
		}
		screen.render(this.x - 4, this.y - 4, this.item.getSprite(), Color.get(-1, 0, 0, 0), 0);
		screen.render(this.x - 4, this.y - 4 - (int) (this.zz), this.item.getSprite(), this.item.getColor(), 0);
	}

	protected void touchedBy(Entity entity) {
		if (this.time > 30) entity.touchItem(this);
	}

	public void take(Player player) {
		Game.instance.soundManager.play(Sound.pickup);
		player.score++;
        this.item.onTake(this);
        this.remove();
	}
}
