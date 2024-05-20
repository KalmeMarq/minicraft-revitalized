package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import me.kalmemarq.minicraft.Translation;

public class InstructionsMenu extends Menu {
	private final Menu parent;

	public InstructionsMenu(Menu parent) {
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

		Font.draw(Translation.translate("minicraft.menu.how_to_play"), screen, 4 * 8 + 4, 8, Color.get(0, 555, 555, 555));
		Font.drawWithMaxWidth(Translation.translate("minicraft.menu.how_to_play.message"), screen, 4, 3 * 8, Color.get(0, 333, 333, 333), 19);
	}
}
