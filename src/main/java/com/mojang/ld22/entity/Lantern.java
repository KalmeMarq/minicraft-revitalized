package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Color;

public class Lantern extends Furniture {
	public Lantern() {
		super("Lantern");
        this.col = Color.get(-1, 000, 111, 555);
        this.sprite = 5;
        this.xr = 3;
        this.yr = 2;
	}

	public int getLightRadius() {
		return 8;
	}
}