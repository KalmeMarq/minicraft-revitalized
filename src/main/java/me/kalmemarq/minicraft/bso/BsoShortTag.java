package me.kalmemarq.minicraft.bso;

public class BsoShortTag implements BsoTag {
	private final short value;

	public BsoShortTag(short value) {
		this.value = value;
	}

	public short value() {
		return this.value;
	}

	@Override
	public int getId() {
		return 0x2;
	}

	@Override
	public int getAdditionalData() {
		return this.value < Byte.MIN_VALUE || this.value > Byte.MAX_VALUE ? 0x0 : 0x1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof BsoShortTag other) return other.value == this.value;
		return false;
	}

	@Override
	public int hashCode() {
		return this.value;
	}
}
