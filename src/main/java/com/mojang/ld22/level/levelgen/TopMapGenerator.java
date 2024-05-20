package com.mojang.ld22.level.levelgen;

import com.mojang.ld22.level.tile.Tile;

public class TopMapGenerator extends LevelMapGenerator {
	public TopMapGenerator(int width, int height, long seed) {
		super(width, height, seed);
	}

	@Override
	protected byte[][] generate() {
		LevelGen mnoise1 = new LevelGen(this.random, this.width, this.height, 16);
		LevelGen mnoise2 = new LevelGen(this.random, this.width, this.height, 16);
		LevelGen mnoise3 = new LevelGen(this.random, this.width, this.height, 16);

		LevelGen noise1 = new LevelGen(this.random, this.width, this.height, 32);
		LevelGen noise2 = new LevelGen(this.random, this.width, this.height, 32);

		byte[] map = new byte[this.width * this.height];
		byte[] data = new byte[this.width * this.height];

		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				int i = x + y * this.width;

				double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;
				double mval = Math.abs(mnoise1.values[i] - mnoise2.values[i]);
				mval = Math.abs(mval - mnoise3.values[i]) * 3 - 2;

				double xd = x / (this.width - 1.0) * 2 - 1;
				double yd = y / (this.height - 1.0) * 2 - 1;
				if (xd < 0) {
					xd = -xd;
				}
				if (yd < 0) {
					yd = -yd;
				}
				double dist = Math.max(xd, yd);
				dist = dist * dist * dist * dist;
				dist = dist * dist * dist * dist;
				val = val + 1 - dist * 20;

				if (val < -0.5) {
					map[i] = Tile.water.id;
				} else if (val > 0.5 && mval < -1.5) {
					map[i] = Tile.rock.id;
				} else {
					map[i] = Tile.grass.id;
				}
			}
		}

		for (int i = 0; i < this.width * this.height / 2800; i++) {
			int xs = this.random.nextInt(this.width);
			int ys = this.random.nextInt(this.height);

			for (int k = 0; k < 10; k++) {
				int x = xs + this.random.nextInt(21) - 10;
				int y = ys + this.random.nextInt(21) - 10;

				for (int j = 0; j < 100; j++) {
					int xo = x + this.random.nextInt(5) - this.random.nextInt(5);
					int yo = y + this.random.nextInt(5) - this.random.nextInt(5);

					for (int yy = yo - 1; yy <= yo + 1; yy++) {
						for (int xx = xo - 1; xx <= xo + 1; xx++) {
							if (xx >= 0 && yy >= 0 && xx < this.width && yy < this.height) {
								if (map[xx + yy * this.width] == Tile.grass.id) {
									map[xx + yy * this.width] = Tile.sand.id;
								}
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < this.width * this.height / 400; i++) {
			int x = this.random.nextInt(this.width);
			int y = this.random.nextInt(this.height);

			for (int j = 0; j < 200; j++) {
				int xx = x + this.random.nextInt(15) - this.random.nextInt(15);
				int yy = y + this.random.nextInt(15) - this.random.nextInt(15);

				if (xx >= 0 && yy >= 0 && xx < this.width && yy < this.height) {
					if (map[xx + yy * this.width] == Tile.grass.id) {
						map[xx + yy * this.width] = Tile.tree.id;
					}
				}
			}
		}

		for (int i = 0; i < this.width * this.height / 400; i++) {
			int x = this.random.nextInt(this.width);
			int y = this.random.nextInt(this.height);
			int col = this.random.nextInt(4);

			for (int j = 0; j < 30; j++) {
				int xx = x + this.random.nextInt(5) - this.random.nextInt(5);
				int yy = y + this.random.nextInt(5) - this.random.nextInt(5);

				if (xx >= 0 && yy >= 0 && xx < this.width && yy < this.height) {
					if (map[xx + yy * this.width] == Tile.grass.id) {
						map[xx + yy * this.width] = Tile.flower.id;
						data[xx + yy * this.width] = (byte) (col + this.random.nextInt(4) * 16);
					}
				}
			}
		}

		for (int i = 0; i < this.width * this.height / 100; i++) {
			int xx = this.random.nextInt(this.width);
			int yy = this.random.nextInt(this.height);

			if (xx < this.width && yy < this.height) {
				if (map[xx + yy * this.width] == Tile.sand.id) {
					map[xx + yy * this.width] = Tile.cactus.id;
				}
			}
		}

		int count = 0;
		stairsLoop:
		for (int i = 0; i < this.width * this.height / 100; i++) {
			int x = this.random.nextInt(this.width - 2) + 1;
			int y = this.random.nextInt(this.height - 2) + 1;

			for (int yy = y - 1; yy <= y + 1; yy++)
				for (int xx = x - 1; xx <= x + 1; xx++) {
					if (map[xx + yy * this.width] != Tile.rock.id) {
						continue stairsLoop;
					}
				}

			map[x + y * this.width] = Tile.stairsDown.id;
			count++;
			if (count == 4) {
				break;
			}
		}

		return new byte[][]{map, data};
	}

	@Override
	public byte[][] generateAndValidate() {
		do {
			byte[][] result = this.generate();

			int[] count = new int[256];

			for (int i = 0; i < this.width * this.height; i++) {
				count[result[0][i] & 0xff]++;
			}
			if (count[Tile.rock.id & 0xff] < 100) {
				continue;
			}
			if (count[Tile.sand.id & 0xff] < 100) {
				continue;
			}
			if (count[Tile.grass.id & 0xff] < 100) {
				continue;
			}
			if (count[Tile.tree.id & 0xff] < 100) {
				continue;
			}
			if (count[Tile.stairsDown.id & 0xff] < 2) {
				continue;
			}

			return result;
		} while (true);
	}
}
