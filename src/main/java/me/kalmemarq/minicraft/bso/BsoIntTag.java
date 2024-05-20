package me.kalmemarq.minicraft.bso;

public class BsoIntTag implements BsoTag {
	private final int value;

	public BsoIntTag(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

	@Override
	public int getId() {
		return 0x3;
	}

	@Override
	public int getAdditionalData() {
		return this.value < Byte.MIN_VALUE || this.value > Byte.MAX_VALUE ? this.value < Short.MIN_VALUE || this.value > Short.MAX_VALUE ? 0x0 : 0x1 : 0x2;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof BsoIntTag other) return other.value == this.value;
		return false;
	}

	@Override
	public int hashCode() {
		return this.value;
	}
}
