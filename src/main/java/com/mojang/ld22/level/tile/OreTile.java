package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.particle.SmashParticle;
import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.level.Level;
import me.kalmemarq.minicraft.ItemStack;

public class OreTile extends Tile {
	private final ItemStack toDrop;

	public OreTile(int id, ItemStack toDrop) {
		super(id);
		this.toDrop = toDrop;
	}

	public void render(Screen screen, Level level, int x, int y) {
		int color = (((ResourceItem) this.toDrop.getItem()).resource.color & 0xffffff00) + Color.get(level.dirtColor);
		screen.render(x * 16, y * 16, 17 + 32, color, 0);
		screen.render(x * 16 + 8, y * 16, 18 + 32, color, 0);
		screen.render(x * 16, y * 16 + 8, 17 + 2 * 32, color, 0);
		screen.render(x * 16 + 8, y * 16 + 8, 18 + 2 * 32, color, 0);
	}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return false;
	}

	public void hurt(Level level, int x, int y, Mob source, int dmg, int attackDir) {
        this.hurt(level, x, y, 0);
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, ItemStack item, int attackDir) {
		if (item.getItem() instanceof ToolItem tool) {
            if (tool.type == ToolType.PICKAXE) {
				if (player.payStamina(6 - tool.level)) {
                    this.hurt(level, xt, yt, 1);
					return true;
				}
			}
		}
		return false;
	}

	public void hurt(Level level, int x, int y, int dmg) {
		int damage = level.getData(x, y) + 1;
		level.add(new SmashParticle(x * 16 + 8, y * 16 + 8));
		level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.get(-1, 500, 500, 500)));
		if (dmg > 0) {
			int count = this.random.nextInt(2);
			if (damage >= this.random.nextInt(10) + 3) {
				level.setTile(x, y, Tile.dirt, 0);
				count += 2;
			} else {
				level.setData(x, y, damage);
			}
			for (int i = 0; i < count; i++) {
				level.add(new ItemEntity(this.toDrop.copy(), x * 16 + this.random.nextInt(10) + 3, y * 16 + this.random.nextInt(10) + 3));
			}
		}
	}

	public void bumpedInto(Level level, int x, int y, Entity entity) {
		entity.hurt(this, x, y, 3);
	}
}
