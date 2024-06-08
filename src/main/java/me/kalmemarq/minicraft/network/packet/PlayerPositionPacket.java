package me.kalmemarq.minicraft.network.packet;

import io.netty.buffer.ByteBuf;
import me.kalmemarq.minicraft.network.Packet;

public class PlayerPositionPacket extends Packet {
	private int x;
	private int y;
	private int direction;

	public PlayerPositionPacket() {
	}

	public PlayerPositionPacket(int x, int y, int direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	@Override
	public void write(ByteBuf buffer) {
		buffer.writeInt(this.x);
		buffer.writeInt(this.y);
		buffer.writeByte(this.direction);
	}

	@Override
	public void read(ByteBuf buffer) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.direction = buffer.readByte();
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getDirection() {
		return this.direction;
	}
}
