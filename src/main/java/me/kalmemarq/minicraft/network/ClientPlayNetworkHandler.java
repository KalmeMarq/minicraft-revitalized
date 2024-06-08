package me.kalmemarq.minicraft.network;

import com.mojang.ld22.Game;
import com.mojang.ld22.entity.ClientPlayer;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.Slime;
import com.mojang.ld22.entity.Zombie;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.screen.DisconnectedMenu;
import me.kalmemarq.minicraft.network.packet.EntityPacket;
import me.kalmemarq.minicraft.network.packet.GamePacket;
import me.kalmemarq.minicraft.network.packet.LevelDataPacket;

public class ClientPlayNetworkHandler implements PacketListener {
	private final Game client;
	private final NetworkConnection connection;

	public ClientPlayNetworkHandler(Game client, NetworkConnection connection) {
		this.client = client;
		this.connection = connection;
	}

	@Override
	public void onPacket(Packet packet) {
		if (packet instanceof GamePacket gamePacket) {
			System.out.println("game packet");
			this.client.currentLevel = gamePacket.getCurrentLevel();
		} else if (packet instanceof LevelDataPacket levelDataPacket) {
			System.out.println("level data packet");
			Level level = new Level(levelDataPacket.getWidth(), levelDataPacket.getHeight(), this.client.currentLevel - 3, levelDataPacket.getTiles());
			this.client.levels[this.client.currentLevel] = level;
			this.client.level = level;
			this.client.playNetworkHandler = this;
			this.client.player = new ClientPlayer(this.client, this.client.input);
			this.client.player.findStartPos(level);
			this.client.level.add(this.client.player);
			this.client.setMenu(null);
		} else if (packet instanceof EntityPacket entityPacket) {
			if (entityPacket.getType() == 1) {
				Zombie zombie = new Zombie(entityPacket.getLvl());
				zombie.x = entityPacket.getX();
				zombie.y = entityPacket.getY();
				zombie.dir = entityPacket.getDir();
				zombie.entityId = entityPacket.getEntityId();
				this.client.level.add(zombie);
			} else if (entityPacket.getType() == 1) {
				Slime zombie = new Slime(entityPacket.getLvl());
				zombie.x = entityPacket.getX();
				zombie.y = entityPacket.getY();
				zombie.dir = entityPacket.getDir();
				zombie.entityId = entityPacket.getEntityId();
				this.client.level.add(zombie);
			}
		}
	}

	@Override
	public void tick() {
	}

	public NetworkConnection getConnection() {
		return this.connection;
	}

	@Override
	public void onDisconnected(String reason) {
		this.client.setMenu(new DisconnectedMenu(reason));
	}
}
