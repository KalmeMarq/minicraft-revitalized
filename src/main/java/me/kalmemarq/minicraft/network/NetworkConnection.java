package me.kalmemarq.minicraft.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import me.kalmemarq.minicraft.ThreadExecutor;
import org.jetbrains.annotations.Nullable;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class NetworkConnection extends SimpleChannelInboundHandler<Packet> {
	public Channel channel;
	public SocketAddress address;
	@Nullable
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

	public void setListener(@Nullable PacketListener listener) {
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
