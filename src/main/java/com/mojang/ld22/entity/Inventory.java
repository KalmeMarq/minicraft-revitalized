package com.mojang.ld22.entity;

import me.kalmemarq.minicraft.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
	public List<ItemStack> itemStacks = new ArrayList<>();

	public boolean has(ItemStack stack) {
		ItemStack result = this.find(stack);;
		if (result == null) return false;
		return result.getCount() >= stack.getCount();
	}

	public boolean remove(ItemStack stack) {
		ItemStack result = this.find(stack);
		if (result == null) return false;
		if (result.getCount() < stack.getCount()) return false;
		result.decrement(stack.getCount());
		if (result.getCount() <= 0) this.itemStacks.remove(result);
		return true;
	}

	public void add(ItemStack stack) {
		this.add(this.itemStacks.size(), stack);
	}

	public void add(int slot, ItemStack stack) {
		if (stack.isStackable()) {
			ItemStack stack1 = this.find(stack);

			if (stack1 != null && stack1.getMaxStackSize() > 1) {
				stack1.increment(stack.getCount());
			} else {
				this.itemStacks.add(slot, stack);
			}
		} else {
			this.itemStacks.add(slot, stack);
		}
	}

	private ItemStack find(ItemStack stack) {
		for (ItemStack item : this.itemStacks) {
			if (stack.isOfSame(item)) {
				return item;
			}
		}
		return null;
	}

	public int count(ItemStack stack) {
		int count = 0;
		for (ItemStack item : this.itemStacks) {
			if (stack.isOfSame(item)) {
				count += stack.getCount();
			}
		}
		return count;
	}
}
