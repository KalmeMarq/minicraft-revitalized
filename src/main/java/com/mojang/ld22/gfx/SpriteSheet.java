package com.mojang.ld22.gfx;

import java.awt.image.BufferedImage;

public class SpriteSheet {
	public int width, height;
	public int[] pixels;

	public SpriteSheet(BufferedImage image) {
		this(image, false);
	}

	public SpriteSheet(BufferedImage image, boolean isColorPalette) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = image.getRGB(0, 0, this.width, this.height, null, 0, this.width);

		if (isColorPalette) {
			for (int i = 0; i < this.pixels.length; i++) {
				this.pixels[i] = ((this.pixels[i] >> 16) & 0xFF) << 16 | ((this.pixels[i] >> 8) & 0xFF) << 8 | (this.pixels[i] & 0xFF);
			}
		} else {
			for (int i = 0; i < this.pixels.length; i++) {
				this.pixels[i] = (this.pixels[i] & 0xff) / 64;
			}
		}
	}
}
