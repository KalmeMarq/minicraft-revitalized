package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import me.kalmemarq.minicraft.Translation;

public class GameMenu extends Menu {
	private int selected = 0;
	private final String[] options = { "minicraft.menu.game_menu.continue", "minicraft.menu.how_to_play", "minicraft.menu.about", "minicraft.menu.language", "minicraft.menu.game_menu.quit" };

	@Override
	public void tick() {
		if (this.input.up.clicked) this.selected--;
		if (this.input.down.clicked) this.selected++;

		int len = this.options.length;
		if (this.selected < 0) this.selected += len;
		if (this.selected >= len) this.selected -= len;

		if (this.input.attack.clicked || this.input.menu.clicked) {
			if (this.selected == 0) {
				this.game.setMenu(null);
			}

			if (this.selected == 1) {
				this.game.setMenu(new InstructionsMenu(this));
			}

			if (this.selected == 2) {
				this.game.setMenu(new AboutMenu(this));
			}

			if (this.selected == 3) {
				this.game.setMenu(new LanguageMenu(this));
			}

			if (this.selected == 4) {
				this.game.playNetworkHandler.getConnection().disconnect("Quit this shithole");
				this.game.resetGame(false);
				this.game.playNetworkHandler = null;
				this.game.setMenu(new TitleMenu());
			}
		}

		if (this.input.back.clicked) this.game.setMenu(null);
	}

	@Override
	public void render(Screen screen) {
		Font.renderFrame(screen, Translation.translate("minicraft.menu.game_menu"), 1, 2, 18, 9);

		for (int i = 0; i < this.options.length; i++) {
			String msg = Translation.translate(this.options[i]);
			int col = Color.get(-1, 222, 222, 222);
			if (i == this.selected) {
				msg = "> " + msg + " <";
				col = Color.get(-1, 555, 555, 555);
			}
			Font.draw(msg, screen, (screen.w - msg.length() * 8) / 2, (8 + i) * 8 - 36, col);
		}
	}
}
