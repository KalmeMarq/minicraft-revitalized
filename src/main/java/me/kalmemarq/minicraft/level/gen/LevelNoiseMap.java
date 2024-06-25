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

import java.util.Random;

public class LevelNoiseMap {
    private final int width;
    private final int height;
    public double[] values;

    public LevelNoiseMap(Random random, int width, int height, int featureSize) {
        this.width = width;
        this.height = height;

        this.values = new double[width * height];

        for (int y = 0; y < width; y += featureSize) {
            for (int x = 0; x < width; x += featureSize) {
                this.setSample(x, y, random.nextFloat() * 2 - 1);
            }
        }

        int stepSize = featureSize;
        double scale = 1.0 / width;
        double scaleMod = 1;
        do {
            int halfStep = stepSize / 2;
            for (int y = 0; y < width; y += stepSize) {
                for (int x = 0; x < width; x += stepSize) {
                    double a = this.sample(x, y);
                    double b = this.sample(x + stepSize, y);
                    double c = this.sample(x, y + stepSize);
                    double d = this.sample(x + stepSize, y + stepSize);

                    double e = (a + b + c + d) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale;
                    this.setSample(x + halfStep, y + halfStep, e);
                }
            }
            for (int y = 0; y < width; y += stepSize) {
                for (int x = 0; x < width; x += stepSize) {
                    double a = this.sample(x, y);
                    double b = this.sample(x + stepSize, y);
                    double c = this.sample(x, y + stepSize);
                    double d = this.sample(x + halfStep, y + halfStep);
                    double e = this.sample(x + halfStep, y - halfStep);
                    double f = this.sample(x - halfStep, y + halfStep);

                    double H = (a + b + d + e) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale * 0.5;
                    double g = (a + c + d + f) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale * 0.5;
                    this.setSample(x + halfStep, y, H);
                    this.setSample(x, y + halfStep, g);
                }
            }
            stepSize /= 2;
            scale *= (scaleMod + 0.8);
            scaleMod *= 0.3;
        } while (stepSize > 1);
    }

    private double sample(int x, int y) {
        return this.values[(x & (this.width - 1)) + (y & (this.height - 1)) * this.width];
    }

    private void setSample(int x, int y, double value) {
        this.values[(x & (this.width - 1)) + (y & (this.height - 1)) * this.width] = value;
    }
}
