/*
 * Minicraft Revitalized.
 * Copyright (C) 2024 KalmeMarq
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */

package me.kalmemarq.minicraft.level.gen;

import me.kalmemarq.minicraft.level.tile.Tiles;

public class TopMapGenerator extends LevelMapGenerator {
    public TopMapGenerator(int width, int height, long seed) {
        super(width, height, seed);
    }

    @Override
    protected byte[][] generate() {
        LevelNoiseMap mnoise1 = new LevelNoiseMap(this.random, this.width, this.height, 16);
        LevelNoiseMap mnoise2 = new LevelNoiseMap(this.random, this.width, this.height, 16);
        LevelNoiseMap mnoise3 = new LevelNoiseMap(this.random, this.width, this.height, 16);

        LevelNoiseMap noise1 = new LevelNoiseMap(this.random, this.width, this.height, 32);
        LevelNoiseMap noise2 = new LevelNoiseMap(this.random, this.width, this.height, 32);

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
                    map[i] = (byte) Tiles.WATER.getNumericId();
                } else if (val > 0.5 && mval < -1.5) {
                    map[i] = (byte) Tiles.ROCK.getNumericId();
                } else {
                    map[i] = (byte) Tiles.GRASS.getNumericId();
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
                                if (map[xx + yy * this.width] == Tiles.GRASS.getNumericId()) {
                                    map[xx + yy * this.width] = (byte) Tiles.SAND.getNumericId();
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
                    if (map[xx + yy * this.width] == Tiles.GRASS.getNumericId()) {
                        map[xx + yy * this.width] = (byte) Tiles.TREE.getNumericId();
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
                    if (map[xx + yy * this.width] == Tiles.GRASS.getNumericId()) {
                        map[xx + yy * this.width] = (byte) Tiles.FLOWER.getNumericId();
                        data[xx + yy * this.width] = (byte) (col + this.random.nextInt(4) * 16);
                    }
                }
            }
        }

        for (int i = 0; i < this.width * this.height / 100; i++) {
            int xx = this.random.nextInt(this.width);
            int yy = this.random.nextInt(this.height);

            if (xx < this.width && yy < this.height) {
                if (map[xx + yy * this.width] == Tiles.SAND.getNumericId()) {
                    map[xx + yy * this.width] = (byte) Tiles.CACTUS.getNumericId();
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
                    if (map[xx + yy * this.width] != Tiles.ROCK.getNumericId()) {
                        continue stairsLoop;
                    }
                }

            map[x + y * this.width] = (byte) Tiles.STAIRS_DOWN.getNumericId();
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
            if (count[Tiles.ROCK.getNumericId() & 0xff] < 100) {
                continue;
            }
            if (count[Tiles.SAND.getNumericId() & 0xff] < 100) {
                continue;
            }
            if (count[Tiles.GRASS.getNumericId() & 0xff] < 100) {
                continue;
            }
            if (count[Tiles.TREE.getNumericId() & 0xff] < 100) {
                continue;
            }
            if (count[Tiles.STAIRS_DOWN.getNumericId() & 0xff] < 2) {
                continue;
            }

            return result;
        } while (true);
    }
}
