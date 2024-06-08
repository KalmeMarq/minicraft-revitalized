package me.kalmemarq.minicraft.network.packet;

import io.netty.buffer.ByteBuf;
import me.kalmemarq.minicraft.network.Packet;

public class HandsakePacket extends Packet {
	private Intention intention;
	private int protocolVersion;

	public HandsakePacket() {
	}

	public HandsakePacket(Intention intention, int protocolVersion) {
		this.intention = intention;
		this.protocolVersion = protocolVersion;
	}

	@Override
	public void write(ByteBuf buffer) {
		buffer.writeByte(this.intention.ordinal());
		buffer.writeInt(this.protocolVersion);
	}

	@Override
	public void read(ByteBuf buffer) {
		this.intention = Intention.values()[buffer.readByte()];
		this.protocolVersion = buffer.readInt();
	}

	public Intention getIntention() {
		return this.intention;
	}

	public int getProtocolVersion() {
		return this.protocolVersion;
	}

	public enum Intention {
		LOGIN,
		SUCK
	}
}
