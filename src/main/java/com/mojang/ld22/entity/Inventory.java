package com.mojang.ld22.entity;

import com.mojang.ld22.item.Item;
import me.kalmemarq.minicraft.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
	public List<ItemStack> itemStacks = new ArrayList<>();

//	public void add(Item item) {
//        this.add(this.items.size(), item);
//	}
//
//	public void add(int slot, Item item) {
//		if (item instanceof ResourceItem toTake) {
//            ResourceItem has = this.findResource(toTake.resource);
//			if (has == null) {
//                this.items.add(slot, toTake);
//			} else {
//				has.count += toTake.count;
//			}
//		} else {
//            this.items.add(slot, item);
//		}
//	}
//
//	private ResourceItem findResource(Resource resource) {
//		for (int i = 0; i < this.items.size(); i++) {
//			if (this.items.get(i) instanceof ResourceItem has) {
//                if (has.resource == resource) return has;
//			}
//		}
//		return null;
//	}
//
//	public boolean hasResources(Resource r, int count) {
//		ResourceItem ri = this.findResource(r);
//		if (ri == null) return false;
//		return ri.count >= count;
//	}

	public boolean IS_has(ItemStack stack) {
		ItemStack result = this.IS_find(stack);;
		if (result == null) return false;
		return result.getCount() >= stack.getCount();
	}
//
//	public boolean removeResource(Resource r, int count) {
//		ResourceItem ri = this.findResource(r);
//		if (ri == null) return false;
//		if (ri.count < count) return false;
//		ri.count -= count;
//		if (ri.count <= 0) this.items.remove(ri);
//		return true;
//	}

	public boolean IS_remove(ItemStack stack) {
		ItemStack result = this.IS_find(stack);
		if (result == null) return false;
		if (result.getCount() < stack.getCount()) return false;
		result.decrement(stack.getCount());
		if (result.getCount() <= 0) this.itemStacks.remove(result);
		return true;
	}
//
//	public int count(Item item) {
//		if (item instanceof ResourceItem) {
//			ResourceItem ri = this.findResource(((ResourceItem)item).resource);
//			if (ri!=null) return ri.count;
//		} else {
//			int count = 0;
//			for (int i = 0; i< this.items.size(); i++) {
//				if (this.items.get(i).matches(item)) count++;
//			}
//			return count;
//		}
//		return 0;
//	}

	public void IS_add(ItemStack stack) {
		this.IS_add(this.itemStacks.size(), stack);
	}

	public void IS_add(int slot, ItemStack stack) {
		if (stack.getMaxStackSize() == 1) {
			this.itemStacks.add(slot, stack);
		} else {
			ItemStack stack1 = this.IS_find(stack);

			if (stack1 != null && stack1.getMaxStackSize() > 1) {
				stack1.increment(stack.getCount());
			} else {
				this.itemStacks.add(slot, stack);
			}
		}
	}

	private ItemStack IS_find(ItemStack stack) {
		for (ItemStack item : this.itemStacks) {
			if (stack.isOfSame(item)) {
				return item;
			}
		}
		return null;
	}

	public int IS_count(ItemStack stack) {
		int count = 0;
		for (ItemStack item : this.itemStacks) {
			if (stack.isOfSame(item)) {
				count += stack.getCount();
			}
		}
		return count;
	}
}
