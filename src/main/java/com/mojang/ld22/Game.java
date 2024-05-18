package com.mojang.ld22;

import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Font;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.gfx.SpriteSheet;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.screen.DeadMenu;
import com.mojang.ld22.screen.LevelTransitionMenu;
import com.mojang.ld22.screen.Menu;
import com.mojang.ld22.screen.TitleMenu;
import com.mojang.ld22.screen.WonMenu;
import me.kalmemarq.minicraft.Framebuffer;
import me.kalmemarq.minicraft.NativeImage;
import me.kalmemarq.minicraft.ShaderProgram;
import me.kalmemarq.minicraft.SoundManager;
import me.kalmemarq.minicraft.Texture;
import me.kalmemarq.minicraft.VertexBuffer;
import me.kalmemarq.minicraft.VertexLayout;
import me.kalmemarq.minicraft.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class Game implements Runnable, Window.WindowEventHandler {
	public static final Logger LOGGER = LogManager.getLogger(Game.class);
	public static boolean DEBUG_KEYS = false;
	public static boolean SHOW_JFRAME = false;
	public static boolean LWJGL_DEBUG = false;
	public static boolean USE_OPENAL = true;

	public static Game instance;

	public static final String NAME = "Minicraft Revitalized";
	public static final int HEIGHT = 120;
	public static final int WIDTH = 160;
	private static final int SCALE = 3;

	public final Canvas canvas = SHOW_JFRAME ? new Canvas() : null;
	private final BufferedImage image = SHOW_JFRAME ? new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB) : null;
	private final int[] pixels = SHOW_JFRAME ? ((DataBufferInt) this.image.getRaster().getDataBuffer()).getData() : null;
	public boolean running = false;
	public Screen screen;
	private Screen lightScreen;
	private final InputHandler input = new InputHandler(this);

	private int tickCount = 0;
	public int gameTime = 0;

	public Level level;
	private Level[] levels = new Level[5];
	private int currentLevel = 3;
	public Player player;

	public Menu menu;
	private int playerDeadTime;
	private int pendingLevelChange;
	private int wonTimer = 0;
	public boolean hasWon = false;

	public Window window;
	public SoundManager soundManager;
	public ShaderProgram tileShader;
	public ShaderProgram overlayShader;
	public ShaderProgram blitShader;
	public ShaderProgram lightShader;

	public VertexBuffer vaoLight;
	public VertexBuffer vaoBlit;
	public VertexBuffer vaoOverlay;

	public Texture colorsTexture;
	public SpriteSheet colorsSpritesheet;

	private final JFrame frame;

	public boolean screenshotRequested;

	public Game(JFrame frame) {
		instance = this;
		this.frame = frame;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
		if (menu != null) menu.init(this, this.input);
	}

	public void start() {
		this.running = true;
		if (SHOW_JFRAME) new Thread(this).start();
		else this.run();
	}

	public void stop() {
		this.running = false;
	}

	public void resetGame() {
		this.playerDeadTime = 0;
		this.wonTimer = 0;
		this.gameTime = 0;
		this.hasWon = false;

		this.levels = new Level[5];
		this.currentLevel = 3;

		this.levels[4] = new Level(128, 128, 1, null);
		this.levels[3] = new Level(128, 128, 0, this.levels[4]);
		this.levels[2] = new Level(128, 128, -1, this.levels[3]);
		this.levels[1] = new Level(128, 128, -2, this.levels[2]);
		this.levels[0] = new Level(128, 128, -3, this.levels[1]);

		this.level = this.levels[this.currentLevel];
		this.player = new Player(this, this.input);
		this.player.findStartPos(this.level);

		this.level.add(this.player);

		for (int i = 0; i < 5; i++) {
			this.levels[i].trySpawn(5000);
		}
	}

	@Override
	public void onResize() {
	}

	private void init() {
		try {
			this.colorsSpritesheet = SHOW_JFRAME ? new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/textures/colors.png"))), true) : null;
			this.screen = new Screen(WIDTH, HEIGHT, SHOW_JFRAME ? new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons.png")))) : null);
			this.lightScreen = new Screen(WIDTH, HEIGHT, SHOW_JFRAME ? new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/icons.png")))) : null);
		} catch (IOException e) {
			LOGGER.fatal(e);
			this.stop();
		}

		this.resetGame();
		this.setMenu(new TitleMenu());
	}

	private void setupGl() {
		this.tileShader = new ShaderProgram("tile");
		this.overlayShader = new ShaderProgram("overlay");
		this.blitShader = new ShaderProgram("blit");
		this.lightShader = new ShaderProgram("light");

		this.colorsTexture = new Texture();
		this.colorsTexture.load(Game.class.getResourceAsStream("/textures/colors.png"));

		this.vaoLight = new VertexBuffer(VertexLayout.POSITION_TEXTURE);
		this.vaoBlit = new VertexBuffer(VertexLayout.POSITION_TEXTURE);
		this.vaoOverlay = new VertexBuffer(VertexLayout.POSITION_TEXTURE);

		try (MemoryStack stack = MemoryStack.stackPush()) {
			this.vaoLight.upload(stack.floats(
				-1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				-1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, -1.0f, 0.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 0.0f, 1.0f, 0.0f));
		}

		try (MemoryStack stack = MemoryStack.stackPush()) {
			this.vaoOverlay.upload(stack.floats(
				1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
				1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
				-1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
				-1.0f, 1.0f, 0.0f, 0.0f, 1.0f));
		}

		try (MemoryStack stack = MemoryStack.stackPush()) {
			this.vaoBlit.upload(stack.floats(
				0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
				0.0f, HEIGHT, 0.0f, 0.0f, 0.0f,
				WIDTH, HEIGHT, 0.0f, 1.0f, 0.0f,
				WIDTH, 0.0f, 0.0f, 1.0f, 1.0f));
		}

		this.tileShader.bind();

		this.tileShader.setUniform("uProjection", new Matrix4f().setOrtho(0f, WIDTH, HEIGHT, 0f, 1000f, 3000f));
		this.tileShader.setUniform("uView", new Matrix4f().identity().translate(0f, 0f, -2000f));
		this.tileShader.setUniform("uColor", 1.0f, 1.0f, 1.0f, 1.0f);

		this.lightShader.bind();

		this.lightShader.setUniform("uProjection", new Matrix4f().setOrtho(0f, WIDTH, HEIGHT, 0f, 1000f, 3000f));
		this.lightShader.setUniform("uView", new Matrix4f().identity().translate(0f, 0f, -2000f));
		this.lightShader.setUniform("uColor", 1.0f, 1.0f, 1.0f, 1.0f);

		this.overlayShader.bind();

		this.overlayShader.setUniform("uProjection", new Matrix4f().setOrtho(0f, WIDTH, HEIGHT, 0f, 1000f, 3000f));
		this.overlayShader.setUniform("uView", new Matrix4f().identity().translate(0f, 0f, -2000f));
		this.overlayShader.setUniform("uColor", 1.0f, 1.0f, 1.0f, 1.0f);
	}

	private void clearGl() {
		this.colorsTexture.close();
		this.screen.close();
		this.vaoLight.close();
		this.vaoBlit.close();
		this.vaoOverlay.close();
		this.tileShader.close();
		this.blitShader.close();
		this.overlayShader.close();
		this.lightShader.close();
	}

	public void run() {
		this.window = new Window(WIDTH * SCALE, HEIGHT * SCALE, NAME);

		LOGGER.info("Lwjgl {}", Version.getVersion());

		GLFW.glfwSetKeyCallback(this.window.getHandle(), (_w, key, scancode, action, mods) -> this.input.onKey(key, scancode, action, mods));

		long lastTime = System.nanoTime();
		double unprocessed = 0;
		double nsPerTick = 1000000000.0 / 60;
		int frames = 0;
		int ticks = 0;
		long lastTimer1 = System.currentTimeMillis();

		this.init();
		this.setupGl();
		this.soundManager = new SoundManager();
		this.window.show();

		while (this.running) {
			if (this.window.shouldClose()) this.stop();

			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsPerTick;
			lastTime = now;
			while (unprocessed >= 1) {
				ticks++;
				this.tick();
				unprocessed -= 1;
			}

			GL33.glClearColor(0f, 0f, 0f, 1f);
			GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);

			this.screen.getFramebuffer().clearColor();
			this.lightScreen.getFramebuffer().clearColor();

			this.render();

			Framebuffer.unbind();

			GL33.glViewport(0, 0, this.window.getWidth(), this.window.getHeight());

			GL33.glClearColor(0f, 0f, 0f, 1f);
			GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);

			this.blitShader.bind();

			this.vaoOverlay.bind();

			float stretchWidth = (float)this.window.getWidth() / WIDTH;
			float stretchHeight = (float)this.window.getHeight() / HEIGHT;
			float scale = Math.min(stretchWidth, stretchHeight);

			GL45.glBindTextureUnit(0, this.screen.getFramebuffer().getColorAttachment());
			this.blitShader.setUniform("uSampler0", 0);
			this.blitShader.setUniform("uGamma", 1.0f);
			this.blitShader.setUniform("uProjection", new Matrix4f().setOrtho(0, this.window.getWidth(), 0, this.window.getHeight(), 1000, 3000));
			this.blitShader.setUniform("uView", new Matrix4f().identity().translate(0, 0, -2000f));
			this.blitShader.setUniform("uModel", new Matrix4f().identity().translate(this.window.getWidth() / 2f, this.window.getHeight() / 2f, 0).scale(WIDTH * scale * 0.5f, HEIGHT * scale * 0.5f, 1));

			GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0L);

			this.window.update();

			frames++;

			if (System.currentTimeMillis() - lastTimer1 > 1000) {
				lastTimer1 += 1000;
				System.out.println(ticks + " ticks, " + frames + " fps");
				frames = 0;
				ticks = 0;
			}
		}

		System.out.println("Closing");
		this.clearGl();
		this.soundManager.close();
		this.window.close();
		if (SHOW_JFRAME) this.frame.dispose();
	}

	public void tick() {
		this.tickCount++;

		boolean hasFocus = SHOW_JFRAME ? this.canvas.hasFocus() || GLFW.glfwGetWindowAttrib(this.window.getHandle(), GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE : GLFW.glfwGetWindowAttrib(this.window.getHandle(), GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;

		if (!hasFocus) {
			this.input.releaseAll();
		} else {
			this.soundManager.tick();
			if (!this.player.removed && !this.hasWon) this.gameTime++;

			this.input.tick();
			if (this.menu != null) {
				this.menu.tick();
			} else {
				if (this.player.removed) {
					this.playerDeadTime++;
					if (this.playerDeadTime > 60) {
						this.setMenu(new DeadMenu());
					}
				} else {
					if (this.pendingLevelChange != 0) {
						this.setMenu(new LevelTransitionMenu(this.pendingLevelChange));
						this.pendingLevelChange = 0;
					}
				}
				if (this.wonTimer > 0) {
					if (--this.wonTimer == 0) {
						this.setMenu(new WonMenu());
					}
				}
				this.level.tick();
				Tile.tickCount++;
			}
		}
	}

	public void changeLevel(int dir) {
		this.level.remove(this.player);
		this.currentLevel += dir;
		this.level = this.levels[this.currentLevel];
		this.player.x = (this.player.x >> 4) * 16 + 8;
		this.player.y = (this.player.y >> 4) * 16 + 8;
		this.level.add(this.player);

	}

	public void render() {
		BufferStrategy bs = SHOW_JFRAME ? this.canvas.getBufferStrategy() : null;
		if (SHOW_JFRAME && bs == null) {
			this.canvas.createBufferStrategy(3);
			this.canvas.requestFocus();
			return;
		}

		int xScroll = this.player.x - this.screen.w / 2;
		int yScroll = this.player.y - (this.screen.h - 8) / 2;
		if (xScroll < 16) xScroll = 16;
		if (yScroll < 16) yScroll = 16;
		if (xScroll > this.level.w * 16 - this.screen.w - 16) xScroll = this.level.w * 16 - this.screen.w - 16;
		if (yScroll > this.level.h * 16 - this.screen.h - 16) yScroll = this.level.h * 16 - this.screen.h - 16;
		if (this.currentLevel > 3) {
			int col = Color.get(20, 20, 121, 121);
			for (int y = 0; y < 14; y++)
				for (int x = 0; x < 24; x++) {
					this.screen.render(x * 8 - ((xScroll / 4) & 7), y * 8 - ((yScroll / 4) & 7), 0, col, 0);
				}
		}

		this.level.renderBackground(this.screen, xScroll, yScroll);
		this.level.renderSprites(this.screen, xScroll, yScroll);

		if (this.currentLevel < 3) {
			this.lightScreen.clear(0);
			this.level.renderLight(this.lightScreen, xScroll, yScroll);
			this.screen.overlay(this.lightScreen, xScroll, yScroll);
		}

		this.renderGui();

		boolean hasFocus = SHOW_JFRAME ? this.canvas.hasFocus() || GLFW.glfwGetWindowAttrib(this.window.getHandle(), GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE : GLFW.glfwGetWindowAttrib(this.window.getHandle(), GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
		if (!hasFocus) this.renderFocusNagger();

		if (bs != null) {
			for (int y = 0; y < this.screen.h; y++) {
				for (int x = 0; x < this.screen.w; x++) {
					int cc = this.screen.pixels[x + y * this.screen.w];
					if (cc < 255) this.pixels[x + y * WIDTH] = this.colorsSpritesheet.pixels[cc];
				}
			}

			Graphics g = bs.getDrawGraphics();
			g.fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());

			int ww = WIDTH * 3;
			int hh = HEIGHT * 3;
			int xo = (this.canvas.getWidth() - ww) / 2;
			int yo = (this.canvas.getHeight() - hh) / 2;
			g.drawImage(this.image, xo, yo, ww, hh, null);
			g.dispose();
			bs.show();
		}

		if (this.screenshotRequested) {
			NativeImage image = NativeImage.read(this.screen.getFramebuffer());
			if (!image.saveTo(Path.of("bruv2.png"))) {
				System.out.println("Could not save bruh1.png");
			}
			image.close();
			this.screenshotRequested = false;
		}
	}

	private void renderGui() {
		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 20; x++) {
				this.screen.render(x * 8, this.screen.h - 16 + y * 8, 12 * 32, Color.get(0, 0, 0, 0), 0);
			}
		}

		for (int i = 0; i < 10; i++) {
			if (i < this.player.health)
				this.screen.render(i * 8, this.screen.h - 16, 12 * 32, Color.get(0, 200, 500, 533), 0);
			else
				this.screen.render(i * 8, this.screen.h - 16, 12 * 32, Color.get(0, 100, 0, 0), 0);

			if (this.player.staminaRechargeDelay > 0) {
				if (this.player.staminaRechargeDelay / 4 % 2 == 0)
					this.screen.render(i * 8, this.screen.h - 8, 1 + 12 * 32, Color.get(0, 555, 0, 0), 0);
				else
					this.screen.render(i * 8, this.screen.h - 8, 1 + 12 * 32, Color.get(0, 110, 0, 0), 0);
			} else {
				if (i < this.player.stamina)
					this.screen.render(i * 8, this.screen.h - 8, 1 + 12 * 32, Color.get(0, 220, 550, 553), 0);
				else
					this.screen.render(i * 8, this.screen.h - 8, 1 + 12 * 32, Color.get(0, 110, 0, 0), 0);
			}
		}
		if (this.player.activeItem != null) {
			this.player.activeItem.renderInventory(this.screen, 10 * 8, this.screen.h - 16);
		}

		if (this.menu != null) {
			this.menu.render(this.screen);
		}
	}

	private void renderFocusNagger() {
		String msg = "Click to focus!";
		int xx = (WIDTH - msg.length() * 8) / 2;
		int yy = (HEIGHT - 8) / 2;
		int w = msg.length();

		this.screen.render(xx - 8, yy - 8, 13 * 32, Color.get(-1, 1, 5, 445), 0);
		this.screen.render(xx + w * 8, yy - 8, 13 * 32, Color.get(-1, 1, 5, 445), 1);
		this.screen.render(xx - 8, yy + 8, 13 * 32, Color.get(-1, 1, 5, 445), 2);
		this.screen.render(xx + w * 8, yy + 8, 13 * 32, Color.get(-1, 1, 5, 445), 3);
		for (int x = 0; x < w; x++) {
			this.screen.render(xx + x * 8, yy - 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
			this.screen.render(xx + x * 8, yy + 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 2);
		}
		this.screen.render(xx - 8, yy, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
		this.screen.render(xx + w * 8, yy, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 1);

		if ((this.tickCount / 20) % 2 == 0) {
			Font.draw(msg, this.screen, xx, yy, Color.get(5, 333, 333, 333));
		} else {
			Font.draw(msg, this.screen, xx, yy, Color.get(5, 555, 555, 555));
		}
	}

	public void scheduleLevelChange(int dir) {
		this.pendingLevelChange = dir;
	}

	public static void main(String[] args) {
		Configuration.DEBUG.set(LWJGL_DEBUG);
		Configuration.DEBUG_LOADER.set(LWJGL_DEBUG);
		Configuration.DEBUG_MEMORY_ALLOCATOR.set(LWJGL_DEBUG);
		Configuration.DEBUG_STACK.set(LWJGL_DEBUG);

		for (String arg : args) {
			if ("--jframe".equals(arg)) {
				SHOW_JFRAME = true;
			}

			if ("--lwjglDebug".equals(arg)) {
				LWJGL_DEBUG = true;
			}

			if ("--debugKeys".equals(arg)) {
				DEBUG_KEYS = true;
			}

			if ("--noOpenAL".equals(arg)) {
				USE_OPENAL = false;
			}
		}

		JFrame frame = SHOW_JFRAME ? new JFrame(Game.NAME + " JFrame") : null;

		Game game = new Game(frame);

		if (frame != null) {
			game.canvas.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
			game.canvas.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
			game.canvas.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					game.stop();
					frame.dispose();
				}
			});
			frame.setLayout(new BorderLayout());
			frame.add(game.canvas, BorderLayout.CENTER);
			frame.pack();
			frame.setResizable(true);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}

		game.start();
	}

	public void won() {
		this.wonTimer = 60 * 3;
		this.hasWon = true;
	}
}
