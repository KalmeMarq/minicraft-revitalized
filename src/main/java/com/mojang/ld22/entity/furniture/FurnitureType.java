package com.mojang.ld22.entity.furniture;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;

import java.util.function.Supplier;

public final class FurnitureType<T extends Furniture> {
	public static final FurnitureType<Anvil> ANVIL = new FurnitureType<>(Anvil::new, "Anvil", Color.get(-1, 0, 111, 222), 0, 0);
	public static final FurnitureType<Chest> CHEST = new FurnitureType<>(Chest::new, "Chest", Color.get(-1, 110, 331, 552), 1, 0);
	public static final FurnitureType<Oven> OVEN = new FurnitureType<>(Oven::new, "Oven", Color.get(-1, 0, 332, 442), 2, 0);
	public static final FurnitureType<Furnace> FURNACE = new FurnitureType<>(Furnace::new, "Furnace", Color.get(-1, 0, 222, 333), 3, 0);
	public static final FurnitureType<Workbench> WORKBENCH = new FurnitureType<>(Workbench::new, "Workbench", Color.get(-1, 100, 321, 431), 4, 0);
	public static final FurnitureType<Lantern> LANTERN = new FurnitureType<>(Lantern::new, "Lantern", Color.get(-1, 0, 111, 555), 5, 8);

	public int col, sprite;
	public String name;
	public int lightRadius;
	private final Supplier<T> creator;

	public FurnitureType(Supplier<T> creator, String name, int col, int sprite, int lightRadius) {
		this.creator = creator;
		this.name = name;
		this.col = col;
		this.sprite = sprite;
		this.lightRadius = lightRadius;
	}

	public void render(Screen screen, int x, int y) {
		screen.render(x - 8, y - 8 - 4, this.sprite * 2 + 8 * 32, this.col, 0);
		screen.render(x, y - 8 - 4, this.sprite * 2 + 8 * 32 + 1, this.col, 0);
		screen.render(x - 8, y - 4, this.sprite * 2 + 8 * 32 + 32, this.col, 0);
		screen.render(x, y - 4, this.sprite * 2 + 8 * 32 + 33, this.col, 0);
	}

	public T create() {
		return this.creator.get();
	}
}
