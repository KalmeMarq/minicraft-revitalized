package com.mojang.ld22.level.levelgen;

import com.mojang.ld22.level.tile.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.function.Supplier;

public class LevelGen {
	private static final Random random = new Random();
	public double[] values;
	private final int w;
    private final int h;

	public LevelGen(int w, int h, int featureSize) {
		this.w = w;
		this.h = h;

        this.values = new double[w * h];

		for (int y = 0; y < w; y += featureSize) {
			for (int x = 0; x < w; x += featureSize) {
                this.setSample(x, y, random.nextFloat() * 2 - 1);
			}
		}

		int stepSize = featureSize;
		double scale = 1.0 / w;
		double scaleMod = 1;
		do {
			int halfStep = stepSize / 2;
			for (int y = 0; y < w; y += stepSize) {
				for (int x = 0; x < w; x += stepSize) {
					double a = this.sample(x, y);
					double b = this.sample(x + stepSize, y);
					double c = this.sample(x, y + stepSize);
					double d = this.sample(x + stepSize, y + stepSize);

					double e = (a + b + c + d) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale;
                    this.setSample(x + halfStep, y + halfStep, e);
				}
			}
			for (int y = 0; y < w; y += stepSize) {
				for (int x = 0; x < w; x += stepSize) {
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
		return this.values[(x & (this.w - 1)) + (y & (this.h - 1)) * this.w];
	}

	private void setSample(int x, int y, double value) {
        this.values[(x & (this.w - 1)) + (y & (this.h - 1)) * this.w] = value;
	}

	public static byte[][] createAndValidateTopMap(int w, int h) {
		int attempt = 0;
		do {
			byte[][] result = createTopMap(w, h);

			int[] count = new int[256];

			for (int i = 0; i < w * h; i++) {
				count[result[0][i] & 0xff]++;
			}
			if (count[Tile.rock.id & 0xff] < 100) continue;
			if (count[Tile.sand.id & 0xff] < 100) continue;
			if (count[Tile.grass.id & 0xff] < 100) continue;
			if (count[Tile.tree.id & 0xff] < 100) continue;
			if (count[Tile.stairsDown.id & 0xff] < 2) continue;

			return result;

		} while (true);
	}

	public static byte[][] createAndValidateUndergroundMap(int w, int h, int depth) {
		int attempt = 0;
		do {
			byte[][] result = createUndergroundMap(w, h, depth);

			int[] count = new int[256];

			for (int i = 0; i < w * h; i++) {
				count[result[0][i] & 0xff]++;
			}
			if (count[Tile.rock.id & 0xff] < 100) continue;
			if (count[Tile.dirt.id & 0xff] < 100) continue;
			if (count[(Tile.ironOre.id & 0xff) + depth - 1] < 20) continue;
			if (depth < 3) if (count[Tile.stairsDown.id & 0xff] < 2) continue;

			return result;

		} while (true);
	}

	public static byte[][] createAndValidateSkyMap(int w, int h) {
		int attempt = 0;
		do {
			byte[][] result = createSkyMap(w, h);

			int[] count = new int[256];

			for (int i = 0; i < w * h; i++) {
				count[result[0][i] & 0xff]++;
			}
			if (count[Tile.cloud.id & 0xff] < 2000) continue;
			if (count[Tile.stairsDown.id & 0xff] < 2) continue;

			return result;

		} while (true);
	}

	private static byte[][] createTopMap(int w, int h) {
		LevelGen mnoise1 = new LevelGen(w, h, 16);
		LevelGen mnoise2 = new LevelGen(w, h, 16);
		LevelGen mnoise3 = new LevelGen(w, h, 16);

		LevelGen noise1 = new LevelGen(w, h, 32);
		LevelGen noise2 = new LevelGen(w, h, 32);

		byte[] map = new byte[w * h];
		byte[] data = new byte[w * h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int i = x + y * w;

				double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;
				double mval = Math.abs(mnoise1.values[i] - mnoise2.values[i]);
				mval = Math.abs(mval - mnoise3.values[i]) * 3 - 2;

				double xd = x / (w - 1.0) * 2 - 1;
				double yd = y / (h - 1.0) * 2 - 1;
				if (xd < 0) xd = -xd;
				if (yd < 0) yd = -yd;
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

		for (int i = 0; i < w * h / 2800; i++) {
			int xs = random.nextInt(w);
			int ys = random.nextInt(h);
			for (int k = 0; k < 10; k++) {
				int x = xs + random.nextInt(21) - 10;
				int y = ys + random.nextInt(21) - 10;
				for (int j = 0; j < 100; j++) {
					int xo = x + random.nextInt(5) - random.nextInt(5);
					int yo = y + random.nextInt(5) - random.nextInt(5);
					for (int yy = yo - 1; yy <= yo + 1; yy++)
						for (int xx = xo - 1; xx <= xo + 1; xx++)
							if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
								if (map[xx + yy * w] == Tile.grass.id) {
									map[xx + yy * w] = Tile.sand.id;
								}
							}
				}
			}
		}

		/*
		 * for (int i = 0; i < w * h / 2800; i++) { int xs = random.nextInt(w); int ys = random.nextInt(h); for (int k = 0; k < 10; k++) { int x = xs + random.nextInt(21) - 10; int y = ys + random.nextInt(21) - 10; for (int j = 0; j < 100; j++) { int xo = x + random.nextInt(5) - random.nextInt(5); int yo = y + random.nextInt(5) - random.nextInt(5); for (int yy = yo - 1; yy <= yo + 1; yy++) for (int xx = xo - 1; xx <= xo + 1; xx++) if (xx >= 0 && yy >= 0 && xx < w && yy < h) { if (map[xx + yy * w] == Tile.grass.id) { map[xx + yy * w] = Tile.dirt.id; } } } } }
		 */

		for (int i = 0; i < w * h / 400; i++) {
			int x = random.nextInt(w);
			int y = random.nextInt(h);
			for (int j = 0; j < 200; j++) {
				int xx = x + random.nextInt(15) - random.nextInt(15);
				int yy = y + random.nextInt(15) - random.nextInt(15);
				if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
					if (map[xx + yy * w] == Tile.grass.id) {
						map[xx + yy * w] = Tile.tree.id;
					}
				}
			}
		}

		for (int i = 0; i < w * h / 400; i++) {
			int x = random.nextInt(w);
			int y = random.nextInt(h);
			int col = random.nextInt(4);
			for (int j = 0; j < 30; j++) {
				int xx = x + random.nextInt(5) - random.nextInt(5);
				int yy = y + random.nextInt(5) - random.nextInt(5);
				if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
					if (map[xx + yy * w] == Tile.grass.id) {
						map[xx + yy * w] = Tile.flower.id;
						data[xx + yy * w] = (byte) (col + random.nextInt(4) * 16);
					}
				}
			}
		}

		for (int i = 0; i < w * h / 100; i++) {
			int xx = random.nextInt(w);
			int yy = random.nextInt(h);
			if (xx < w && yy < h) {
				if (map[xx + yy * w] == Tile.sand.id) {
					map[xx + yy * w] = Tile.cactus.id;
				}
			}
		}

		int count = 0;
		stairsLoop: for (int i = 0; i < w * h / 100; i++) {
			int x = random.nextInt(w - 2) + 1;
			int y = random.nextInt(h - 2) + 1;

			for (int yy = y - 1; yy <= y + 1; yy++)
				for (int xx = x - 1; xx <= x + 1; xx++) {
					if (map[xx + yy * w] != Tile.rock.id) continue stairsLoop;
				}

			map[x + y * w] = Tile.stairsDown.id;
			count++;
			if (count == 4) break;
		}

		return new byte[][] { map, data };
	}

	private static byte[][] createUndergroundMap(int w, int h, int depth) {
		LevelGen mnoise1 = new LevelGen(w, h, 16);
		LevelGen mnoise2 = new LevelGen(w, h, 16);
		LevelGen mnoise3 = new LevelGen(w, h, 16);

		LevelGen nnoise1 = new LevelGen(w, h, 16);
		LevelGen nnoise2 = new LevelGen(w, h, 16);
		LevelGen nnoise3 = new LevelGen(w, h, 16);

		LevelGen wnoise1 = new LevelGen(w, h, 16);
		LevelGen wnoise2 = new LevelGen(w, h, 16);
		LevelGen wnoise3 = new LevelGen(w, h, 16);

		LevelGen noise1 = new LevelGen(w, h, 32);
		LevelGen noise2 = new LevelGen(w, h, 32);

		byte[] map = new byte[w * h];
		byte[] data = new byte[w * h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int i = x + y * w;

				double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;

				double mval = Math.abs(mnoise1.values[i] - mnoise2.values[i]);
				mval = Math.abs(mval - mnoise3.values[i]) * 3 - 2;

				double nval = Math.abs(nnoise1.values[i] - nnoise2.values[i]);
				nval = Math.abs(nval - nnoise3.values[i]) * 3 - 2;

				double wval = Math.abs(wnoise1.values[i] - wnoise2.values[i]);
				wval = Math.abs(nval - wnoise3.values[i]) * 3 - 2;

				double xd = x / (w - 1.0) * 2 - 1;
				double yd = y / (h - 1.0) * 2 - 1;
				if (xd < 0) xd = -xd;
				if (yd < 0) yd = -yd;
				double dist = Math.max(xd, yd);
				dist = dist * dist * dist * dist;
				dist = dist * dist * dist * dist;
				val = val + 1 - dist * 20;

				if (val > -2 && wval < -2.0 + (depth) / 2 * 3) {
					if (depth > 2)
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
			for (int i = 0; i < w * h / 400; i++) {
				int x = random.nextInt(w);
				int y = random.nextInt(h);
				for (int j = 0; j < 30; j++) {
					int xx = x + random.nextInt(5) - random.nextInt(5);
					int yy = y + random.nextInt(5) - random.nextInt(5);
					if (xx >= r && yy >= r && xx < w - r && yy < h - r) {
						if (map[xx + yy * w] == Tile.rock.id) {
							map[xx + yy * w] = (byte) ((Tile.ironOre.id & 0xff) + depth - 1);
						}
					}
				}
			}
		}

		if (depth < 3) {
			int count = 0;
			stairsLoop: for (int i = 0; i < w * h / 100; i++) {
				int x = random.nextInt(w - 20) + 10;
				int y = random.nextInt(h - 20) + 10;

				for (int yy = y - 1; yy <= y + 1; yy++)
					for (int xx = x - 1; xx <= x + 1; xx++) {
						if (map[xx + yy * w] != Tile.rock.id) continue stairsLoop;
					}

				map[x + y * w] = Tile.stairsDown.id;
				count++;
				if (count == 4) break;
			}
		}

		return new byte[][] { map, data };
	}

	private static byte[][] createSkyMap(int w, int h) {
		LevelGen noise1 = new LevelGen(w, h, 8);
		LevelGen noise2 = new LevelGen(w, h, 8);

		byte[] map = new byte[w * h];
		byte[] data = new byte[w * h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int i = x + y * w;

				double val = Math.abs(noise1.values[i] - noise2.values[i]) * 3 - 2;

				double xd = x / (w - 1.0) * 2 - 1;
				double yd = y / (h - 1.0) * 2 - 1;
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

		stairsLoop: for (int i = 0; i < w * h / 50; i++) {
			int x = random.nextInt(w - 2) + 1;
			int y = random.nextInt(h - 2) + 1;

			for (int yy = y - 1; yy <= y + 1; yy++)
				for (int xx = x - 1; xx <= x + 1; xx++) {
					if (map[xx + yy * w] != Tile.cloud.id) continue stairsLoop;
				}

			map[x + y * w] = Tile.cloudCactus.id;
		}

		int count = 0;
		stairsLoop: for (int i = 0; i < w * h; i++) {
			int x = random.nextInt(w - 2) + 1;
			int y = random.nextInt(h - 2) + 1;

			for (int yy = y - 1; yy <= y + 1; yy++)
				for (int xx = x - 1; xx <= x + 1; xx++) {
					if (map[xx + yy * w] != Tile.cloud.id) continue stairsLoop;
				}

			map[x + y * w] = Tile.stairsDown.id;
			count++;
			if (count == 2) break;
		}

		return new byte[][] { map, data };
	}

	public static void main(String[] args) {
		int[] d = { 0 };
		int[] size = { 128, 128 };

		JComboBox<String> mapComboBox = new JComboBox<>(new String[] { "Top Map", "Undergroup Map", "Sky Map" });
		JComboBox<String> mapSizeComboBox = new JComboBox<>(new String[] { "64x64", "128x128", "256x256","512x512" });
		mapSizeComboBox.setSelectedIndex(1);

		Supplier<BufferedImage> generateMap = () -> {
			BufferedImage img = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_RGB);
			byte[] map = switch (mapComboBox.getSelectedIndex()) {
				case 1 -> LevelGen.createAndValidateUndergroundMap(size[0], size[1], (d[0]++ % 3) + 1)[0];
				case 2 -> LevelGen.createAndValidateSkyMap(size[0], size[1])[0];
				default -> LevelGen.createAndValidateTopMap(size[0], size[1])[0];
			};

			int[] pixels = new int[size[0] * size[1]];
			for (int y = 0; y < size[1]; y++) {
				for (int x = 0; x < size[0]; x++) {
					int i = x + y * size[0];

					if (map[i] == Tile.water.id) pixels[i] = 0x000080;
					if (map[i] == Tile.grass.id) pixels[i] = 0x208020;
					if (map[i] == Tile.rock.id) pixels[i] = 0xa0a0a0;
					if (map[i] == Tile.dirt.id) pixels[i] = 0x604040;
					if (map[i] == Tile.sand.id) pixels[i] = 0xa0a040;
					if (map[i] == Tile.tree.id) pixels[i] = 0x003000;
					if (map[i] == Tile.lava.id) pixels[i] = 0xff2020;
					if (map[i] == Tile.cloud.id) pixels[i] = 0xa0a0a0;
					if (map[i] == Tile.stairsDown.id) pixels[i] = 0xffffff;
					if (map[i] == Tile.stairsUp.id) pixels[i] = 0xffffff;
					if (map[i] == Tile.cloudCactus.id) pixels[i] = 0xff00ff;
				}
			}
			img.setRGB(0, 0, size[0], size[1], pixels, 0, size[0]);
			return img;
		};

		JFrame frame = new JFrame("Another");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		ImageIcon image = new ImageIcon(generateMap.get().getScaledInstance(size[0] * 4, size[1] * 4, Image.SCALE_AREA_AVERAGING));
		JLabel label = new JLabel(image);
		contentPanel.add(label, "North");

		JPanel seedPanel = new JPanel();
		seedPanel.setLayout(new BoxLayout(seedPanel, BoxLayout.X_AXIS));

		JCheckBox seedEnabled = new JCheckBox("Seed Enabled");
		seedEnabled.setSelected(false);
		JTextField seedField = new JTextField("0");
		seedField.setEnabled(false);
		seedPanel.add(seedEnabled);
		seedPanel.add(seedField);

		seedEnabled.addActionListener((ev) -> {
			seedField.setEnabled(seedEnabled.isSelected());
		});

		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));

		JButton generateButton = new JButton("Generate");
		generateButton.addActionListener((ev) -> {
			if (seedEnabled.isSelected()) {
				try {
					long seed = Long.parseLong(seedField.getText());
					random.setSeed(seed);
				} catch (Exception ignored) {};
			} else {
				random.setSeed(random.nextLong());
			}

			size[0] = 64 * (mapSizeComboBox.getSelectedIndex() + 1);
			size[1] = size[0];
			int scale = size[0] == 64 ? 8 : size[0] == 128 ? 4 : size[0] == 256 ? 2 : 1;
			image.setImage(generateMap.get().getScaledInstance(size[0] * scale, size[1] * scale, Image.SCALE_AREA_AVERAGING));
			label.updateUI();
		});

		controlsPanel.add(generateButton);
		controlsPanel.add(mapComboBox);
		controlsPanel.add(mapSizeComboBox);

		contentPanel.add(controlsPanel, "Center");
		contentPanel.add(seedPanel, "South");

		frame.add(contentPanel);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
