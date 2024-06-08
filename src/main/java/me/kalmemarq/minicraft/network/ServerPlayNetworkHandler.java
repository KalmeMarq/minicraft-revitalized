package me.kalmemarq.minicraft.network;

public class ServerPlayNetworkHandler implements PacketListener {
	private final Server server;
	private final NetworkConnection connection;

	public ServerPlayNetworkHandler(Server server, NetworkConnection connection) {
		this.server = server;
		this.connection = connection;
	}

	@Override
	public void onPacket(Packet packet) {
	}

	@Override
	public void tick() {
	}

	@Override
	public void onDisconnected(String reason) {
	}
}
