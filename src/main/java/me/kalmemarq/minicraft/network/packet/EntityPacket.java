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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EntityPacket extends Packet {
    private int type;
    private UUID uuid;
    private int x;
    private int y;
    private int dir;
    private Map<String, Integer> tracked = new HashMap<>();

    public EntityPacket() {
    }

    public EntityPacket(UUID uuid, int x, int y) {
        this.type = 1;
        this.uuid = uuid;
        this.x = x;
        this.y = y;
    }

    public EntityPacket(int type, UUID uuid, int x, int y, int dir) {
        this(type, uuid, x, y, dir, Collections.emptyMap());
    }

    public EntityPacket(int type, UUID uuid, int x, int y, int dir, Map<String, Integer> tracked) {
        this.type = type;
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.tracked = tracked;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(this.type);
        PacketBufUtils.writeUuid(buffer, this.uuid);
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeByte(this.dir);
        buffer.writeByte(this.tracked.size());
        for (Map.Entry<String, Integer> entry : this.tracked.entrySet()) {
            PacketBufUtils.writeString(buffer, entry.getKey());
            buffer.writeInt(entry.getValue());
        }
    }

    @Override
    public void read(ByteBuf buffer) {
        this.type = buffer.readInt();
        this.uuid = PacketBufUtils.readUuid(buffer);
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.dir = buffer.readByte();
        int tl = buffer.readUnsignedByte();
        this.tracked = new HashMap<>();
        for (int i = 0; i < tl; ++i) {
            this.tracked.put(PacketBufUtils.readString(buffer), buffer.readInt());
        }
    }

    public int getType() {
        return this.type;
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

    public Set<Map.Entry<String, Integer>> getTracked() {
        return this.tracked.entrySet();
    }
}
