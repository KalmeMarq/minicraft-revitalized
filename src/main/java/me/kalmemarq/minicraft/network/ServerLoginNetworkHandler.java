package me.kalmemarq.minicraft.network;

import me.kalmemarq.minicraft.network.packet.DisconnectPacket;
import me.kalmemarq.minicraft.network.packet.GamePacket;
import me.kalmemarq.minicraft.network.packet.LevelDataPacket;
import me.kalmemarq.minicraft.network.packet.LoginPacket;
import me.kalmemarq.minicraft.network.packet.LoginResponsePacket;
import me.kalmemarq.minicraft.network.packet.ReadyPacket;

public class ServerLoginNetworkHandler implements PacketListener {
	private final Server server;
	private final NetworkConnection connection;

	public ServerLoginNetworkHandler(Server server, NetworkConnection connection) {
		this.server = server;
		this.connection = connection;
	}

	@Override
	public void onPacket(Packet packet) {
		if (packet instanceof LoginPacket loginPacket) {
			this.connection.send(new LoginResponsePacket(0));
		} else if (packet instanceof ReadyPacket readyPacket) {
			this.connection.setListener(new ServerPlayNetworkHandler(this.server, this.connection));
			this.send(new GamePacket(3));
			this.send(new LevelDataPacket(this.server.levels[3].w,this.server.levels[3].h, this.server.seed, this.server.levels[3].tiles));
		}
	}

	@Override
	public void tick() {
	}

	public void send(Packet packet) {
		this.connection.send(packet);
	}

	public void disconnect(String reason) {
		this.connection.send(new DisconnectPacket(reason));
		this.connection.disconnect(reason);
	}

	@Override
	public void onDisconnected(String reason) {
		System.out.println("Lost connection: " + reason);
	}
}
