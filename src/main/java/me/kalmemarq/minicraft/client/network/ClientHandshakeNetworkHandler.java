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

package me.kalmemarq.minicraft.client.network;

import me.kalmemarq.minicraft.client.Client;
import me.kalmemarq.minicraft.network.NetworkConnection;
import me.kalmemarq.minicraft.network.Packet;
import me.kalmemarq.minicraft.network.PacketListener;
import me.kalmemarq.minicraft.network.packet.DisconnectPacket;
import me.kalmemarq.minicraft.network.packet.HandshakeResponsePacket;

public class ClientHandshakeNetworkHandler implements PacketListener {
    private final Client client;
    private final NetworkConnection connection;

    public ClientHandshakeNetworkHandler(Client client, NetworkConnection connection) {
        this.client = client;
        this.connection = connection;
    }

    @Override
    public void onPacket(Packet packet) {
        if (packet instanceof HandshakeResponsePacket handshakeResponsePacket) {
            this.onHandshakeResponse(handshakeResponsePacket);
        } else if (packet instanceof DisconnectPacket disconnectPacket) {
            this.connection.disconnect(disconnectPacket.getReason());
        } else {
            System.out.println("Unknown packet " + packet.getClass().getSimpleName());
        }
    }

    private void onHandshakeResponse(HandshakeResponsePacket packet) {
        if (!packet.isMeGood()) {
            this.connection.disconnect("Me not good pepeSad");
            return;
        }
        this.connection.setListener(new ClientLoginNetworkHandler(this.client, this.connection));
    }

    @Override
    public void tick() {
    }

    @Override
    public void onDisconnected(String reason) {
        this.client.connection = null;
        System.out.println("Disconnected: " + reason);
    }
}
