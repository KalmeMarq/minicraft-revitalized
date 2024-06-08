package me.kalmemarq.minicraft.network;

import com.mojang.ld22.Game;
import com.mojang.ld22.screen.DisconnectedMenu;
import me.kalmemarq.minicraft.network.packet.DisconnectPacket;
import me.kalmemarq.minicraft.network.packet.LoginResponsePacket;
import me.kalmemarq.minicraft.network.packet.ReadyPacket;

public class ClientLoginNetworkHandler implements PacketListener {
	private final Game client;
	private final NetworkConnection connection;

	public ClientLoginNetworkHandler(Game client, NetworkConnection connection) {
		this.client = client;
		this.connection = connection;
	}

	@Override
	public void onPacket(Packet packet) {
		if (packet instanceof DisconnectPacket disconnectPacket) {
			this.connection.disconnect(disconnectPacket.getReason());
		} else if (packet instanceof LoginResponsePacket loginResponsePacket) {
			this.connection.setListener(new ClientPlayNetworkHandler(this.client, this.connection));
			this.connection.send(new ReadyPacket());
		}
	}

	@Override
	public void tick() {
	}

	@Override
	public void onDisconnected(String reason) {
		this.client.setMenu(new DisconnectedMenu(reason));
	}
}
