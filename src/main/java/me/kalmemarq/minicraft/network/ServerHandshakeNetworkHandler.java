package me.kalmemarq.minicraft.network;

import me.kalmemarq.minicraft.network.packet.DisconnectPacket;
import me.kalmemarq.minicraft.network.packet.HandsakePacket;

public class ServerHandshakeNetworkHandler implements PacketListener {
	private final Server server;
	private final NetworkConnection connection;

	public ServerHandshakeNetworkHandler(Server server, NetworkConnection connection) {
		this.server = server;
		this.connection = connection;
	}

	@Override
	public void onPacket(Packet packet) {
		if (packet instanceof HandsakePacket handsakePacket) {
			if (handsakePacket.getIntention() == HandsakePacket.Intention.SUCK) {
				this.send(new DisconnectPacket("Fuck you"));
				this.connection.disconnect("Fuck you");
			} else if (handsakePacket.getProtocolVersion() != 1) {
				int protocol = handsakePacket.getProtocolVersion();
				if (protocol > 1) {
					this.send(new DisconnectPacket("Outdated server"));
				} else {
					this.send(new DisconnectPacket("Outdated client"));
				}
				this.connection.disconnect("");
			} else {
				this.connection.setListener(new ServerLoginNetworkHandler(this.server, this.connection));
			}
		} else {
//			throw new RuntimeException("Unknown packet " + packet.getClass().getSimpleName());
		}
	}

	@Override
	public void tick() {
	}

	public void send(Packet packet) {
		this.connection.send(packet);
	}

	@Override
	public void onDisconnected(String reason) {
	}
}
