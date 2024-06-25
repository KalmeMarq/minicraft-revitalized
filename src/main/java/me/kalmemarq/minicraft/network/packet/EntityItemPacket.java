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

package me.kalmemarq.minicraft.network.packet;

import io.netty.buffer.ByteBuf;
import me.kalmemarq.minicraft.bso.BsoMapTag;
import me.kalmemarq.minicraft.level.item.ItemStack;
import me.kalmemarq.minicraft.network.PacketBufUtils;

import java.util.Map;
import java.util.UUID;

public class EntityItemPacket extends EntityPacket {
    private ItemStack stack;
    private double zz;

    public EntityItemPacket() {
    }

    public EntityItemPacket(int type, UUID uuid, int x, int y, double zz, ItemStack stack, Map<String, Integer> tracked) {
       super(type, uuid, x, y, 0, tracked);
       this.stack = stack;
       this.zz = zz;
    }

    @Override
    public void write(ByteBuf buffer) {
        super.write(buffer);
        buffer.writeDouble(this.zz);
        PacketBufUtils.writeBso(buffer, this.stack.write(new BsoMapTag()));
    }

    @Override
    public void read(ByteBuf buffer) {
        super.read(buffer);
        this.zz = buffer.readDouble();
        this.stack = ItemStack.fromBso((BsoMapTag) PacketBufUtils.readBso(buffer));
    }

    public double getZz() {
        return this.zz;
    }

    public ItemStack getStack() {
        return this.stack;
    }
}
