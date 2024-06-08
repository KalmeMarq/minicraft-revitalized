package me.kalmemarq.minicraft.network.packet;

import io.netty.buffer.ByteBuf;
import me.kalmemarq.minicraft.network.Packet;

public class EntityPacket extends Packet {
	private int entityId;
	private int type;
	private int lvl;
	private int x;
	private int y;
	private int dir;

	public EntityPacket() {
	}

	public EntityPacket(int entityId, int type, int lvl, int x, int y, int dir) {
		this.entityId = entityId;
		this.type = type;
		this.lvl = lvl;
		this.x = x;
		this.y = y;
		this.dir = dir;
	}

	@Override
	public void write(ByteBuf buffer) {
		buffer.writeInt(this.entityId);
		buffer.writeInt(this.type);
		buffer.writeInt(this.lvl);
		buffer.writeInt(this.x);
		buffer.writeInt(this.y);
		buffer.writeInt(this.dir);
	}

	@Override
	public void read(ByteBuf buffer) {
		this.entityId = buffer.readInt();
		this.type = buffer.readInt();
		this.lvl = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.dir = buffer.readInt();
	}

	public int getEntityId() {
		return this.entityId;
	}
	public int getType() {
		return this.type;
	}
	public int getLvl() {
		return this.lvl;
	}
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
	public int getDir() {
		return this.dir;
	}
}
