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

import me.kalmemarq.minicraft.client.ClientPlayerEntity;
import me.kalmemarq.minicraft.client.menu.DisconnectMenu;
import me.kalmemarq.minicraft.client.menu.InventoryMenu;
import me.kalmemarq.minicraft.level.entity.Entity;
import me.kalmemarq.minicraft.level.entity.ItemEntity;
import me.kalmemarq.minicraft.level.entity.PlayerEntity;
import me.kalmemarq.minicraft.level.entity.SlimeEntity;
import me.kalmemarq.minicraft.level.entity.ZombieEntity;
import me.kalmemarq.minicraft.client.Client;
import me.kalmemarq.minicraft.level.entity.particle.TextParticle;
import me.kalmemarq.minicraft.network.NetworkConnection;
import me.kalmemarq.minicraft.network.Packet;
import me.kalmemarq.minicraft.network.PacketListener;
import me.kalmemarq.minicraft.network.packet.*;

import java.util.Map;

public class ClientPlayNetworkHandler implements PacketListener {
    private final Client client;
    private ClientPlayerEntity playerEntity;
    private final NetworkConnection connection;
	private int syncX = -1;
	private int syncY = -1;
	private int syncDir = -1;

    public ClientPlayNetworkHandler(Client client, NetworkConnection connection) {
        this.client = client;
        this.connection = connection;
    }

    public void send(Packet packet) {
        this.connection.sendPacket(packet);
    }

    @Override
    public void onPacket(Packet packet) {
        if (packet instanceof LevelDataPacket levelDataPacket) {
            this.client.level.load(levelDataPacket.getWidth(), levelDataPacket.getHeight(), levelDataPacket.getTiles());
        } else if (packet instanceof OpenInventoryPacket) {
            this.client.setMenu(new InventoryMenu());
        } else if (packet instanceof EntityPacket entityPacket) {
            if (this.client.player != null && this.client.player.uuid.equals(entityPacket.getUuid())) {
                return;
            }

            Entity entity;
            boolean alreadyHere = this.client.entityMap.containsKey(entityPacket.getUuid());
            if (alreadyHere) {
                entity = this.client.entityMap.get(entityPacket.getUuid());
            } else {
                if (entityPacket.getType() == 1) {
                    entity = new ZombieEntity(this.client.level);
                } else if (entityPacket.getType() == 3) {
                    entity = new SlimeEntity(this.client.level);
                } else if (entityPacket.getType() == 2) {
                    entity = new PlayerEntity(this.client.level);
                } else if (entityPacket.getType() == 4) {
                    entity = new TextParticle(this.client.level, ((EntityParticlePacket) entityPacket).getMessage(), entityPacket.getX(), entityPacket.getY(), ((EntityParticlePacket) entityPacket).getColor());
                } else {
                    entity = new ItemEntity(this.client.level, ((EntityItemPacket) entityPacket).getStack(), entityPacket.getX(), entityPacket.getY());
                    ((ItemEntity) entity).zz = ((EntityItemPacket) entityPacket).getZz();
                }
            }

            entity.uuid = entityPacket.getUuid();
            entity.x = entityPacket.getX();
            entity.y = entityPacket.getY();
            entity.dir = entityPacket.getDir();

            for (Map.Entry<String, Integer> entry : entityPacket.getTracked()) {
                entity.data.put(entry.getKey(), entry.getValue());
            }

            if (!alreadyHere) {
                this.client.entityMap.put(entity.uuid, entity);
                this.client.entities.add(entity);
            }
        } else if (packet instanceof DisconnectPacket disconnectPacket) {
            this.connection.disconnect(disconnectPacket.getReason());
        } else if (packet instanceof GamePacket gamePacket) {
            this.client.player = new ClientPlayerEntity(this.client, this.client.level, this);
            this.client.player.uuid = gamePacket.getUuid();
            this.client.player.x = gamePacket.getX();
            this.client.player.y = gamePacket.getY();
            this.client.player.dir = gamePacket.getDir();
            this.client.entities.add(this.client.player);
            this.client.entityMap.put(this.client.player.uuid, this.client.player);
        } else if (packet instanceof EntityRemovePacket packet1) {
            Entity entity = this.client.entityMap.remove(packet1.getUuid());
            this.client.entities.remove(entity);
        } else if (packet instanceof PlayerStatsPacket playerStatsPacket) {
            this.client.player.health = playerStatsPacket.getHealth();
            this.client.player.stamina = playerStatsPacket.getStamina();
        } else if (packet instanceof SetTilePacket setTilePacket && this.client.level.loaded) {
            this.client.level.setTile(setTilePacket.getX(), setTilePacket.getY(), setTilePacket.getTile(), setTilePacket.getData());
        } else if (packet instanceof InventoryPacket inventoryPacket) {
			this.client.player.inventory.itemStacks.clear();
			this.client.player.inventory.itemStacks.addAll(inventoryPacket.getContents());
			System.out.println(this.client.player.inventory.itemStacks);
		} else if (packet instanceof MessagePacket messagePacket) {
			this.onMessage(messagePacket);
        } else {
            System.out.println("Unknown packet " + packet.getClass().getSimpleName());
        }
    }

    @Override
    public void tick() {
        if (this.playerEntity == null) {
            if (this.client.player != null) this.playerEntity = this.client.player;
            return;
        }

        if (this.syncDir != this.playerEntity.dir || this.syncX != this.playerEntity.x || this.syncY != this.playerEntity.y) {
            this.send(new GamePacket(this.playerEntity.uuid, this.playerEntity.x, this.playerEntity.y, this.playerEntity.dir));
            this.syncX = this.playerEntity.x;
            this.syncY = this.playerEntity.y;
            this.syncDir = this.playerEntity.dir;
        }
    }

	private void onMessage(MessagePacket packet) {
		this.client.messages.add(packet.getMessage());
	}

    @Override
    public void onDisconnected(String reason) {
        this.client.connection = null;
        System.out.println("Disconnected: " + reason);
		this.client.setMenu(new DisconnectMenu(reason));
	}
}
