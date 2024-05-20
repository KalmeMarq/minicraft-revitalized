package com.mojang.ld22.item;

public final class ToolType {
	public static final ToolType SHOVEL = new ToolType("Shvl", 0);
	public static final ToolType HOE = new ToolType("Hoe", 1);
	public static final ToolType SWORD = new ToolType("Swrd", 2);
	public static final ToolType PICKAXE = new ToolType("Pick", 3);
	public static final ToolType AXE = new ToolType("Axe", 4);

	public final String name;
	public final int sprite;

	private ToolType(String name, int sprite) {
		this.name = name;
		this.sprite = sprite;
	}
}
