package me.kalmemarq.minicraft;

import com.mojang.ld22.Game;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;

public class Framebuffer {
	private static int currentId = -1;

	private final int width;
	private final int height;

	private final int handle;
	private final Texture texture;
	private final int renderbufferHandle;

	public Framebuffer(int width, int height) {
		this.handle = GL45.glCreateFramebuffers();

		this.width = width;
		this.height = height;

		this.texture = new Texture();
		this.texture.load(width, height, GL33.GL_RGB8, null, 0);
		this.texture.setFilter(Texture.FilterMode.NEAREST);
		this.texture.setWrap(Texture.WrapMode.CLAMP_TO_EDGE);
		GL45.glNamedFramebufferTexture(this.handle, GL33.GL_COLOR_ATTACHMENT0, this.texture.getHandle(), 0);

		this.renderbufferHandle = GL45.glCreateRenderbuffers();
		GL45.glNamedRenderbufferStorage(this.renderbufferHandle, GL33.GL_DEPTH24_STENCIL8, width, height);
		GL45.glNamedFramebufferRenderbuffer(this.handle, GL33.GL_DEPTH_STENCIL_ATTACHMENT, GL33.GL_RENDERBUFFER, this.renderbufferHandle);

		int status = GL45.glCheckNamedFramebufferStatus(this.handle, GL33.GL_FRAMEBUFFER);
		if (status != GL33.GL_FRAMEBUFFER_COMPLETE) {
			Game.LOGGER.error("Framebuffer is not complete! {}", getStatusError(status));
		} else {
			Game.LOGGER.info("Framebuffer was complete!");
		}
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public static String getStatusError(int status) {
		return switch (status) {
			case GL33.GL_FRAMEBUFFER_UNDEFINED -> "FRAMEBUFFER_UNDEFINED";
			case GL33.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
			case GL33.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> "FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
			case GL33.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER -> "FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER";
			case GL33.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER -> "FRAMEBUFFER_INCOMPLETE_READ_BUFFER";
			case GL33.GL_FRAMEBUFFER_UNSUPPORTED -> "FRAMEBUFFER_UNSUPPORTED";
			case GL33.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE -> "FRAMEBUFFER_INCOMPLETE_MULTISAMPLE";
			case GL33.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS -> "GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS";
			default -> "Error: " + status;
		};
	}

	public void clearColor() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			GL45.glClearNamedFramebufferfv(this.handle, GL33.GL_COLOR, 0, stack.floats(0f, 0f, 0f, 1f));
		}
	}

	public void bind() {
		if (currentId != this.handle) {
			GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, this.handle);
			currentId = this.handle;
		}
	}

	public static void unbind() {
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
		currentId = -1;
	}

	public int getColorAttachment() {
		return this.texture.getHandle();
	}

	public void close() {
		GL33.glDeleteTextures(this.texture.getHandle());
		GL33.glDeleteRenderbuffers(this.renderbufferHandle);
		GL33.glDeleteFramebuffers(this.handle);
	}
}
