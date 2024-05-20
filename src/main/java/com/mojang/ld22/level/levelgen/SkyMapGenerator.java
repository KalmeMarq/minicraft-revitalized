package com.mojang.ld22.level.levelgen;

import com.mojang.ld22.level.tile.Tile;

public class SkyMapGenerator extends LevelMapGenerator {
	public SkyMapGenerator(int width, int height, long seed) {
		super(width, height, seed);
	}

	@Override
	protected byte[][] generate() {
		LevelGen noise1 = new LevelGen(this.random, this.width, this.height, 8);
		LevelGen noise2 = new LevelGen(this.random, this.width, this.height, 8);

		byte[] map = new byte[this.width * this.height];
		byte[] data = new byte[this.width * this.height];
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				int i = x + y * this.width;

				double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;

				double xd = x / (this.width - 1.0) * 2 - 1;
				double yd = y / (this.height - 1.0) * 2 - 1;
				if (xd < 0) xd = -xd;
				if (yd < 0) yd = -yd;
				double dist = Math.max(xd, yd);
				dist = dist * dist * dist * dist;
				dist = dist * dist * dist * dist;
				val = -val * 1 - 2.2;
				val = val + 1 - dist * 20;

				if (val < -0.25) {
					map[i] = Tile.infiniteFall.id;
				} else {
					map[i] = Tile.cloud.id;
				}
			}
		}

		stairsLoop: for (int i = 0; i < this.width * this.height / 50; i++) {
			int x = this.random.nextInt(this.width - 2) + 1;
			int y = this.random.nextInt(this.height - 2) + 1;

			for (int yy = y - 1; yy <= y + 1; yy++)
				for (int xx = x - 1; xx <= x + 1; xx++) {
					if (map[xx + yy * this.width] != Tile.cloud.id) continue stairsLoop;
				}

			map[x + y * this.width] = Tile.cloudCactus.id;
		}

		int count = 0;
		stairsLoop: for (int i = 0; i < this.width * this.height; i++) {
			int x = this.random.nextInt(this.width - 2) + 1;
			int y = this.random.nextInt(this.height - 2) + 1;

			for (int yy = y - 1; yy <= y + 1; yy++)
				for (int xx = x - 1; xx <= x + 1; xx++) {
					if (map[xx + yy * this.width] != Tile.cloud.id) continue stairsLoop;
				}

			map[x + y * this.width] = Tile.stairsDown.id;
			count++;
			if (count == 2) break;
		}

		return new byte[][] { map, data };
	}

	@Override
	public byte[][] generateAndValidate() {
		do {
			byte[][] result = this.generate();

			int[] count = new int[256];

			for (int i = 0; i < this.width * this.height; i++) {
				count[result[0][i] & 0xff]++;
			}
			if (count[Tile.cloud.id & 0xff] < 2000) continue;
			if (count[Tile.stairsDown.id & 0xff] < 2) continue;

			return result;

		} while (true);
	}
}
