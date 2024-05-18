package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Screen;

public class LevelTransitionMenu extends Menu {
	private final int dir;
	private int time = 0;

	public LevelTransitionMenu(int dir) {
		this.dir = dir;
	}

	public void tick() {
        this.time += 2;
		if (this.time == 30) this.game.changeLevel(this.dir);
		if (this.time == 60) this.game.setMenu(null);
	}

	public void render(Screen screen) {
		for (int x = 0; x < 20; x++) {
			for (int y = 0; y < 15; y++) {
				int dd = (y + x % 2 * 2 + x / 3) - this.time;
				if (dd < 0 && dd > -30) {
					if (this.dir > 0)
						screen.render(x * 8, y * 8, 0, 0, 0);
					else
						screen.render(x * 8, screen.h - y * 8 - 8, 0, 0, 0);
				}
			}
		}
	}
}
