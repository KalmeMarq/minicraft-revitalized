package com.mojang.ld22.gfx;

public class Font {
	private static final String chars =
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ012345" +
			"6789.,!?'\"-+=/\\%()<>:;^@ÁÉÍÓÚÑ¿¡" +
			"ÃÊÇÔÕĞÇÜİÖŞÆØÅŰŐ[]#|{}_АБВГДЕЁЖЗ" +
			"ИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯÀÂÄÈÎÌÏÒ" +
			"ÙÛÝ*«»£$&€§ªºabcdefghijklmnopqrs" +
			"tuvwxyzáàãâäéèêëíìîïóòõôöúùûüçñý" +
			"ÿабвгдеёжзийклмнопрстуфхцчшщъыьэ" +
			"юяışő";

	public static void draw(String msg, Screen screen, int x, int y, int col) {
		for (int i = 0, line = 0, il = 0; i < msg.length(); i++, il++) {
			char chr = msg.charAt(i);
			if (chr == '\n') {
				il = 0;
				line++;
				continue;
			}
			int ix = chars.indexOf(chr);
			if (ix >= 0) {
				screen.render(x + il * 8, y + line * 8, ix + 24 * 32, col, 0);
			}
		}
	}

	// TODO: Fix the max cpl not really being accurate
	public static void drawWithMaxWidth(String msg, Screen screen, int x, int y, int col, int maxCharactersPerLine) {
		int line = 0;

		for (int i = 0, il = 0, ls = 0, nsp = -1; i < msg.length(); i++) {
			if (msg.charAt(i) == '\n') {
				il = 0;
				line++;
				ls = i + 1;
				continue;
			}

			if (msg.charAt(i) == ' ') {
				il++;
				continue;
			}

			if (il == 0 || nsp == -1 || nsp <= i) {
				nsp = msg.indexOf(' ', i);
			}

			if (nsp - ls >= maxCharactersPerLine) {
				il = 0;
				line++;
				ls = i + 1;
				i--;
				continue;
			}

			int ix = chars.indexOf(msg.charAt(i));

			if (ix >= 0) {
				screen.render(x + il * 8, y + line * 8, ix + 24 * 32, col, 0);
			}
			il++;
		}
	}

	public static void renderFrame(Screen screen, String title, int x0, int y0, int x1, int y1) {
		for (int y = y0; y <= y1; y++) {
			for (int x = x0; x <= x1; x++) {
				if (x == x0 && y == y0)
					screen.render(x * 8, y * 8, 13 * 32, Color.get(-1, 1, 5, 445), 0);
				else if (x == x1 && y == y0)
					screen.render(x * 8, y * 8, 13 * 32, Color.get(-1, 1, 5, 445), 1);
				else if (x == x0 && y == y1)
					screen.render(x * 8, y * 8, 13 * 32, Color.get(-1, 1, 5, 445), 2);
				else if (x == x1 && y == y1)
					screen.render(x * 8, y * 8, 13 * 32, Color.get(-1, 1, 5, 445), 3);
				else if (y == y0)
					screen.render(x * 8, y * 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
				else if (y == y1)
					screen.render(x * 8, y * 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 2);
				else if (x == x0)
					screen.render(x * 8, y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 0);
				else if (x == x1)
					screen.render(x * 8, y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 1);
				else
					screen.render(x * 8, y * 8, 2 + 13 * 32, Color.get(5, 5, 5, 5), 1);
			}
		}

		draw(title, screen, x0 * 8 + 8, y0 * 8, Color.get(5, 5, 5, 550));
	}
}
