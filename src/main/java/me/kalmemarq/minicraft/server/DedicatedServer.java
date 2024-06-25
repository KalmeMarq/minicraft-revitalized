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

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.kalmemarq.minicraft.client.util.IOUtils;
import me.kalmemarq.minicraft.client.util.StringUtils;
import me.kalmemarq.minicraft.network.NetworkConnection;
import me.kalmemarq.minicraft.server.network.ServerHandshakeNetworkHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class DedicatedServer extends Server {
    public ServerConsoleGui gui;
    private Path worldPath;

    public DedicatedServer(Thread serverThread) {
        super(serverThread);
    }

    public void startGui() {
        if (this.gui == null) {
            this.gui = new ServerConsoleGui();
            this.gui.setOnSend(this::runCommand);
            this.gui.setOnClose(() -> {
                if (this.gui != null) {
                    this.gui.frame.dispose();
                    this.gui = null;
                }
                this.stop(false);
            });
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        if (this.gui != null) {
            this.gui.frame.dispose();
            this.gui = null;
        }
    }

    @Override
    public void printMessage(String message) {
        if (this.gui != null) this.gui.textArea.append(message + "\n");
        else super.printMessage(message);
    }

    @Override
    public Path getSavePath() {
        return this.worldPath == null ? super.getSavePath() : this.worldPath;
    }

    @Override
    public void setupServer() {
        Path propertiesPath = Path.of("properties.toml");

        if (!Files.exists(propertiesPath)) {
            try {
                Files.writeString(propertiesPath, StringUtils.readAllLines(DedicatedServer.class.getResourceAsStream("/default_server_properties.toml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }

        try {
            JsonNode node = IOUtils.TOML_OBJECT_MAPPER.readTree(Files.newInputStream(propertiesPath));
            if (node.has("max_players") && node.isNumber()) {
                this.maxPlayers = node.get("max_players").asInt();
            }
            if (node.has("world") && node.get("world").isObject()) {
                JsonNode worldNode = node.get("world");
                if (worldNode.has("name") && worldNode.get("name").isTextual()) {
                    this.worldPath = Path.of(worldNode.get("name").asText());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.startGui();
        this.startConsoleThread();

        this.level.generate(64, 64);

        this.eventLoopGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(this.eventLoopGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                try {
                    ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException ignored) {
                }
                NetworkConnection connection = new NetworkConnection(DedicatedServer.this);
                NetworkConnection.addHandlers(ch.pipeline(), connection);
                DedicatedServer.this.connections.add(connection);
                connection.setListener(new ServerHandshakeNetworkHandler(DedicatedServer.this, connection));
            }
        });

        ChannelFuture channelFuture = bootstrap.bind("localhost", 8080);
        this.channelFutures.add(channelFuture);
        channelFuture.syncUninterruptibly();
        this.printMessage("Started server at 8080");
    }

    private void startConsoleThread() {
        Thread consoleThread = new Thread("Command Console") {
            @Override
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String line;
                    while (!DedicatedServer.this.hasStopped() && (line = reader.readLine()) != null) {
                        line = line.trim();

                        if (!line.isEmpty()) {
                            String finalLine = line;
                            DedicatedServer.this.execute(() -> DedicatedServer.this.runCommand(finalLine));
                        }
                    }
                } catch (IOException ignored) {
                }
            }
        };
        consoleThread.setDaemon(true);
        consoleThread.start();
    }
}
