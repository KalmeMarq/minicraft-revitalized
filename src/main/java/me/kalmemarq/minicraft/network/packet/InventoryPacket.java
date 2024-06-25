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
import me.kalmemarq.minicraft.network.Packet;
import me.kalmemarq.minicraft.network.PacketBufUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryPacket extends Packet {
    private List<ItemStack> contents;

    public InventoryPacket() {
    }

    public InventoryPacket(List<ItemStack> contents) {
        this.contents = contents;
    }

    @Override
    public void write(ByteBuf buffer) {
        PacketBufUtils.writeUnsignedVarInt(this.contents.size(), buffer);
        for (ItemStack stack : this.contents) {
            BsoMapTag mapTag = new BsoMapTag();
            stack.write(mapTag);
            PacketBufUtils.writeBso(buffer, mapTag);
        }
    }

    @Override
    public void read(ByteBuf buffer) {
        int length = PacketBufUtils.readUnsignedVarInt(buffer);
        this.contents = new ArrayList<>();

        for (int i = 0; i < length; ++i) {
            this.contents.add(ItemStack.fromBso((BsoMapTag) Objects.requireNonNull(PacketBufUtils.readBso(buffer))));
        }
    }

    public List<ItemStack> getContents() {
        return this.contents;
    }
}
