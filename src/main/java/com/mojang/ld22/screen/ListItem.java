package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Screen;
import me.kalmemarq.minicraft.ItemStack;

public interface ListItem {
	void renderInventory(Screen screen, int i, int j, ItemStack stack);
}
