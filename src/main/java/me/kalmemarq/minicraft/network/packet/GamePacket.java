package me.kalmemarq.minicraft.network.packet;

import io.netty.buffer.ByteBuf;
import me.kalmemarq.minicraft.network.Packet;

public class GamePacket extends Packet {
	private int currentLevel;

	public GamePacket() {
	}

	public GamePacket(int currentLevel) {
		this.currentLevel = currentLevel;
	}

	@Override
	public void write(ByteBuf buffer) {
		buffer.writeInt(this.currentLevel);
	}

	@Override
	public void read(ByteBuf buffer) {
		this.currentLevel = buffer.readInt();
	}

	public int getCurrentLevel() {
		return this.currentLevel;
	}
}
