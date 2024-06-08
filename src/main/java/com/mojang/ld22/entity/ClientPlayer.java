package com.mojang.ld22.entity;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.FurnitureItem;

public class ClientPlayer extends Player {
	private Game client;

	public ClientPlayer(Game game, InputHandler input) {
		super(input);
		this.client = game;
		this.isClient = true;
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
		if (this.isSwimming()) {
			yo += 4;
			int waterColor = Color.get(-1, -1, 115, 335);
			if (this.tickTime / 8 % 2 == 0) {
				waterColor = Color.get(-1, 335, 5, 115);
			}
			screen.render(xo, yo + 3, 5 + 13 * 32, waterColor, 0);
			screen.render(xo + 8, yo + 3, 5 + 13 * 32, waterColor, 1);
		}

		if (this.attackTime > 0 && this.attackDir == 1) {
			screen.render(xo, yo - 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 0);
			screen.render(xo + 8, yo - 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 1);
			if (this.attackItem != null) {
				this.attackItem.renderIcon(screen, xo + 4, yo - 4);
			}
		}
		int col = Color.get(-1, 100, 220, 532);
		if (this.hurtTime > 0) {
			col = Color.get(-1, 555, 555, 555);
		}

		if (this.activeItem != null && this.activeItem.getItem() instanceof FurnitureItem) {
			yt += 2;
		}
		screen.render(xo + 8 * flip1, yo, xt + yt * 32, col, flip1);
		screen.render(xo + 8 - 8 * flip1, yo, xt + 1 + yt * 32, col, flip1);
		if (!this.isSwimming()) {
			screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col, flip2);
			screen.render(xo + 8 - 8 * flip2, yo + 8, xt + 1 + (yt + 1) * 32, col, flip2);
		}

		if (this.attackTime > 0 && this.attackDir == 2) {
			screen.render(xo - 4, yo, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 1);
			screen.render(xo - 4, yo + 8, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 3);
			if (this.attackItem != null) {
				this.attackItem.renderIcon(screen, xo - 4, yo + 4);
			}
		}
		if (this.attackTime > 0 && this.attackDir == 3) {
			screen.render(xo + 8 + 4, yo, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 0);
			screen.render(xo + 8 + 4, yo + 8, 7 + 13 * 32, Color.get(-1, 555, 555, 555), 2);
			if (this.attackItem != null) {
				this.attackItem.renderIcon(screen, xo + 8 + 4, yo + 4);
			}
		}
		if (this.attackTime > 0 && this.attackDir == 0) {
			screen.render(xo, yo + 8 + 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 2);
			screen.render(xo + 8, yo + 8 + 4, 6 + 13 * 32, Color.get(-1, 555, 555, 555), 3);
			if (this.attackItem != null) {
				this.attackItem.renderIcon(screen, xo + 4, yo + 8 + 4);
			}
		}

		if (this.activeItem != null && this.activeItem.getItem() instanceof FurnitureItem furnitureItem) {
			furnitureItem.furniture.render(screen, this.x, yo);
		}
	}
}
