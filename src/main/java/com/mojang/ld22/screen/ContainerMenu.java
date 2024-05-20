package com.mojang.ld22.screen;

import com.mojang.ld22.entity.Inventory;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;

public class ContainerMenu extends Menu {
	private final Player player;
	private final Inventory container;
	private int selected = 0;
	private final String title;
	private int oSelected;
	private int window = 0;

	public ContainerMenu(Player player, String title, Inventory container) {
		this.player = player;
		this.title = title;
		this.container = container;
	}

	public void tick() {
		if (this.input.menu.clicked) this.game.setMenu(null);

		if (this.input.left.clicked) {
            this.window = 0;
			int tmp = this.selected;
            this.selected = this.oSelected;
            this.oSelected = tmp;
		}
		if (this.input.right.clicked) {
            this.window = 1;
			int tmp = this.selected;
            this.selected = this.oSelected;
            this.oSelected = tmp;
		}

		Inventory i = this.window == 1 ? this.player.inventory : this.container;
		Inventory i2 = this.window == 0 ? this.player.inventory : this.container;

		int len = i.itemStacks.size();
		if (this.selected < 0) this.selected = 0;
		if (this.selected >= len) this.selected = len - 1;

		if (this.input.up.clicked) this.selected--;
		if (this.input.down.clicked) this.selected++;

		if (len == 0) this.selected = 0;
		if (this.selected < 0) this.selected += len;
		if (this.selected >= len) this.selected -= len;

		if (this.input.attack.clicked && len > 0) {
			i2.IS_add(this.oSelected, i.itemStacks.remove(this.selected));
			if (this.selected >= i.itemStacks.size()) this.selected = i.itemStacks.size() - 1;
		}
	}

	public void render(Screen screen) {
		if (this.window == 1) screen.setOffset(6 * 8, 0);
		Font.renderFrame(screen, this.title, 1, 1, 12, 11);
        this.renderItemStackList(screen, 1, 1, 12, 11, this.container.itemStacks, this.window == 0 ? this.selected : -this.oSelected - 1);

		Font.renderFrame(screen, "inventory", 13, 1, 13 + 11, 11);
        this.renderItemStackList(screen, 13, 1, 13 + 11, 11, this.player.inventory.itemStacks, this.window == 1 ? this.selected : -this.oSelected - 1);
		screen.setOffset(0, 0);
	}
}
