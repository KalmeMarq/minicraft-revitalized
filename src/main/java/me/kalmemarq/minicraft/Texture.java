package me.kalmemarq.minicraft;

import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL45;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture {
	private int handle = -1;
	private int width;
	private int height;

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setFilter(FilterMode mode) {
		GL45.glTextureParameteri(this.handle, GL33.GL_TEXTURE_MIN_FILTER, mode.glEnum);
		GL45.glTextureParameteri(this.handle, GL33.GL_TEXTURE_MAG_FILTER, mode.glEnum);
	}

	public void setWrap(WrapMode mode) {
		GL45.glTextureParameteri(this.handle, GL33.GL_TEXTURE_WRAP_S, mode.glEnum);
		GL45.glTextureParameteri(this.handle, GL33.GL_TEXTURE_WRAP_T, mode.glEnum);
	}

	public void load(int width, int height, int internalFormat, ByteBuffer buffer, int pixelFormat) {
		if (this.handle == -1) {
			this.handle = GL45.glCreateTextures(GL33.GL_TEXTURE_2D);
		}

		GL45.glTextureStorage2D(this.handle, 1, internalFormat, width, height);
		if (buffer != null) {
			GL45.glTextureSubImage2D(this.handle, 0, 0, 0, width, height, pixelFormat, GL33.GL_UNSIGNED_BYTE, buffer);
		}
	}

	public void load(InputStream inputStream) {
		if (this.handle == -1) {
			this.handle = GL45.glCreateTextures(GL33.GL_TEXTURE_2D);
		}

		this.setFilter(FilterMode.NEAREST);
		this.setWrap(WrapMode.REPEAT);

		ByteBuffer buffer = IOUtils.readInputStreamToByteBuffer(inputStream);

		if (buffer != null) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer wP = stack.mallocInt(1);
				IntBuffer hP = stack.mallocInt(1);
				IntBuffer cP = stack.mallocInt(1);

				ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(buffer, wP, hP, cP, 4);

				if (imageBuffer != null) {
					this.width = wP.get(0);
					this.height = hP.get(0);

					this.load(wP.get(0), hP.get(0), GL33.GL_RGBA8, imageBuffer, GL33.GL_RGBA);
					STBImage.stbi_image_free(imageBuffer);
				}
			}

			MemoryUtil.memFree(buffer);
		}
	}

	public int getHandle() {
		return this.handle;
	}

	public void bind(int unit) {
		GL45.glBindTextureUnit(unit, this.handle);
	}

	public void close() {
		GL33.glDeleteTextures(this.handle);
	}

	public enum FilterMode {
		NEAREST(GL33.GL_NEAREST),
		LINEAR(GL33.GL_LINEAR);

		public final int glEnum;

		FilterMode(int glEnum) {
			this.glEnum = glEnum;
		}
	}

	public enum WrapMode {
		REPEAT(GL33.GL_REPEAT),
		CLAMP_TO_EDGE(GL33.GL_CLAMP_TO_EDGE);

		public final int glEnum;

		WrapMode(int glEnum) {
			this.glEnum = glEnum;
		}
	}
}
