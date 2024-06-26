package com.mojang.ld22.screen;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.sound.Sound;
import me.kalmemarq.minicraft.Translation;

public class TitleMenu extends Menu {
	private int selected = 0;

	private String[] options;

	public TitleMenu() {
	}

	@Override
	public void init(Game game, InputHandler input) {
		super.init(game, input);

		this.options = new String[]{
			Translation.translate("minicraft.menu.play"),
			Translation.translate("minicraft.menu.how_to_play"),
			Translation.translate("minicraft.menu.about"),
			Translation.translate("minicraft.menu.language"),
			Translation.translate("minicraft.menu.quit")
		};
	}

	public void tick() {
		if (this.input.up.clicked) this.selected--;
		if (this.input.down.clicked) this.selected++;

		int len = this.options.length;
		if (this.selected < 0) this.selected += len;
		if (this.selected >= len) this.selected -= len;

		if (this.input.attack.clicked || this.input.menu.clicked) {
			if (this.selected == 0) {
				Game.instance.soundManager.play(Sound.test);
                this.game.setMenu(new ConnectMenu());
			}
			if (this.selected == 1) this.game.setMenu(new InstructionsMenu(this));
			if (this.selected == 2) this.game.setMenu(new AboutMenu(this));
			if (this.selected == 3) this.game.setMenu(new LanguageMenu(this));
			if (this.selected == 4) this.game.running = false;
		}
	}

	public void render(Screen screen) {
		screen.clear(0);
		screen.renderBackgroundRGBA(0, 0, 0xFF090909);

		int h = 2;
		int w = 13;
		int titleColor = Color.get(0, 10, 131, 551);
		int xo = (screen.w - w * 8) / 2;
		int yo = 24;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				screen.render(xo + x * 8, yo + y * 8, x + (y + 6) * 32, titleColor, 0);
			}
		}

		for (int i = 0; i < this.options.length; i++) {
			String msg = this.options[i];
			int col = Color.get(0, 222, 222, 222);
			if (i == this.selected) {
				msg = "> " + msg + " <";
				col = Color.get(0, 555, 555, 555);
			}
			Font.draw(msg, screen, (screen.w - msg.length() * 8) / 2, (8 + i) * 8, col);
		}

		Font.draw("(Arrow keys,X and C)", screen, 0, screen.h - 8, Color.get(0, 111, 111, 111));
	}
}
