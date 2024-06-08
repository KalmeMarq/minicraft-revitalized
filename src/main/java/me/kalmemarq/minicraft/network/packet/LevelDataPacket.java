package me.kalmemarq.minicraft.network.packet;

import io.netty.buffer.ByteBuf;
import me.kalmemarq.minicraft.network.Packet;

public class LevelDataPacket extends Packet {
	private int width;
	private int height;
	private long seed;
	private byte[] tiles;

	public LevelDataPacket() {
	}

	public LevelDataPacket(int width, int height, long seed, byte[] tiles) {
		this.width = width;
		this.height = height;
		this.seed = seed;
		this.tiles = tiles;
	}

	@Override
	public void write(ByteBuf buffer) {
		buffer.writeInt(this.width);
		buffer.writeInt(this.height);
		buffer.writeLong(this.seed);
		buffer.writeBytes(this.tiles);
	}

	@Override
	public void read(ByteBuf buffer) {
		this.width = buffer.readInt();
		this.height = buffer.readInt();
		this.seed = buffer.readLong();
		this.tiles = new byte[this.width * this.height];
		buffer.readBytes(this.tiles, 0, this.tiles.length);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public long getSeed() {
		return this.seed;
	}

	public byte[] getTiles() {
		return this.tiles;
	}
}
