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
import me.kalmemarq.minicraft.network.PacketBufUtils;

import java.util.UUID;

public class GamePacket extends Packet {
    private UUID uuid;
    private int x;
    private int y;
    private int dir;

    public GamePacket() {
    }

    public GamePacket(UUID uuid, int x, int y, int dir) {
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.dir = dir;
    }

    @Override
    public void write(ByteBuf buffer) {
        PacketBufUtils.writeUuid(buffer, this.uuid);
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.dir);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.uuid = PacketBufUtils.readUuid(buffer);
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.dir = buffer.readInt();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getDir() {
        return this.dir;
    }
}
