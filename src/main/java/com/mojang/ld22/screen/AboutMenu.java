package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class AboutMenu extends Menu {
	private final Menu parent;

	public AboutMenu(Menu parent) {
		this.parent = parent;
	}

	public void tick() {
		if (this.input.attack.clicked || this.input.menu.clicked) {
            this.game.setMenu(this.parent);
		}
	}

	public void render(Screen screen) {
		screen.clear(0);
		screen.renderBackgroundRGBA(0, 0, 0xFF090909);

		Font.draw("About Minicraft", screen, 2 * 8 + 4, 8, Color.get(0, 555, 555, 555));
		Font.draw("Minicraft was made", screen, 4, 3 * 8, Color.get(0, 333, 333, 333));
		Font.draw("by Markus Persson", screen, 4, 4 * 8, Color.get(0, 333, 333, 333));
		Font.draw("For the 22'nd ludum", screen, 4, 5 * 8, Color.get(0, 333, 333, 333));
		Font.draw("dare competition in", screen, 4, 6 * 8, Color.get(0, 333, 333, 333));
		Font.draw("december 2011.", screen, 4, 7 * 8, Color.get(0, 333, 333, 333));
		Font.draw("it is dedicated to", screen, 4, 9 * 8, Color.get(0, 333, 333, 333));
		Font.draw("my father. <3", screen, 4, 10 * 8, Color.get(0, 333, 333, 333));
	}
}
