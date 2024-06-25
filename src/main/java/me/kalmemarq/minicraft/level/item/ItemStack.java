/*
 * Minicraft Revitalized.
 * Copyright (C) 2024 KalmeMarq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */

package me.kalmemarq.minicraft.level.item;

import com.fasterxml.jackson.databind.node.ObjectNode;
import me.kalmemarq.minicraft.bso.BsoMapTag;
import me.kalmemarq.minicraft.bso.BsoUtils;
import me.kalmemarq.minicraft.level.Level;
import me.kalmemarq.minicraft.level.entity.Entity;
import me.kalmemarq.minicraft.level.entity.PlayerEntity;
import me.kalmemarq.minicraft.level.tile.Tile;

public class ItemStack {
    private final Item item;
    private int count;
    private BsoMapTag data;

    public ItemStack(Item item) {
        this(item, 1);
    }

    public ItemStack(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    public Item getItem() {
        return this.item;
    }

    public BsoMapTag getData() {
        return this.data;
    }

    public BsoMapTag getOrCreateData() {
        if (this.data == null) {
            this.data = new BsoMapTag();
        }
        return this.data;
    }

    public boolean interactOn(Tile tile, Level level, int xt, int yt, PlayerEntity player, int attackDir) {
        if (this.item != null) {
            return this.item.interactOn(tile, level, xt, yt, player, this, attackDir);
        }
        return false;
    }

    public boolean interact(PlayerEntity player, Entity entity, int attackDir) {
        return this.item != null && this.item.interact(player, entity, attackDir);
    }

    public boolean canAttack() {
        return this.item != null && this.item.canAttack();
    }

    public int getAttackDamageBonus(Entity entity) {
        return this.item == null ? 0 : this.item.getAttackDamageBonus(entity);
    }

    public int getMaxStackSize() {
        return this.item.getMaxStackSize();
    }

    public boolean isStackable() {
        return this.getMaxStackSize() > 1;
    }

    public ItemStack copy() {
        return new ItemStack(this.item, this.count);
    }

    public void increment(int amount) {
        this.count += amount;
    }
    public void decrement(int amount) {
        this.count -= amount;
    }

    public int getCount() {
        return this.count;
    }

    public boolean isOfSame(ItemStack other) {
        if (this.item != other.item) {
            return false;
        }

        if (this.count <= 0 || other.count <= 0) {
            return false;
        }

        if ((this.data == null && other.data != null) || this.data != null && other.data == null) {
            return false;
        }

        if (this.data != null) {
            return this.data.equals(other.data);
        }

        return true;
    }

    @Override
    public String toString() {
        return "{item=" + (this.item == null ? "air" : this.item.getStringId()) + ",count=" + this.count + "}";
    }

    public boolean isDepleted() {
        return this.count <= 0;
    }

    public BsoMapTag write(BsoMapTag obj) {
        obj.put("item", Items.REGISTRY.getStringId(this.item));
        obj.put("count", (short) this.count);
        if (this.data != null) obj.put("data", this.data);
        return obj;
    }

    public static ItemStack fromBso(BsoMapTag map) {
        Item resItem = Items.REGISTRY.getByStringId(map.getString("item"));
        return new ItemStack(resItem, map.containsKey("count") ? map.getShort("count") : 1);
    }

    public static ItemStack fromJson(ObjectNode node) {
        Item resItem = Items.REGISTRY.getByStringId(node.get("item").textValue());
        return new ItemStack(resItem, node.has("count") ? node.get("count").shortValue() : 1);
    }
}
