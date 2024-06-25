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

public class LoginRequestPacket extends Packet {
    private String username;

    public LoginRequestPacket() {
    }

    public LoginRequestPacket(String username) {
        this.username = username;
    }

    @Override
    public void write(ByteBuf buffer) {
        PacketBufUtils.writeString(buffer, this.username);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.username = PacketBufUtils.readString(buffer);
    }

    public String getUsername() {
        return this.username;
    }
}
