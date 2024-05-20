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

	public LevelGen(Random random, int w, int h, int featureSize) {
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

	public static void main(String[] args) {
		int[] d = { 0, 0 };
		long[] s = { 0 };
		int[] size = { 128, 128 };

		JComboBox<String> mapComboBox = new JComboBox<>(new String[] { "Top Map", "Undergroup Map", "Sky Map" });
		JComboBox<String> mapSizeComboBox = new JComboBox<>(new String[] { "64x64", "128x128", "256x256","512x512" });
		JComboBox<String> mapGenComboBox = new JComboBox<>(new String[] { /* "LevelGen", */ "LevelMapGenerator" });
		JTextField seedField = new JTextField("0");

		JPanel seedPanel = new JPanel();
		seedPanel.setLayout(new BoxLayout(seedPanel, BoxLayout.X_AXIS));
		seedField.setEnabled(false);
		JCheckBox seedEnabled = new JCheckBox("Seed Enabled");
		seedEnabled.setSelected(false);

		seedEnabled.addActionListener((ev) -> {
			seedField.setEnabled(seedEnabled.isSelected());
		});
		seedPanel.add(seedEnabled);
		seedPanel.add(seedField);
		seedPanel.add(mapGenComboBox);
		mapSizeComboBox.setSelectedIndex(1);

		Supplier<BufferedImage> generateMap = () -> {
			BufferedImage img = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_RGB);
			byte[] map = switch (mapComboBox.getSelectedIndex()) {
				case 1 -> /* mapGenComboBox.getSelectedIndex() == 1 ? LevelGen.createAndValidateUndergroundMap(size[0], size[1], (d[0]++ % 3) + 1)[0] :*/ new UndergroundMapGenerator(size[0], size[1], (d[0]++ % 3) + 1, s[0]).generateAndValidate()[0];
				case 2 -> /* mapGenComboBox.getSelectedIndex() == 2 ? LevelGen.createAndValidateSkyMap(size[0], size[1])[0] :*/ new SkyMapGenerator(size[0], size[1], s[0]).generateAndValidate()[0];
				default -> /* mapGenComboBox.getSelectedIndex() == 0 ? LevelGen.createAndValidateTopMap(size[0], size[1])[0] :*/ new TopMapGenerator(size[0], size[1], s[0]).generateAndValidate()[0];
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

		JPanel controlsPanel = new JPanel();
		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.X_AXIS));

		JButton generateButton = new JButton("Generate");
		generateButton.addActionListener((ev) -> {
			if (seedEnabled.isSelected()) {
				try {
					s[0] = Long.parseLong(seedField.getText());
				} catch (Exception ignored) {}
			} else {
				s[0] = random.nextLong();
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
