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

public class QueryResponsePacket extends Packet {
    private String serverName;
    private int playerCount;

    public QueryResponsePacket() {
    }

    public QueryResponsePacket(String serverName, int playerCount) {
        this.serverName = serverName;
        this.playerCount = playerCount;
    }

    @Override
    public void write(ByteBuf buffer) {
        PacketBufUtils.writeString(buffer, this.serverName);
        PacketBufUtils.writeUnsignedVarInt(this.playerCount, buffer);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.serverName = PacketBufUtils.readString(buffer);
        this.playerCount = PacketBufUtils.readUnsignedVarInt(buffer);
    }

    public String getServerName() {
        return this.serverName;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }
}
