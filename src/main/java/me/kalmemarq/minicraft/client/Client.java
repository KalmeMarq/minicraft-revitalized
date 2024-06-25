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

package me.kalmemarq.minicraft.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.kalmemarq.minicraft.client.util.IOUtils;
import me.kalmemarq.minicraft.client.util.Translation;
import me.kalmemarq.minicraft.util.ThreadExecutor;
import me.kalmemarq.minicraft.client.level.ClientLevel;
import me.kalmemarq.minicraft.client.menu.Font;
import me.kalmemarq.minicraft.client.menu.Menu;
import me.kalmemarq.minicraft.client.menu.TitleMenu;
import me.kalmemarq.minicraft.client.menu.WorldMenu;
import me.kalmemarq.minicraft.client.network.ClientHandshakeNetworkHandler;
import me.kalmemarq.minicraft.client.texture.TextureManager;
import me.kalmemarq.minicraft.level.entity.Entity;
import me.kalmemarq.minicraft.network.NetworkConnection;
import me.kalmemarq.minicraft.network.packet.HandshakePacket;
import me.kalmemarq.minicraft.network.packet.LoginRequestPacket;
import me.kalmemarq.minicraft.server.IntegratedServer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Configuration;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Client extends ThreadExecutor implements GameWindow.WindowEventHandler {
    public final Thread thread;
    public NioEventLoopGroup eventLoopGroup;
    public volatile NetworkConnection connection;

    public Map<UUID, Entity> entityMap = new HashMap<>();
    public List<Entity> entities = new ArrayList<>();

    public IntegratedServer integratedServer;
    public ClientLevel level = new ClientLevel();
    public ClientPlayerEntity player;
    public GameWindow window;
    public TextureManager textureManager = new TextureManager(this);
    public Font font = new Font(this.textureManager);
    public Menu menu;
    public boolean running = true;
    public int tickCount;
    public String debugString = "";
    public Renderer renderer = new Renderer(this);
    public boolean showDebug;
    public final Path saveDir;
    public InputHandler inputHandler = new InputHandler();

    public Client(Path saveDir) {
        this.saveDir = saveDir;
        this.thread = Thread.currentThread();
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
        if (menu != null) menu.init(this);
    }

    public void connectLocal() {
        Thread serverThread = new Thread(() -> {
            this.integratedServer.run();
        });
        this.integratedServer = new IntegratedServer(serverThread, this.saveDir.resolve("saves/world0"));
        serverThread.start();

        SocketAddress address = this.integratedServer.bindLocal();

        Thread thread = new Thread(() -> {
            if (this.eventLoopGroup == null) {
                this.eventLoopGroup = new NioEventLoopGroup();
            }
            NetworkConnection connection = new NetworkConnection(this);
            ClientHandshakeNetworkHandler handler = new ClientHandshakeNetworkHandler(this, connection);
            connection.setListener(handler);

            Bootstrap bootstrap = new Bootstrap()
                    .group(this.eventLoopGroup)
                    .channel(LocalChannel.class)
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
                bootstrap.connect(address).syncUninterruptibly();
                connection.send(new HandshakePacket(1, HandshakePacket.Intent.LOGIN));
                connection.send(new LoginRequestPacket("KalmeMarq"));
            } catch (Exception e) {
                System.out.println(e.getMessage());
//                this.execute(() -> this.connection = null);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void connect(String ip, int port) {
        Thread thread = new Thread(() -> {
            if (this.eventLoopGroup == null) {
                this.eventLoopGroup = new NioEventLoopGroup();
            }
            NetworkConnection connection = new NetworkConnection(this);
            ClientHandshakeNetworkHandler handler = new ClientHandshakeNetworkHandler(this, connection);
            connection.setListener(handler);

            Bootstrap bootstrap = new Bootstrap()
                    .group(this.eventLoopGroup)
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
                bootstrap.connect(InetAddress.getByName(ip), port).syncUninterruptibly();
                connection.send(new HandshakePacket(1, HandshakePacket.Intent.LOGIN));
                connection.send(new LoginRequestPacket("KalmeMarq"));
            } catch (Exception e) {
                System.out.println(e.getMessage());
//                this.execute(() -> this.connection = null);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public Thread getThread() {
        return this.thread;
    }

    public void tick() {
        ++this.tickCount;

        if (this.connection != null) {
            if (this.connection.isOpen()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }

        if (!this.window.isFocused()) {
            this.inputHandler.releaseAll();
        } else {
            this.inputHandler.tick();

            if (this.menu != null) {
                this.menu.tick();
            } else if (this.level != null && this.level.loaded) {
            }
        }

        for (Entity entity : this.entities) {
            entity.tick();
        }
    }

    @Override
    public void onResize() {
        GL11.glViewport(0, 0, this.window.getWidth(), this.window.getHeight());
    }

    public void run() {
        Configuration.DEBUG.set(true);
        this.window = new GameWindow(160 * 3, 120 * 3, "Minicraft Revitalized");
        System.out.println("GL_VENDOR: " + GL11.glGetString(GL11.GL_VENDOR));
        System.out.println("GL_RENDERER: " + GL11.glGetString(GL11.GL_RENDERER));
        System.out.println("GL_VERSION: " + GL11.glGetString(GL11.GL_VERSION));

        this.window.setWindowEventListener(this);
        this.window.setKeyboardEventHandler(new GameWindow.KeyboardEventHandler() {
            @Override
            public void onKey(int key, int action) {

                if (action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_F3) {
                    Client.this.showDebug = !Client.this.showDebug;
                } else if (action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_F4) {
                    var threadMxBean = ManagementFactory.getThreadMXBean();
                    long[] ids = threadMxBean.getAllThreadIds();
                    for (long id : ids) {
                        System.out.println(threadMxBean.getThreadInfo(id).getThreadName());
                    }
                } else if (action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_F5) {
                    var collectors = ManagementFactory.getGarbageCollectorMXBeans();

                    long i = 0;
                    for (var collector : collectors) {
                        i += collector.getCollectionCount();
                    }

                    System.out.println(i);
                }

                if (Client.this.menu != null) {
                    if (action != GLFW.GLFW_RELEASE) {
                        Client.this.menu.keyPressed(key);
                    }
                } else {
                    Client.this.inputHandler.onKey(key, 0, action, 0);
                    if (action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_ESCAPE) {
                        Client.this.setMenu(new WorldMenu());
                    }
                }
            }

            @Override
            public void onCharTyped(int codepoint) {
                if (Client.this.menu != null) {
                    Client.this.menu.charTyped(codepoint);
                }
            }
        });

        long lastTime = System.nanoTime();
        double unprocessed = 0;
        double nsPerTick = 1000000000.0 / 60;
        int frames = 0;
        int ticks = 0;
        long lastTimer1 = System.currentTimeMillis();

        this.textureManager.addBuiltinTextures();
        this.textureManager.addAtlases();
        GL11.glEnable(GL11.GL_TEXTURE_2D);


        this.loadSettings();

        Translation.load(Translation.currentCode);
        this.setMenu(new TitleMenu());

        this.window.show();

        while (this.running) {
            if (this.window.shouldClose()) this.running = false;

            long now = System.nanoTime();
            double lD  = (now - lastTime) / nsPerTick;
            unprocessed += lD;
            lastTime = now;
            this.runAllTasks();

            while (unprocessed >= 1) {
                ticks++;
                this.tick();
                unprocessed -= 1;
            }

           this.renderer.render();

            this.window.update();

            ++frames;

            if (System.currentTimeMillis() - lastTimer1 > 1000) {
                lastTimer1 += 1000;
                this.debugString = ticks + " TK, " + frames + " FPS, " + this.entities.size() + "\nJava " + System.getProperty("java.version");
                ticks = 0;
                frames = 0;
            }
        }

        this.close();
    }

    private void loadSettings() {
        Path optionsPath = this.saveDir.resolve("settings.toml");
        System.out.println("Loading settings");

        if (Files.exists(optionsPath)) {
            try {
                JsonNode node = IOUtils.TOML_OBJECT_MAPPER.readTree(Files.newInputStream(optionsPath));

                if (node.has("language") && node.get("language").isTextual()) {
                    Translation.currentCode = node.get("language").asText();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSettings() {
        try {
            Path optionsPath = this.saveDir.resolve("settings.toml");
            System.out.println("Saving settings");
            ObjectNode node = IOUtils.TOML_OBJECT_MAPPER.createObjectNode();
            node.put("language", Translation.currentCode);
            Files.writeString(optionsPath, IOUtils.TOML_OBJECT_MAPPER.writeValueAsString(node));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        this.textureManager.close();

        this.window.close();

        this.saveSettings();

        if (this.integratedServer != null) {
            System.out.println("Closing integrated server");
            this.integratedServer.stop(true);
        }

        if (this.eventLoopGroup != null) {
            this.eventLoopGroup.shutdownGracefully();
        }
    }

    public void disconnect() {
        if (this.integratedServer != null) {
            System.out.println("Closing integrated server");
            this.integratedServer.stop(true);
        }

        if (this.eventLoopGroup != null) {
            this.eventLoopGroup.shutdownGracefully();
            this.eventLoopGroup = null;
        }

        this.level = new ClientLevel();
        this.player = null;
        this.entities.clear();
        this.entityMap.clear();
        this.connection = null;
        this.integratedServer = null;
    }
}
