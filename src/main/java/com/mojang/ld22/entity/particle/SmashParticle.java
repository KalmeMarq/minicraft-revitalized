package com.mojang.ld22.entity.particle;

import com.mojang.ld22.Game;
import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Sound;

public class SmashParticle extends Entity {
	private int time = 0;

	public SmashParticle(int x, int y) {
		this.x = x;
		this.y = y;
		Game.instance.soundManager.play(Sound.monsterHurt);
	}

	public void tick() {
        this.time++;
		if (this.time > 10) {
            this.remove();
		}
	}

	public void render(Screen screen) {
		int col = Color.get(-1, 555, 555, 555);
		screen.render(this.x - 8, this.y - 8, 5 + 12 * 32, col, 2);
		screen.render(this.x, this.y - 8, 5 + 12 * 32, col, 3);
		screen.render(this.x - 8, this.y, 5 + 12 * 32, col, 0);
		screen.render(this.x, this.y, 5 + 12 * 32, col, 1);
	}
}
