package me.kalmemarq.minicraft.bso;

public class BsoFloatTag implements BsoTag {
	private final float value;

	public BsoFloatTag(float value) {
		this.value = value;
	}

	public float value() {
		return this.value;
	}

	@Override
	public int getId() {
		return 0x5;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof BsoFloatTag other) return other.value == this.value;
		return false;
	}

	@Override
	public int hashCode() {
		return Float.hashCode(this.value);
	}
}
