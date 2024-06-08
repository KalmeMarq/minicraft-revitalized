package me.kalmemarq.minicraft.bso;

import java.util.Arrays;

public class BsoFloatArrayTag implements BsoTag {
	private float[] values;

	public BsoFloatArrayTag(float[] values) {
		this.values = values;
	}

	@Override
	public int getId() {
		return 0x0A;
	}

	@Override
	public int getAdditionalData() {
		if (this.values.length <= (Byte.MAX_VALUE * 2) + 1) {
			return 0x2;
		} else if (this.values.length <= (Short.MAX_VALUE * 2) + 1) {
			return 0x1;
		}
		return 0x0;
	}

	public void clear() {
		this.values = new float[0];
	}

	public float[] getArray() {
		return this.values;
	}

	public int size() {
		return this.values.length;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.values);
	}
}
