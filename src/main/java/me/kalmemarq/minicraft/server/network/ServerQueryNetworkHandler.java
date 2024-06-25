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

package me.kalmemarq.minicraft.server.network;

import me.kalmemarq.minicraft.network.NetworkConnection;
import me.kalmemarq.minicraft.network.Packet;
import me.kalmemarq.minicraft.network.PacketListener;
import me.kalmemarq.minicraft.network.packet.QueryRequestPacket;
import me.kalmemarq.minicraft.network.packet.QueryResponsePacket;
import me.kalmemarq.minicraft.server.Server;

public class ServerQueryNetworkHandler implements PacketListener {
    private final Server server;
    private final NetworkConnection connection;

    public ServerQueryNetworkHandler(Server server, NetworkConnection connection) {
        this.server = server;
        this.connection = connection;
    }

    @Override
    public void onPacket(Packet packet) {
        if (packet instanceof QueryRequestPacket) {
            this.connection.sendPacket(new QueryResponsePacket("Minicraft Server", this.server.playConnections.size()));
            this.connection.disconnect("Queried successfully!");
        }
    }

    @Override
    public void tick() {
    }

    @Override
    public void onDisconnected(String reason) {
    }
}
