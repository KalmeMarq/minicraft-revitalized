package me.kalmemarq.minicraft.bso;

public record BsoStringTag(String value) implements BsoTag {
	@Override
	public int getId() {
		return 0x7;
	}
}
