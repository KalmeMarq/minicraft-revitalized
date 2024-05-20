package me.kalmemarq.minicraft.bso;

public class BsoLongTag implements BsoTag {
	private final long value;

	public BsoLongTag(long value) {
		this.value = value;
	}

	public long value() {
		return this.value;
	}

	@Override
	public int getId() {
		return 0x4;
	}

	@Override
	public int getAdditionalData() {
		return this.value < Byte.MIN_VALUE || this.value > Byte.MAX_VALUE ? this.value < Short.MIN_VALUE || this.value > Short.MAX_VALUE ? this.value < Integer.MIN_VALUE || this.value > Integer.MAX_VALUE ? 0x0 : 0x1 : 0x2 : 0x3;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof BsoLongTag other) return other.value == this.value;
		return false;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(this.value);
	}
}
