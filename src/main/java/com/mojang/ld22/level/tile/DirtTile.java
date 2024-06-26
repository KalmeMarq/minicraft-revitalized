package com.mojang.ld22.level.tile;

import com.mojang.ld22.Game;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.sound.Sound;

import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Items;

public class DirtTile extends Tile {
	public DirtTile(int id) {
		super(id);
	}

	public void render(Screen screen, Level level, int x, int y) {
		int col = Color.get(level.dirtColor, level.dirtColor, level.dirtColor - 111, level.dirtColor - 111);
		screen.render(x * 16, y * 16, 0, col, 0);
		screen.render(x * 16 + 8, y * 16, 1, col, 0);
		screen.render(x * 16, y * 16 + 8, 2, col, 0);
		screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, ItemStack item, int attackDir) {
		if (item.getItem() instanceof ToolItem tool) {
            if (tool.type == ToolType.SHOVEL) {
				if (player.payStamina(4 - tool.level)) {
					level.setTile(xt, yt, Tile.hole, 0);
					level.add(new ItemEntity(new ItemStack(Items.DIRT), xt * 16 + this.random.nextInt(10) + 3, yt * 16 + this.random.nextInt(10) + 3));
					Game.instance.soundManager.play(Sound.monsterHurt);
					return true;
				}
			}
			if (tool.type == ToolType.HOE) {
				if (player.payStamina(4 - tool.level)) {
					level.setTile(xt, yt, Tile.farmland, 0);
					Game.instance.soundManager.play(Sound.monsterHurt);
					return true;
				}
			}
		}
		return false;
	}
}
