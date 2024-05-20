package me.kalmemarq.minicraft.bso;

public class BsoByteTag implements BsoTag {
	private final byte value;

	BsoByteTag(byte value) {
		this.value = value;
	}

	public byte value() {
		return this.value;
	}

	@Override
	public int getId() {
		return 0x1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof BsoByteTag other) return other.value == this.value;
		return false;
	}

	@Override
	public int hashCode() {
		return this.value;
	}
}
