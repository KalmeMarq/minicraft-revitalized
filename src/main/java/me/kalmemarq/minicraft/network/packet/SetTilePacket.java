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
import me.kalmemarq.minicraft.network.Packet;

public class SetTilePacket extends Packet {
    private int x;
    private int y;
    private byte tile;
    private byte data;

    public SetTilePacket() {
    }

    public SetTilePacket(int x, int y, int tile, int data) {
        this.x = x;
        this.y = y;
        this.tile = (byte) tile;
        this.data = (byte) data;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeByte(this.tile);
        buffer.writeByte(this.data);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.tile = buffer.readByte();
        this.data = buffer.readByte();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public byte getTile() {
        return this.tile;
    }

    public byte getData() {
        return this.data;
    }
}
