package com.mojang.ld22.level.tile;

import com.mojang.ld22.Game;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.sound.Sound;
import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Items;

public class GrassTile extends Tile {
	public GrassTile(int id) {
		super(id);
        this.connectsToGrass = true;
	}

	public void render(Screen screen, Level level, int x, int y) {
		int col = Color.get(level.grassColor, level.grassColor, level.grassColor + 111, level.grassColor + 111);
		int transitionColor = Color.get(level.grassColor - 111, level.grassColor, level.grassColor + 111, level.dirtColor);

		boolean u = !level.getTile(x, y - 1).connectsToGrass;
		boolean d = !level.getTile(x, y + 1).connectsToGrass;
		boolean l = !level.getTile(x - 1, y).connectsToGrass;
		boolean r = !level.getTile(x + 1, y).connectsToGrass;

		if (!u && !l) {
			screen.render(x * 16, y * 16, 0, col, 0);
		} else
			screen.render(x * 16, y * 16, (l ? 11 : 12) + (u ? 0 : 1) * 32, transitionColor, 0);

		if (!u && !r) {
			screen.render(x * 16 + 8, y * 16, 1, col, 0);
		} else
			screen.render(x * 16 + 8, y * 16, (r ? 13 : 12) + (u ? 0 : 1) * 32, transitionColor, 0);

		if (!d && !l) {
			screen.render(x * 16, y * 16 + 8, 2, col, 0);
		} else
			screen.render(x * 16, y * 16 + 8, (l ? 11 : 12) + (d ? 2 : 1) * 32, transitionColor, 0);
		if (!d && !r) {
			screen.render(x * 16 + 8, y * 16 + 8, 3, col, 0);
		} else
			screen.render(x * 16 + 8, y * 16 + 8, (r ? 13 : 12) + (d ? 2 : 1) * 32, transitionColor, 0);
	}

	public void tick(Level level, int xt, int yt) {
		if (this.random.nextInt(40) != 0) return;

		int xn = xt;
		int yn = yt;

		if (this.random.nextBoolean())
			xn += this.random.nextInt(2) * 2 - 1;
		else
			yn += this.random.nextInt(2) * 2 - 1;

		if (level.getTile(xn, yn) == Tile.dirt) {
			level.setTile(xn, yn, this, 0);
		}
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, ItemStack item, int attackDir) {
		if (item.getItem() instanceof ToolItem tool) {
            if (tool.type == ToolType.SHOVEL) {
				if (player.payStamina(4 - tool.level)) {
					level.setTile(xt, yt, Tile.dirt, 0);
					Game.instance.soundManager.play(Sound.monsterHurt);
					if (this.random.nextInt(5) == 0) {
						level.add(new ItemEntity(new ItemStack(Items.SEEDS), xt * 16 + this.random.nextInt(10) + 3, yt * 16 + this.random.nextInt(10) + 3));
						return true;
					}
				}
			}
			if (tool.type == ToolType.HOE) {
				if (player.payStamina(4 - tool.level)) {
					Game.instance.soundManager.play(Sound.monsterHurt);
					if (this.random.nextInt(5) == 0) {
						level.add(new ItemEntity(new ItemStack(Items.SEEDS), xt * 16 + this.random.nextInt(10) + 3, yt * 16 + this.random.nextInt(10) + 3));
						return true;
					}
					level.setTile(xt, yt, Tile.farmland, 0);
					return true;
				}
			}
		}
		return false;

	}
}
