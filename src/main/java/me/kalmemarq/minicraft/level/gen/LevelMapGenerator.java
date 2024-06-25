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

public abstract class LevelMapGenerator {
    protected final Random random;
    protected final int width;
    protected final int height;

    public LevelMapGenerator(int width, int height, long seed) {
        this.random = new Random(seed);
        this.width = width;
        this.height = height;
    }

    abstract protected byte[][] generate();
    abstract public byte[][] generateAndValidate();
}

