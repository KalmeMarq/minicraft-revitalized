package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class DisconnectedMenu extends Menu {
	private final String reason;

	public DisconnectedMenu(String reason) {
		this.reason = reason;
	}

	public void tick() {
		if (this.input.attack.clicked || this.input.menu.clicked) {
            this.game.setMenu(new TitleMenu());
		}
	}

	public void render(Screen screen) {
		screen.clear(0);
		screen.renderBackgroundRGBA(0, 0, 0xFF090909);

		Font.draw("DISCONNECTED", screen, 2 * 8 + 4, 8, Color.get(0, 555, 555, 555));
		Font.drawWithMaxWidth(this.reason, screen, 2 * 8 + 4, 16, Color.get(0, 555, 555, 555), 15);
	}
}
