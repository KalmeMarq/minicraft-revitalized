package me.kalmemarq.minicraft.network.packet;

import io.netty.buffer.ByteBuf;
import me.kalmemarq.minicraft.network.Packet;

public class LoginResponsePacket extends Packet {
	private int playerId;

	public LoginResponsePacket() {
	}

	public LoginResponsePacket(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public void write(ByteBuf buffer) {
		buffer.writeInt(this.playerId);
	}

	@Override
	public void read(ByteBuf buffer) {
		this.playerId = buffer.readInt();
	}

	public int getPlayerId() {
		return this.playerId;
	}
}
