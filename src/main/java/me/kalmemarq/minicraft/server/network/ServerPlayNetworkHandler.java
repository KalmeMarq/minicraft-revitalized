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
import me.kalmemarq.minicraft.network.packet.GamePacket;
import me.kalmemarq.minicraft.network.packet.MessagePacket;
import me.kalmemarq.minicraft.network.packet.PlayerAttackPacket;
import me.kalmemarq.minicraft.network.packet.PlayerUsePacket;
import me.kalmemarq.minicraft.server.EntityTracker;
import me.kalmemarq.minicraft.server.Server;
import me.kalmemarq.minicraft.server.ServerPlayerEntity;

import java.util.Iterator;

public class ServerPlayNetworkHandler implements PacketListener {
    private final Server server;
    private final NetworkConnection connection;
    private final ServerPlayerEntity player;

    private int uX = -1;
    private int uY = -1;

    public ServerPlayNetworkHandler(Server server, ServerPlayerEntity player, NetworkConnection connection) {
        this.server = server;
        this.player = player;
        this.connection = connection;
    }

    public void sendPacket(Packet packet) {
        this.connection.sendPacket(packet);
    }

    @Override
    public void onPacket(Packet packet) {
        if (packet instanceof GamePacket entityPacket) {
            if (this.uX == -1) {
                this.uX = this.player.x;
                this.uY = this.player.y;
            }

            int dX = entityPacket.getX() - this.uX;
            int dY = entityPacket.getY() - this.uY;

            this.player.move(dX, dY);

            this.uX = entityPacket.getX();
            this.uY = entityPacket.getY();

            this.player.dir = entityPacket.getDir();
        } else if (packet instanceof PlayerAttackPacket) {
            this.player.doAttack();
        } else if (packet instanceof PlayerUsePacket) {
            this.player.doUse();
		} else if (packet instanceof MessagePacket messagePacket) {
			this.onMessage(messagePacket);
        } else {
            System.out.println("Unknown packet " + packet.getClass().getSimpleName());
        }
    }

    @Override
    public void tick() {
        this.player.netTick();
    }

	private void onMessage(MessagePacket packet) {
		this.server.broadcast(new MessagePacket("<" + this.player.username + "> " + packet.getMessage()));
	}

    @Override
    public void onDisconnected(String reason) {
        this.server.entities.remove(this.player);
        this.server.players.remove(this.player);
        this.server.playConnections.remove(this.connection);
        this.server.level.remove(this.player);
        Iterator<EntityTracker> iter = this.server.entityTrackers.iterator();

        while (iter.hasNext()) {
            if (iter.next().getEntity() == this.player) {
                iter.remove();
                break;
            }
        }
    }
}
