package com.mojang.ld22.level.levelgen;

import com.mojang.ld22.level.tile.Tile;

public class UndergroundMapGenerator extends LevelMapGenerator {
	private final int depth;

	public UndergroundMapGenerator(int width, int height, int depth, long seed) {
		super(width, height, seed);
		this.depth = depth;
	}

	@Override
	protected byte[][] generate() {
		LevelGen mnoise1 = new LevelGen(this.random, this.width, this.height, 16);
		LevelGen mnoise2 = new LevelGen(this.random, this.width, this.height, 16);
		LevelGen mnoise3 = new LevelGen(this.random, this.width, this.height, 16);

		LevelGen nnoise1 = new LevelGen(this.random, this.width, this.height, 16);
		LevelGen nnoise2 = new LevelGen(this.random, this.width, this.height, 16);
		LevelGen nnoise3 = new LevelGen(this.random, this.width, this.height, 16);

		LevelGen wnoise1 = new LevelGen(this.random, this.width, this.height, 16);
		LevelGen wnoise2 = new LevelGen(this.random, this.width, this.height, 16);
		LevelGen wnoise3 = new LevelGen(this.random, this.width, this.height, 16);

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

				double nval = Math.abs(nnoise1.values[i] - nnoise2.values[i]);
				nval = Math.abs(nval - nnoise3.values[i]) * 3 - 2;

				double wval = Math.abs(wnoise1.values[i] - wnoise2.values[i]);
				wval = Math.abs(nval - wnoise3.values[i]) * 3 - 2;

				double xd = x / (this.width - 1.0) * 2 - 1;
				double yd = y / (this.height - 1.0) * 2 - 1;
				if (xd < 0) xd = -xd;
				if (yd < 0) yd = -yd;
				double dist = Math.max(xd, yd);
				dist = dist * dist * dist * dist;
				dist = dist * dist * dist * dist;
				val = val + 1 - dist * 20;

				if (val > -2 && wval < -2.0 + (this.depth) / 2 * 3) {
					if (this.depth > 2)
						map[i] = Tile.lava.id;
					else
						map[i] = Tile.water.id;
				} else if (val > -2 && (mval < -1.7 || nval < -1.4)) {
					map[i] = Tile.dirt.id;
				} else {
					map[i] = Tile.rock.id;
				}
			}
		}

		{
			int r = 2;
			for (int i = 0; i < this.width * this.height / 400; i++) {
				int x = this.random.nextInt(this.width);
				int y = this.random.nextInt(this.height);
				for (int j = 0; j < 30; j++) {
					int xx = x + this.random.nextInt(5) - this.random.nextInt(5);
					int yy = y + this.random.nextInt(5) - this.random.nextInt(5);
					if (xx >= r && yy >= r && xx < this.width - r && yy < this.height - r) {
						if (map[xx + yy * this.width] == Tile.rock.id) {
							map[xx + yy * this.width] = (byte) ((Tile.ironOre.id & 0xff) + this.depth - 1);
						}
					}
				}
			}
		}

		if (this.depth < 3) {
			int count = 0;
			stairsLoop: for (int i = 0; i < this.width * this.height / 100; i++) {
				int x = this.random.nextInt(this.width - 20) + 10;
				int y = this.random.nextInt(this.height - 20) + 10;

				for (int yy = y - 1; yy <= y + 1; yy++)
					for (int xx = x - 1; xx <= x + 1; xx++) {
						if (map[xx + yy * this.width] != Tile.rock.id) continue stairsLoop;
					}

				map[x + y * this.width] = Tile.stairsDown.id;
				count++;
				if (count == 4) break;
			}
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
			if (count[Tile.rock.id & 0xff] < 100) continue;
			if (count[Tile.dirt.id & 0xff] < 100) continue;
			if (count[(Tile.ironOre.id & 0xff) + this.depth - 1] < 20) continue;
			if (this.depth < 3) if (count[Tile.stairsDown.id & 0xff] < 2) continue;

			return result;

		} while (true);
	}
}
