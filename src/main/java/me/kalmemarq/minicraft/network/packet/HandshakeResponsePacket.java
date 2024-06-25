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

public class HandshakeResponsePacket extends Packet {
    private boolean youGood;

    public HandshakeResponsePacket() {
    }

    public HandshakeResponsePacket(boolean youGood) {
        this.youGood = youGood;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeBoolean(this.youGood);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.youGood = buffer.readBoolean();
    }

    public boolean isMeGood() {
        return this.youGood;
    }
}
