package com.mojang.ld22.screen;

import com.mojang.ld22.Game;
import com.mojang.ld22.InputHandler;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import me.kalmemarq.minicraft.Translation;

public class LanguageMenu extends Menu {
	private final Menu parent;
	private int selected;

	public LanguageMenu(Menu parent) {
		this.parent = parent;
	}

	@Override
	public void init(Game game, InputHandler input) {
		super.init(game, input);
		int i = 0;
		for (String code : Translation.metadata.keySet()) {
			if (Translation.currentCode.equals(code)) {
				this.selected = i;
				break;
			}
			++i;
		}
	}

	public void tick() {
		if (this.input.up.clicked) this.selected--;
		if (this.input.down.clicked) this.selected++;

		int len = Translation.metadata.size();
		if (this.selected < 0) this.selected += len;
		if (this.selected >= len) this.selected -= len;

		if (this.input.attack.clicked) {
			this.game.setMenu(this.parent);
		} else if (this.input.menu.clicked) {
			int i = 0;
			for (String code : Translation.metadata.keySet()) {
				if (this.selected == i) {
					Translation.load(code);
					this.game.setMenu(this.parent);
					break;
				}
				++i;
			}
		}
	}

	public void render(Screen screen) {
		screen.clear(0);
		screen.renderBackgroundRGBA(0, 0, 0xFF090909);

		Font.draw(Translation.translate("minicraft.menu.language"), screen, 4 * 8 + 8, 8, Color.get(0, 555, 555, 555));

		int i = 0;
		for (Translation.LanguageMetadata metadata : Translation.metadata.values()) {
			int col = Color.get(0, 222, 222, 222);
			String msg = metadata.name() + " (" + metadata.region() + ")";
			if (i == this.selected) {
				msg = "> " + msg + " <";
				col = Color.get(0, 555, 555, 555);
			}
			Font.draw(msg, screen, (screen.w - msg.length() * 8) / 2, (8 + i) * 8, col);
			++i;
		}
	}
}
