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

package me.kalmemarq.minicraft.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import me.kalmemarq.minicraft.util.ThreadExecutor;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class NetworkConnection extends SimpleChannelInboundHandler<Packet> {
    public Channel channel;
    public SocketAddress address;
    public PacketListener listener;
    private final Queue<Consumer<NetworkConnection>> queue = new ConcurrentLinkedQueue<>();
    public ThreadExecutor executor;
    private boolean disconnected;
    private String pendingReason;

    public NetworkConnection(ThreadExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        this.address = this.channel.remoteAddress();
    }

    public void setListener(PacketListener listener) {
        this.listener = listener;
    }

    public void send(Packet packet) {
        ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
        channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendPacket(Packet packet) {
        if (this.channel != null && this.channel.isOpen()) {
            this.send(packet);
        } else {
            this.queue.add((conn) -> conn.send(packet));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        this.disconnect();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.disconnect("End of Stream");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        if (this.listener != null) {
            this.executor.execute(() -> this.listener.onPacket(msg));
        }
    }

    public void tick() {
        if (this.listener != null) {
            this.listener.tick();
        }

        if (!this.isOpen() && !this.disconnected) {
            this.handleDisconnection();
        }

        if (this.isOpen()) {
            synchronized (this.queue) {
                Consumer<NetworkConnection> consumer;
                while ((consumer = this.queue.poll()) != null) {
                    consumer.accept(this);
                }
            }
        }
    }

    public void disconnect(String reason) {
        if (this.isOpen()) {
            this.channel.close().syncUninterruptibly();
        }
        this.pendingReason = reason;
    }

    public void disconnect() {
        if (this.isOpen()) {
            this.channel.close().syncUninterruptibly();
        }
    }

    public void tryDisableAutoRead() {
        if (this.channel != null) {
            this.channel.config().setAutoRead(false);
        }
    }

    public boolean isOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean isConnected() {
        return this.channel == null || this.channel.isOpen();
    }

    public void handleDisconnection() {
        if (this.isConnected()) {
            return;
        }

        if (this.disconnected) {
            System.out.println("called twice");
            return;
        }
        this.disconnected = true;
        if (this.listener != null) {
            this.listener.onDisconnected(this.pendingReason == null ? "Disconnect generic" : this.pendingReason);
        }
    }

    public static void addHandlers(ChannelPipeline pipeline, NetworkConnection connection) {
        pipeline
                .addLast("frame_decoder", new PacketFrameDecoder())
                .addLast("decoder", new PacketDecoder())
                .addLast("prepender", new PacketSizePrepender())
                .addLast("encoder", new PacketEncoder())
                .addLast("handler", connection);
    }

    public boolean isChannelAbsent() {
        return this.channel == null;
    }
}
