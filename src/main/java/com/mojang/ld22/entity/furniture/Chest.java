package com.mojang.ld22.entity.furniture;

import com.mojang.ld22.entity.Inventory;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.screen.ContainerMenu;
import me.kalmemarq.minicraft.ItemStack;
import me.kalmemarq.minicraft.Items;
import me.kalmemarq.minicraft.bso.BsoListTag;
import me.kalmemarq.minicraft.bso.BsoMapTag;
import me.kalmemarq.minicraft.bso.BsoTag;

public class Chest extends Furniture {
	public Inventory inventory = new Inventory();

	@Override
	public FurnitureType<Chest> getFurnitureType() {
		return FurnitureType.CHEST;
	}

	@Override
	public void read(BsoMapTag data) {
		BsoListTag list = data.getList("contents");
		if (list != null) {
			for (BsoTag item : list) {
				BsoMapTag obj = (BsoMapTag) item;
				this.inventory.itemStacks.add(new ItemStack(Items.REGISTRY.getByNumericId(obj.getInt("id")), obj.getShort("count")));
			}
		}
	}

	@Override
	public ItemStack beforeGivenItem(ItemStack stack) {
		BsoListTag list = new BsoListTag();
		for (ItemStack itemStack : this.inventory.itemStacks) {
			list.add(itemStack.write(new BsoMapTag()));
		}
		stack.getOrCreateData().put("contents", list);
		return stack;
	}

	public boolean use(Player player, int attackDir) {
		player.game.setMenu(new ContainerMenu(player, "Chest", this.inventory));
		return true;
	}
}
