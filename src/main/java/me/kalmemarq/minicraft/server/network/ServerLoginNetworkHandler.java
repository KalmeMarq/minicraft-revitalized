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

import me.kalmemarq.minicraft.level.entity.Entity;
import me.kalmemarq.minicraft.level.entity.PlayerEntity;
import me.kalmemarq.minicraft.level.entity.SlimeEntity;
import me.kalmemarq.minicraft.level.entity.ZombieEntity;
import me.kalmemarq.minicraft.network.NetworkConnection;
import me.kalmemarq.minicraft.network.Packet;
import me.kalmemarq.minicraft.network.PacketListener;
import me.kalmemarq.minicraft.network.packet.DisconnectPacket;
import me.kalmemarq.minicraft.network.packet.EntityPacket;
import me.kalmemarq.minicraft.network.packet.GamePacket;
import me.kalmemarq.minicraft.network.packet.InventoryPacket;
import me.kalmemarq.minicraft.network.packet.LevelDataPacket;
import me.kalmemarq.minicraft.network.packet.LoginRequestPacket;
import me.kalmemarq.minicraft.network.packet.ReadyPacket;
import me.kalmemarq.minicraft.server.EntityTracker;
import me.kalmemarq.minicraft.server.Server;
import me.kalmemarq.minicraft.server.ServerPlayerEntity;

public class ServerLoginNetworkHandler implements PacketListener {
    private final Server server;
    private final NetworkConnection connection;

    public ServerLoginNetworkHandler(Server server, NetworkConnection connection) {
        this.server = server;
        this.connection = connection;
    }

    @Override
    public void onPacket(Packet packet) {
        if (packet instanceof LoginRequestPacket) {
            if (this.server.playConnections.size() + 1 > this.server.maxPlayers) {
                this.connection.sendPacket(new DisconnectPacket("Server is full!"));
                this.connection.disconnect();
            } else {
                this.connection.send(new ReadyPacket());
            }
        } else if (packet instanceof ReadyPacket) {
            ServerPlayerEntity entity1 = new ServerPlayerEntity(this.server.level);
            entity1.findStartPos(this.server.level);
            ServerPlayNetworkHandler networkHandler = new ServerPlayNetworkHandler(this.server, entity1, this.connection);
            entity1.networkHandler = networkHandler;
            this.server.level.insertEntity(entity1.x >> 4, entity1.y >> 4, entity1);
            this.server.broadcast(new EntityPacket(2, entity1.uuid, entity1.x, entity1.y, entity1.dir));
            this.connection.setListener(networkHandler);
            this.server.players.add(entity1);
            this.server.entities.add(entity1);
            this.connection.sendPacket(new LevelDataPacket(this.server.level.width, this.server.level.height, 0L, this.server.level.tiles));
            this.connection.sendPacket(new GamePacket(entity1.uuid, entity1.x, entity1.y, entity1.dir));
            this.connection.sendPacket(new InventoryPacket(entity1.inventory.itemStacks));
            this.server.entityTrackers.add(new EntityTracker(this.server, entity1));
            this.server.playConnections.add(this.connection);

            for (Entity entity : this.server.entities) {
                int type = entity instanceof ZombieEntity ? 1 : entity instanceof SlimeEntity ? 3 : 2;
                this.connection.sendPacket(new EntityPacket(type, entity.uuid, entity.x, entity.y, entity.dir, entity.data));
            }
        } else {
            System.out.println("Unknown packet " + packet.getClass().getSimpleName());
        }
    }

    @Override
    public void tick() {
    }

    @Override
    public void onDisconnected(String reason) {
    }
}
