package com.mojang.ld22.entity.furniture;

public class Lantern extends Furniture {
	public Lantern() {
        this.xr = 3;
        this.yr = 2;
	}

	@Override
	public FurnitureType<Lantern> getFurnitureType() {
		return FurnitureType.LANTERN;
	}
}
