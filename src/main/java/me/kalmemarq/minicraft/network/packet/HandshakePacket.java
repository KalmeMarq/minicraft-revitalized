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

public class HandshakePacket extends Packet {
    private int protocol;
    private Intent intent;

    public HandshakePacket() {
    }

    public HandshakePacket(int protocol, Intent intent) {
        this.protocol = protocol;
        this.intent = intent;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(this.protocol);
        buffer.writeByte(this.intent.ordinal());
    }

    @Override
    public void read(ByteBuf buffer) {
        this.protocol = buffer.readInt();
        this.intent = Intent.values()[buffer.readByte()];
    }

    public int getProtocol() {
        return this.protocol;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public enum Intent {
        LOGIN,
        STATUS
    }
}
