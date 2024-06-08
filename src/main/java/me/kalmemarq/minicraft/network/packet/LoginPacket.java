package me.kalmemarq.minicraft.network.packet;

import io.netty.buffer.ByteBuf;
import me.kalmemarq.minicraft.network.Packet;
import me.kalmemarq.minicraft.network.PacketBufUtils;

public class LoginPacket extends Packet {
	private String username;

	public LoginPacket() {
	}

	public LoginPacket(String username, int protocolVersion) {
		this.username = username;

	}

	@Override
	public void write(ByteBuf buffer) {
		PacketBufUtils.writeString(buffer, this.username);
	}

	@Override
	public void read(ByteBuf buffer) {
		this.username = PacketBufUtils.readString(buffer);
	}

	public String getUsername() {
		return this.username;
	}
}
