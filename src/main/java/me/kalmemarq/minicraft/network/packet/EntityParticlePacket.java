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
import me.kalmemarq.minicraft.network.PacketBufUtils;

import java.util.UUID;

public class EntityParticlePacket extends EntityPacket {
    private String message;
    private int color;

    public EntityParticlePacket() {
    }

    public EntityParticlePacket(int type, UUID uuid, int x, int y, String message, int color) {
       super(type, uuid, x, y, 0);
       this.message = message;
       this.color = color;
    }

    @Override
    public void write(ByteBuf buffer) {
        super.write(buffer);
        PacketBufUtils.writeString(buffer, this.message);
        buffer.writeInt(this.color);
    }

    @Override
    public void read(ByteBuf buffer) {
        super.read(buffer);
        this.message = PacketBufUtils.readString(buffer);
        this.color = buffer.readInt();
    }

    public String getMessage() {
        return this.message;
    }

    public int getColor() {
        return this.color;
    }
}
