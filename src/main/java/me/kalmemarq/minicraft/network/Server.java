package me.kalmemarq.minicraft.network;

import com.mojang.ld22.level.Level;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.kalmemarq.minicraft.ThreadExecutor;
import me.kalmemarq.minicraft.network.packet.DisconnectPacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Server extends ThreadExecutor {
	private static final double TICKS = 60;
	private static final double MSPT = 1000.0 / TICKS;

	public volatile boolean running;
	public final Thread thread;
	public Level[] levels = new Level[5];
	public long seed;
	public long gameTime;

	protected NioEventLoopGroup eventLoopGroup;
	protected ChannelFuture channelFuture;
	protected List<NetworkConnection> connections = new ArrayList<>();

	public Server() {
		this.thread = Thread.currentThread();
	}

	public Thread getThread() {
		return this.thread;
	}

	public boolean setupServer() {
		this.startConsoleThread();

		this.seed = new Random().nextLong();
		System.out.println("Creating sky level");
		this.levels[3] = new Level(128, 128, this.seed, 0, this.levels[4]);
		System.out.println("Creating overworld level");
		this.levels[2] = new Level(128, 128, this.seed, -1, this.levels[3]);
		System.out.println("Creating underground levels");
		this.levels[1] = new Level(128, 128, this.seed, -2, this.levels[2]);
		this.levels[4] = new Level(128, 128, this.seed, 1, null);
		this.levels[0] = new Level(128, 128, this.seed, -3, this.levels[1]);

		System.out.println("Try spawning");
		for (int i = 0; i < 5; i++) {
			this.levels[i].trySpawn(5000);
		}

		this.eventLoopGroup = new NioEventLoopGroup();
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(this.eventLoopGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				NetworkConnection connection = new NetworkConnection(Server.this);
				NetworkConnection.addHandlers(ch.pipeline(), connection);
				Server.this.connections.add(connection);
				connection.setListener(new ServerHandshakeNetworkHandler(Server.this, connection));
			}
		});

		ChannelFuture channelFuture = bootstrap.bind("localhost", 8080);
		this.channelFuture = channelFuture.syncUninterruptibly();
		System.out.println("Started server at 8080");
		return true;
	}

	public void run() {
		this.running = this.setupServer();

		while (this.running) {
			long lastTime = System.currentTimeMillis();
			this.tick();
			this.runAllTasks();
			this.runTasksWhile(() -> System.currentTimeMillis() - lastTime < MSPT);
		}

		this.shutdown();
	}

	public void shutdown() {
		System.out.println("Server shutting down");
		try {
			this.channelFuture.channel().close().sync();
		} catch (Exception e) {
			System.out.println(e);
		}

		for (NetworkConnection connection : this.connections) {
			connection.send(new DisconnectPacket("Server Shutdown"));
			connection.disconnect("Server shutdown");
			connection.tryDisableAutoRead();
		}
		this.connections.clear();

		if (this.eventLoopGroup != null) this.eventLoopGroup.shutdownGracefully();
	}

	public void stop() {
		this.running = false;
	}

	public boolean hasStopped() {
		return !this.thread.isAlive();
	}

	private void runCommand(String command) {
		if ("/stop".equals(command)) {
			this.stop();
		}
	}

	public void tick() {
		this.gameTime++;

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
						connection.send(new DisconnectPacket("Internale server error"));
						connection.disconnect("Internale server error");
						connection.tryDisableAutoRead();
					}
					continue;
				}
				System.out.println("get outa here");
				iter.remove();
				connection.handleDisconnection();
			}
		}

		for (int i = 0; i < 5; ++i) {
			this.levels[i].tick();
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	private void startConsoleThread() {
		Thread consoleThread = new Thread("Command Console") {
			@Override
			public void run() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
					String line;
					while (!Server.this.hasStopped() && (line = reader.readLine()) != null) {
						line = line.trim();

						if (!line.isEmpty()) {
							String finalLine = line;
							Server.this.execute(() -> Server.this.runCommand(finalLine));
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
