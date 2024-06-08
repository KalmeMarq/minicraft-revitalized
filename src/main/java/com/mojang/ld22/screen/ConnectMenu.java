package com.mojang.ld22.screen;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.kalmemarq.minicraft.network.ClientLoginNetworkHandler;
import me.kalmemarq.minicraft.network.packet.HandsakePacket;
import me.kalmemarq.minicraft.network.packet.LoginPacket;
import me.kalmemarq.minicraft.network.NetworkConnection;

public class ConnectMenu extends Menu {
	private volatile NetworkConnection connection;

	public ConnectMenu() {
		Thread thread = new Thread(() -> {
			this.game.resetGame();
			if (this.game.eventLoopGroup == null) {
				this.game.eventLoopGroup = new NioEventLoopGroup();
			}
			NetworkConnection connection = new NetworkConnection(this.game);
			ClientLoginNetworkHandler handler = new ClientLoginNetworkHandler(this.game, connection);
			connection.setListener(handler);

			Bootstrap bootstrap = new Bootstrap()
				.group(this.game.eventLoopGroup)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						try {
							ch.config().setOption(ChannelOption.TCP_NODELAY, true);
						} catch (ChannelException ignored) {
						}
						NetworkConnection.addHandlers(ch.pipeline(), connection);
					}
				});

			this.connection = connection;
			try {
				bootstrap.connect("localhost", 8080).syncUninterruptibly();
				connection.send(new HandsakePacket(HandsakePacket.Intention.LOGIN, 1));
				connection.send(new LoginPacket("KalmeMarq", 1));
			} catch (Exception e) {
				System.out.println(e.getMessage());
				this.game.execute(() -> this.game.setMenu(new DisconnectedMenu(e.getMessage())));
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	public void tick() {
		if (this.connection != null) {
			if (this.connection.isOpen()) {
				this.connection.tick();
			} else {
				this.connection.handleDisconnection();
			}
		}
	}

	public void render(Screen screen) {
		screen.clear(0);
		screen.renderBackgroundRGBA(0, 0, 0xFF090909);

		Font.draw("Connecting", screen, 2 * 8 + 4, 8, Color.get(0, 555, 555, 555));
	}
}
