package me.kalmemarq.minicraft.network.packet;

import io.netty.buffer.ByteBuf;
import me.kalmemarq.minicraft.network.Packet;
import me.kalmemarq.minicraft.network.PacketBufUtils;

public class DisconnectPacket extends Packet {
	private String reason;

	public DisconnectPacket() {
	}

	public DisconnectPacket(String reason) {
		this.reason = reason;
	}

	@Override
	public void write(ByteBuf buffer) {
		PacketBufUtils.writeString(buffer, this.reason);
	}

	@Override
	public void read(ByteBuf buffer) {
		this.reason = PacketBufUtils.readString(buffer);
	}

	public String getReason() {
		return this.reason;
	}
}
