package me.kalmemarq.minicraft.network;

import io.netty.buffer.ByteBuf;
import me.kalmemarq.minicraft.network.packet.*;

import java.util.Map;
import java.util.stream.Collectors;

public abstract class Packet {
	private static int packetId = 1;
	public static Map<Class<? extends Packet>, Integer> CLASS_TO_ID = Map.ofEntries(
		Map.entry(HandsakePacket.class, packetId++),
		Map.entry(LoginPacket.class, packetId++),
		Map.entry(LoginResponsePacket.class, packetId++),
		Map.entry(DisconnectPacket.class, packetId++),
		Map.entry(PlayerPositionPacket.class, packetId++),
		Map.entry(LevelDataPacket.class, packetId++),
		Map.entry(ReadyPacket.class, packetId++),
		Map.entry(EntityPacket.class, packetId++),
		Map.entry(GamePacket.class, packetId++)
	);
	public static Map<Integer, Class<? extends Packet>> ID_TO_CLASS = CLASS_TO_ID.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
	abstract public void write(ByteBuf buffer);
	abstract public void read(ByteBuf buffer);
}
