package me.kalmemarq.minicraft;

import org.lwjgl.opengl.GL33;

public class VertexLayout {
	public static final VertexLayout POSITION_TEXTURE = new VertexLayout(VertexAttribute.POSITION, VertexAttribute.TEXTURE);

	private final VertexAttribute[] attributes;
	private final int stride;
	private final int[] offsets;

	public VertexLayout(VertexAttribute... attributes) {
		this.attributes = attributes;
		this.offsets = new int[attributes.length];

		int strd = 0;
		for (int i = 0; i < this.attributes.length; ++i) {
			this.offsets[i] = strd;
			strd += this.attributes[i].byteLength;
		}
		this.stride = strd;
	}

	public void enable() {
		for (int i = 0; i < this.attributes.length; ++i) {
			this.attributes[i].enable(i, this.stride, this.offsets[i]);
		}
	}

	public void disable() {
		for (int i = 0; i < this.attributes.length; ++i) {
			this.attributes[i].disable(i);
		}
	}

	public enum VertexAttribute {
		POSITION(3, ComponentType.FLOAT),
		TEXTURE(2, ComponentType.FLOAT);

		public final int size;
		public final ComponentType type;
		public final int byteLength;

		VertexAttribute(int size, ComponentType type) {
			this.size = size;
			this.type = type;
			this.byteLength = size * type.byteLength;
		}

		public void enable(int index, int stride, int offset) {
			GL33.glEnableVertexAttribArray(index);
			GL33.glVertexAttribPointer(index, this.size, this.type.glEnum, false, stride, offset);
		}

		public void disable(int index) {
			GL33.glDisableVertexAttribArray(index);
		}
	}

	public enum ComponentType {
		FLOAT(4, GL33.GL_FLOAT);

		public final int byteLength;
		public final int glEnum;

		ComponentType(int byteLength, int glEnum) {
			this.byteLength = byteLength;
			this.glEnum = glEnum;
		}
	}
}
