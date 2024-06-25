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

public class LevelDataPacket extends Packet {
    private int width;
    private int height;
    private long seed;
    private byte[] tiles;

    public LevelDataPacket() {
    }

    public LevelDataPacket(int width, int height, long seed, byte[] tiles) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.tiles = tiles;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(this.width);
        buffer.writeInt(this.height);
        buffer.writeLong(this.seed);
        buffer.writeBytes(this.tiles);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.width = buffer.readInt();
        this.height = buffer.readInt();
        this.seed = buffer.readLong();
        this.tiles = new byte[this.width * this.height];
        buffer.readBytes(this.tiles, 0, this.tiles.length);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public long getSeed() {
        return this.seed;
    }

    public byte[] getTiles() {
        return this.tiles;
    }
}
