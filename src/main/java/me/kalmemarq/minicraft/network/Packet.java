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

package me.kalmemarq.minicraft.network;

import io.netty.buffer.ByteBuf;
import me.kalmemarq.minicraft.network.packet.*;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class Packet {
    public static Map<Integer, Class<? extends Packet>> ID_TO_CLASS = new HashMap<>();
    public static Map<Class<? extends Packet>, Integer> CLASS_TO_ID = new IdentityHashMap<>();

    public static void register(Class<? extends Packet> packetClazz) {
        int id = ID_TO_CLASS.size() + 1;
        ID_TO_CLASS.put(id, packetClazz);
        CLASS_TO_ID.put(packetClazz, id);
    }

    abstract public void write(ByteBuf buffer);
    abstract public void read(ByteBuf buffer);

    static {
        register(HandshakePacket.class);
        register(HandshakeResponsePacket.class);
        register(DisconnectPacket.class);
        register(LoginRequestPacket.class);
        register(ReadyPacket.class);
        register(EntityPacket.class);
        register(QueryResponsePacket.class);
        register(QueryRequestPacket.class);
        register(GamePacket.class);
        register(MessagePacket.class);
        register(EntityRemovePacket.class);
        register(LevelDataPacket.class);
        register(PlayerStatsPacket.class);
        register(EntityParticlePacket.class);
        register(PlayerAttackPacket.class);
        register(OpenInventoryPacket.class);
        register(PlayerUsePacket.class);
        register(InventoryPacket.class);
        register(SetTilePacket.class);
        register(EntityItemPacket.class);
    }
}
