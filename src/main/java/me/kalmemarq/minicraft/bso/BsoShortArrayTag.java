package me.kalmemarq.minicraft.bso;

import java.util.Arrays;

public class BsoShortArrayTag implements BsoTag {
	private short[] values;

	public BsoShortArrayTag(short[] values) {
		this.values = values;
	}

	@Override
	public int getId() {
		return 0xA;
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
		this.values = new short[0];
	}

	public short[] getArray() {
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
