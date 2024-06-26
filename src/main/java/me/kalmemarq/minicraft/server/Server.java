/*
 * Minicraft Revitalized.
 * Copyright (C) 2024 KalmeMarq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */

package me.kalmemarq.minicraft.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import me.kalmemarq.minicraft.bso.BsoArrayTag;
import me.kalmemarq.minicraft.bso.BsoMapTag;
import me.kalmemarq.minicraft.bso.BsoUtils;
import me.kalmemarq.minicraft.level.entity.Entity;
import me.kalmemarq.minicraft.network.NetworkConnection;
import me.kalmemarq.minicraft.network.Packet;
import me.kalmemarq.minicraft.server.level.ServerLevel;
import me.kalmemarq.minicraft.util.ThreadExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Server extends ThreadExecutor {
    protected final Thread thread;
    protected NioEventLoopGroup eventLoopGroup;
    protected List<ChannelFuture> channelFutures = new ArrayList<>();
    public List<NetworkConnection> connections = new ArrayList<>();
    public List<NetworkConnection> playConnections = new ArrayList<>();
    protected boolean running = true;

    public List<Entity> entities = new ArrayList<>();
    public List<EntityTracker> entityTrackers = new ArrayList<>();
    public List<ServerPlayerEntity> players = new ArrayList<>();
    public ServerLevel level = new ServerLevel(this);
    public int maxPlayers = 8;

    public Server(Thread serverThread) {
        this.thread = serverThread;
    }

    public void broadcast(Packet packet) {
        for (NetworkConnection connection : this.playConnections) {
            connection.send(packet);
        }
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    abstract public void setupServer();

    public void tick() {
        List<NetworkConnection> connectionList = this.connections;
        synchronized (connectionList) {
            Iterator<NetworkConnection> iter = this.connections.iterator();
            while (iter.hasNext()) {
                NetworkConnection connection = iter.next();
                if (connection.isChannelAbsent()) continue;
                if (connection.isOpen()) {
                    try {
                        connection.tick();
                    } catch (Exception exception) {
                        connection.disconnect("Internale server error");
                        connection.tryDisableAutoRead();
                    }
                    continue;
                }
                this.printMessage("get outa here");
                iter.remove();
                connection.handleDisconnection();
                this.playConnections.remove(connection);
            }
        }

        this.level.tick();
    }

    public void loadEntity(Entity entity) {
        this.entities.add(entity);
        this.entityTrackers.add(new EntityTracker(this, entity));
    }

    public void run() {
        this.setupServer();

        long lastTime = System.nanoTime();
        double unprocessed = 0;
        double nsPerTick = 1000000000.0 / 60;
        int ticks = 0;
        long lastTimer1 = System.currentTimeMillis();

        while (this.running) {
            long now = System.nanoTime();
            unprocessed += (now - lastTime) / nsPerTick;
            lastTime = now;
            while (unprocessed >= 1) {
                ticks++;
                this.tick();
                unprocessed -= 1;
            }

            this.runAllTasks();

            if (System.currentTimeMillis() - lastTimer1 > 1000) {
                lastTimer1 += 1000;
                this.printMessage(ticks + " ticks");
                ticks = 0;
            }
        }

        this.shutdown();
    }

    public void shutdown() {
        System.out.println("Server shutting down");

        for (ChannelFuture channelFuture : this.channelFutures) {
            try {
                channelFuture.channel().close().sync();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        for (NetworkConnection connection : this.connections) {
            connection.disconnect("Server shutdown");
            connection.tryDisableAutoRead();
        }
        this.connections.clear();
        this.playConnections.clear();

        this.save();

        if (this.eventLoopGroup != null) this.eventLoopGroup.shutdownGracefully();

        System.gc();
    }

    public Path getSavePath() {
        return Path.of("world");
    }

    public void save() {
        try {
            Files.createDirectories(this.getSavePath());

            BsoMapTag map = new BsoMapTag();
            map.put("width", this.level.width);
            map.put("height", this.level.height);
            map.put("textures/tiles", this.level.tiles);
            map.put("data", this.level.data);

            BsoUtils.writeCompressed(this.getSavePath().resolve("level0.bso"), map);

			BsoArrayTag m = new BsoArrayTag();
			for (Entity entity : this.entities) {
				BsoMapTag o = new BsoMapTag();
				entity.write(o);
				m.add(o);
			}
			BsoUtils.writeCompressed(this.getSavePath().resolve("level0_entities.bso"), m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Thread getThread() {
        return this.thread;
    }

    protected void runCommand(String command) {
        if ("/stop".equals(command)) {
            this.stop(false);
        } else if (command.matches("/maxplayers \\d+")) {
            this.maxPlayers = Integer.parseInt(command.substring(command.indexOf(' ') + 1));
            this.printMessage("Max players set to " + this.maxPlayers);
        }
    }

    public void stop(boolean waitForShutdown) {
        this.running = false;
        if (waitForShutdown) {
            try {
                this.thread.join();
            } catch (InterruptedException interruptedException) {
                System.err.println("Error while shutting down: " + interruptedException);
            }
        }
    }

    public boolean hasStopped() {
        return !this.thread.isAlive();
    }
}
