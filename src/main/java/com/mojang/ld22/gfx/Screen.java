package com.mojang.ld22.gfx;

import com.mojang.ld22.Game;
import me.kalmemarq.minicraft.render.Framebuffer;
import me.kalmemarq.minicraft.render.Texture;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL45;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Screen {
	/*
	 * public static final int MAP_WIDTH = 64; // Must be 2^x public static final int MAP_WIDTH_MASK = MAP_WIDTH - 1;
	 *
	 * public int[] tiles = new int[MAP_WIDTH * MAP_WIDTH]; public int[] colors = new int[MAP_WIDTH * MAP_WIDTH]; public int[] databits = new int[MAP_WIDTH * MAP_WIDTH];
	 */
	public int xOffset;
	public int yOffset;

	public static final int BIT_MIRROR_X = 0x01;
	public static final int BIT_MIRROR_Y = 0x02;

	public final int w, h;
	public int[] pixels;

	private final SpriteSheet sheet;
	private final Framebuffer framebuffer;
	public Texture texture;

	public static Texture ditherTexture;

	public Screen(int w, int h, SpriteSheet sheet) {
		this.sheet = sheet;
		this.w = w;
		this.h = h;
		this.framebuffer = new Framebuffer(w, h);

        this.pixels = new int[w * h];

		GL33.glEnable(GL33.GL_BLEND);
		GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);

		if (ditherTexture == null) {
			this.texture = new Texture();
			this.texture.load(Game.class.getResourceAsStream("/icons.png"));

			ditherTexture = new Texture();
			ByteBuffer buffer = BufferUtils.createByteBuffer(16);
			buffer.put((byte)0)  .put((byte)80) .put((byte)20) .put((byte)100);
			buffer.put((byte)120).put((byte)40) .put((byte)140).put((byte)60);
			buffer.put((byte)30) .put((byte)110).put((byte)10) .put((byte)90);
			buffer.put((byte)150).put((byte)70) .put((byte)130).put((byte)50);
			buffer.flip();
			ditherTexture.load(4, 4, GL33.GL_RGBA8, buffer, GL33.GL_RED);
		}

		// Random random = new Random();

		/*
		 * for (int i = 0; i < MAP_WIDTH * MAP_WIDTH; i++) { colors[i] = Color.get(00, 40, 50, 40); tiles[i] = 0;
		 *
		 * if (random.nextInt(40) == 0) { tiles[i] = 32; colors[i] = Color.get(111, 40, 222, 333); databits[i] = random.nextInt(2); } else if (random.nextInt(40) == 0) { tiles[i] = 33; colors[i] = Color.get(20, 40, 30, 550); } else { tiles[i] = random.nextInt(4); databits[i] = random.nextInt(4);
		 *
		 * } }
		 *
		 * Font.setMap("Testing the 0341879123", this, 0, 0, Color.get(0, 555, 555, 555));
		 */
	}

	public void close() {
		if (ditherTexture != null) {
			ditherTexture.close();
			ditherTexture = null;
		}
		this.texture.close();
		this.framebuffer.close();
	}

	public Framebuffer getFramebuffer() {
		return this.framebuffer;
	}

	public void clear(int color) {
        Arrays.fill(this.pixels, color);
		this.framebuffer.bind();
		GL33.glClearColor(((color >> 16) & 0xFF) / 255.f, ((color >> 8) & 0xFF) / 255.f, (color & 0xFF) / 255.f, ((color >> 24) & 0xFF) / 255.f);
		GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
	}

	/*
	 * public void renderBackground() { for (int yt = yScroll >> 3; yt <= (yScroll + h) >> 3; yt++) { int yp = yt * 8 - yScroll; for (int xt = xScroll >> 3; xt <= (xScroll + w) >> 3; xt++) { int xp = xt * 8 - xScroll; int ti = (xt & (MAP_WIDTH_MASK)) + (yt & (MAP_WIDTH_MASK)) * MAP_WIDTH; render(xp, yp, tiles[ti], colors[ti], databits[ti]); } }
	 *
	 * for (int i = 0; i < sprites.size(); i++) { Sprite s = sprites.get(i); render(s.x, s.y, s.img, s.col, s.bits); } sprites.clear(); }
	 */

	public void renderBackgroundRGBA(int xp, int yp, int color) {
		xp -= this.xOffset;
		yp -= this.yOffset;

		this.framebuffer.bind();
		GL33.glViewport(0, 0, Game.WIDTH, Game.HEIGHT);

		Game.instance.tileShader.bind();
		Game.instance.vaoBlit.bind();

		this.texture.bind(0);
		Game.instance.colorsTexture.bind(1);
		Game.instance.tileShader.setUniform("uSampler0", 0);
		Game.instance.tileShader.setUniform("uSampler1", 1);

		Game.instance.tileShader.setUniform("uColor", ((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f, ((color) & 0xFF) / 255.0f, 1.0f);
		Game.instance.tileShader.setUniform("uModel", new Matrix4f().identity().translate(xp, yp, 0f));
		Game.instance.tileShader.setUniform("uTile", 0);
		Game.instance.tileShader.setUniform("uMirror", false, false);
		Game.instance.tileShader.setUniform("uTileColors", 0);
		Game.instance.tileShader.setUniform("uTextured", false);
		GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0L);
	}

	public void render(int xp, int yp, int tile, int colors, int bits) {
		xp -= this.xOffset;
		yp -= this.yOffset;
		boolean mirrorX = (bits & BIT_MIRROR_X) > 0;
		boolean mirrorY = (bits & BIT_MIRROR_Y) > 0;

		this.framebuffer.bind();
		GL33.glViewport(0, 0, Game.WIDTH, Game.HEIGHT);

		Game.instance.tileShader.bind();
		Game.instance.vaoOverlay.bind();

		this.texture.bind(0);
		Game.instance.colorsTexture.bind(1);
		Game.instance.tileShader.setUniform("uScreenSampler", 0);
		Game.instance.tileShader.setUniform("uPaletteSampler", 1);

		Game.instance.tileShader.setUniform("uColor", 1.0f, 1.0f, 1.0f, 1.0f);
		Game.instance.tileShader.setUniform("uModel", new Matrix4f().identity().translate(xp + 4, yp + 4, 0f).scale(4));
		Game.instance.tileShader.setUniform("uTile", tile);
		Game.instance.tileShader.setUniform("uMirror", mirrorX, mirrorY);
		Game.instance.tileShader.setUniform("uTileColors", colors);
		Game.instance.tileShader.setUniform("uTextured", true);
		GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0L);

		if (Game.SHOW_JFRAME) {
			int xTile = tile % 32;
			int yTile = tile / 32;
			int toffs = xTile * 8 + yTile * 8 * this.sheet.width;

			for (int y = 0; y < 8; y++) {
				int ys = y;
				if (mirrorY) ys = 7 - y;
				if (y + yp < 0 || y + yp >= this.h) continue;
				for (int x = 0; x < 8; x++) {
					if (x + xp < 0 || x + xp >= this.w) continue;

					int xs = x;
					if (mirrorX) xs = 7 - x;
					int col = (colors >> (this.sheet.pixels[xs + ys * this.sheet.width + toffs] * 8)) & 255;
					if (col < 255) this.pixels[(x + xp) + (y + yp) * this.w] = col;
				}
			}
		}
	}

	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	private final int[] dither = new int[] { 0, 8, 2, 10, 12, 4, 14, 6, 3, 11, 1, 9, 15, 7, 13, 5, };

	public void overlay(Screen screen2, int xa, int ya) {
		this.framebuffer.bind();

		GL33.glViewport(0, 0, Game.WIDTH, Game.HEIGHT);

		Game.instance.overlayShader.bind();
		Game.instance.vaoOverlay.bind();

		Game.instance.overlayShader.setUniform("uProjection", new Matrix4f().setOrtho(0, this.w, 0, this.h, -1, 1));
		Game.instance.overlayShader.setUniform("uModelView", new Matrix4f().identity().translate(this.w / 2.0f, this.h / 2.0f, 0).scale(this.w / 2.0f, this.h / 2.0f, 1));
		Game.instance.overlayShader.setUniform("uAdjust", xa, ya);

		GL45.glBindTextureUnit(0, this.framebuffer.getColorAttachment());
		GL45.glBindTextureUnit(1, screen2.getFramebuffer().getColorAttachment());
		GL45.glBindTextureUnit(2, ditherTexture.getHandle());

		Game.instance.overlayShader.setUniform("uScreenSampler", 0);
		Game.instance.overlayShader.setUniform("uLightSampler", 1);
		Game.instance.overlayShader.setUniform("uDitherSampler", 2);

		// TODO: Do a blue and orange tint in the morning anf afternoon
		float alpha = 0;
		if (Game.instance.currentLevel >= 3) {
			int dayLength = 3600 * 5;
			int gameTime = Game.instance.gameTime % dayLength;
			int transTime = dayLength / 4;
			float relTime = (Game.instance.gameTime % transTime) * 1.0f / transTime;
			if (gameTime >= dayLength / 4 * 3) {
				alpha = 170;
			} else if (gameTime >= dayLength / 4 * 2) {
				alpha = relTime * 170;
			} else if (gameTime >= dayLength / 4 * 1) {
				alpha = 0;
			} else if (gameTime >= 0) {
				alpha = Game.instance.gameTime / dayLength == 0 ? 0 : (1 - relTime) * 170;
			}
		}

		Game.instance.overlayShader.setUniform("uDarknessOverlayAlpha", alpha / 255.0f);

		GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0L);

		if (Game.SHOW_JFRAME) {
			int[] oPixels = screen2.pixels;
			int i = 0;
			for (int y = 0; y < this.h; y++) {
				for (int x = 0; x < this.w; x++) {
					if (oPixels[i] / 10 <= this.dither[((x + xa) & 3) + ((y + ya) & 3) * 4]) this.pixels[i] = 0;
					i++;
				}
			}
		}
	}

	public void renderLight(int x, int y, int r) {
		x -= this.xOffset;
		y -= this.yOffset;

		int x0 = x - r;
		int x1 = x + r;
		int y0 = y - r;
		int y1 = y + r;

		this.framebuffer.bind();
		GL33.glViewport(0, 0, Game.WIDTH, Game.HEIGHT);
		Game.instance.lightShader.bind();
		Game.instance.vaoLight.bind();

		Game.instance.lightShader.setUniform("uProjection", new Matrix4f().setOrtho(0f, Game.WIDTH, Game.HEIGHT, 0f, 1000f, 3000f));
		Game.instance.lightShader.setUniform("uView", new Matrix4f().identity().translate(0f, 0f, -2000f));
		Game.instance.lightShader.setUniform("uColor", 1.0f, 1.0f, 1.0f, 1.0f);
		Game.instance.lightShader.setUniform("uModel", new Matrix4f().identity().translate(x, y, 0).scale(r, r, 1));
		Game.instance.lightShader.setUniform("uRadius", r);
		Game.instance.lightShader.setUniform("uRectangle", x - r, y - r, x + r, y + r);
		GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0L);

		if (Game.SHOW_JFRAME) {
			if (x0 < 0) x0 = 0;
			if (y0 < 0) y0 = 0;
			if (x1 > this.w) x1 = this.w;
			if (y1 > this.h) y1 = this.h;
			// System.out.println(x0 + ", " + x1 + " -> " + y0 + ", " + y1);
			for (int yy = y0; yy < y1; yy++) {
				int yd = yy - y;
				yd = yd * yd;
				for (int xx = x0; xx < x1; xx++) {
					int xd = xx - x;
					int dist = xd * xd + yd;
					// System.out.println(dist);
					if (dist <= r * r) {
						int br = 255 - dist * 255 / (r * r);
						if (this.pixels[xx + yy * this.w] < br) this.pixels[xx + yy * this.w] = br;
					}
				}
			}
		}
	}
}
