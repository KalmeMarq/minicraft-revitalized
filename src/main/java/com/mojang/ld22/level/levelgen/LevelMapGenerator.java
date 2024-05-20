package com.mojang.ld22.level.levelgen;

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
