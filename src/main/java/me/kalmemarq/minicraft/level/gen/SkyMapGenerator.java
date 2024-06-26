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

public class SkyMapGenerator extends LevelMapGenerator {
    public SkyMapGenerator(int width, int height, long seed) {
        super(width, height, seed);
    }

    @Override
    protected byte[][] generate() {
        LevelNoiseMap noise1 = new LevelNoiseMap(this.random, this.width, this.height, 8);
        LevelNoiseMap noise2 = new LevelNoiseMap(this.random, this.width, this.height, 8);

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
                    map[i] = (byte) Tiles.INFINITE_FALL.getNumericId();
                } else {
                    map[i] = (byte) Tiles.CLOUD.getNumericId();
                }
            }
        }

        stairsLoop: for (int i = 0; i < this.width * this.height / 50; i++) {
            int x = this.random.nextInt(this.width - 2) + 1;
            int y = this.random.nextInt(this.height - 2) + 1;

            for (int yy = y - 1; yy <= y + 1; yy++)
                for (int xx = x - 1; xx <= x + 1; xx++) {
                    if (map[xx + yy * this.width] != Tiles.CLOUD.getNumericId()) continue stairsLoop;
                }

            map[x + y * this.width] = (byte) Tiles.CLOUD_CACTUS.getNumericId();
        }

        int count = 0;
        stairsLoop: for (int i = 0; i < this.width * this.height; i++) {
            int x = this.random.nextInt(this.width - 2) + 1;
            int y = this.random.nextInt(this.height - 2) + 1;

            for (int yy = y - 1; yy <= y + 1; yy++)
                for (int xx = x - 1; xx <= x + 1; xx++) {
                    if (map[xx + yy * this.width] != Tiles.CLOUD.getNumericId()) continue stairsLoop;
                }

            map[x + y * this.width] = (byte) Tiles.STAIRS_DOWN.getNumericId();
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
            if (count[Tiles.CLOUD.getNumericId() & 0xff] < 2000) continue;
            if (count[Tiles.STAIRS_DOWN.getNumericId() & 0xff] < 2) continue;

            return result;

        } while (true);
    }
}
