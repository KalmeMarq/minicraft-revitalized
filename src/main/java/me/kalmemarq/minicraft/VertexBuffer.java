package me.kalmemarq.minicraft;

import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class VertexBuffer {
	private static int currentVao = -1;

	private final VertexLayout layout;
	private final int vao;
	private final int vbo;
	private final int ibo;

	public VertexBuffer(VertexLayout layout) {
		this.layout = layout;
		this.vao = GL33.glGenVertexArrays();
		this.vbo = GL33.glGenBuffers();
		this.ibo = GL33.glGenBuffers();
	}

	public void upload(FloatBuffer buffer) {
		GL33.glBindVertexArray(this.vao);
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, this.vbo);
		GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, this.ibo);

		this.layout.enable();

		GL33.glBufferData(GL33.GL_ARRAY_BUFFER, buffer, GL33.GL_STATIC_DRAW);

		try (MemoryStack stack = MemoryStack.stackPush()) {
			GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, stack.ints(0, 1, 2, 2, 3, 0), GL33.GL_STATIC_DRAW);
		}
	}

	public void bind() {
		if (currentVao != this.vao) {
			GL33.glBindVertexArray(this.vao);
			currentVao = this.vao;
		}
	}

	public void unbind() {
		GL33.glBindVertexArray(0);
		currentVao = -1;
	}

	public void close() {
		GL33.glDeleteBuffers(this.ibo);
		GL33.glDeleteBuffers(this.vbo);
		GL33.glDeleteVertexArrays(this.vao);
	}
}
