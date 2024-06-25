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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.kalmemarq.minicraft.network.NetworkConnection;
import me.kalmemarq.minicraft.server.network.ServerHandshakeNetworkHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.file.Path;

public class IntegratedServer extends Server {
    private final Path savePath;

    public IntegratedServer(Thread serverThread, Path savePath) {
        super(serverThread);
        this.savePath = savePath;
    }

    @Override
    public Path getSavePath() {
        return this.savePath;
    }

    public SocketAddress bindLocal() {
        this.eventLoopGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(this.eventLoopGroup);
        bootstrap.channel(LocalServerChannel.class);
        bootstrap.childHandler(new ChannelInitializer<LocalChannel>() {
            @Override
            protected void initChannel(LocalChannel ch) throws Exception {
                try {
                    ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException ignored) {
                }
                NetworkConnection connection = new NetworkConnection(IntegratedServer.this);
                NetworkConnection.addHandlers(ch.pipeline(), connection);
                IntegratedServer.this.connections.add(connection);
                connection.setListener(new ServerHandshakeNetworkHandler(IntegratedServer.this, connection));
            }
        });

        ChannelFuture channelFuture = bootstrap.localAddress(LocalAddress.ANY).bind();
        ChannelFuture channelFuture1 = channelFuture.syncUninterruptibly();
        System.out.println("Started server locally");
        this.channelFutures.add(channelFuture1);
        return channelFuture1.channel().localAddress();
    }

    public int getAvailablePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            serverSocket.close();
            return port;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 25525;
    }

    public void openToLan(int port) {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(this.eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        try {
                            ch.config().setOption(ChannelOption.TCP_NODELAY, true);
                        } catch (ChannelException ignored) {
                        }
                        NetworkConnection connection = new NetworkConnection(IntegratedServer.this);
                        NetworkConnection.addHandlers(ch.pipeline(), connection);
                        IntegratedServer.this.connections.add(connection);
                        connection.setListener(new ServerHandshakeNetworkHandler(IntegratedServer.this, connection));
                    }
                });

        var a = bootstrap.bind((InetAddress) null, port);
        this.channelFutures.add(a);
        a.syncUninterruptibly();
    }

    @Override
    public void setupServer() {
        this.level.generate(64, 64);
    }
}
