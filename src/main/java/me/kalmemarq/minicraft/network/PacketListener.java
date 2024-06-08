package me.kalmemarq.minicraft.network;

public interface PacketListener {
	void onPacket(Packet packet);
	void tick();
	void onDisconnected(String reason);
}
