package me.kalmemarq.minicraft.bso;

public class BsoDoubleTag implements BsoTag {
	private final double value;

	public BsoDoubleTag(double value) {
		this.value = value;
	}

	public double value() {
		return this.value;
	}

	@Override
	public int getId() {
		return 0x6;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof BsoDoubleTag other) return other.value == this.value;
		return false;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(this.value);
	}
}
