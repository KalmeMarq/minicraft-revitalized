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

package me.kalmemarq.minicraft.client.menu;

import me.kalmemarq.minicraft.client.util.StringUtils;
import me.kalmemarq.minicraft.client.texture.TextureManager;
import org.lwjgl.opengl.GL11;

public class Font {
    private final String chars = StringUtils.readAllLines(Font.class.getResourceAsStream("/font.txt")).replaceAll("\n", "");

    private final TextureManager textureManager;

    public Font(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void drawOutlined(String message, int x, int y, int color) {
        this.drawOutlined(message, x, y, color, 0);
    }

    public void drawOutlined(String message, int x, int y, int color, int linePadding) {
        this.textureManager.bind("/font.png");
        GL11.glBegin(GL11.GL_QUADS);

        this.draw(message, x - 1, y, 0x000000, linePadding, TextAlignment.LEFT, false);
        this.draw(message, x + 1, y, 0x000000, linePadding, TextAlignment.LEFT, false);
        this.draw(message, x, y - 1, 0x000000, linePadding, TextAlignment.LEFT, false);
        this.draw(message, x, y + 1, 0x000000, linePadding, TextAlignment.LEFT, false);
        this.draw(message, x, y, color, linePadding, TextAlignment.LEFT, false);

        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void draw(String message, int x, int y, int color) {
        this.draw(message, x, y, color, 0, TextAlignment.LEFT);
    }

    public void draw(String message, int x, int y, int color, int linePadding) {
        this.draw(message, x, y, color, linePadding, TextAlignment.LEFT);
    }

    public void draw(String message, int x, int y, int color, int linePadding, TextAlignment alignment) {
        this.draw(message, x, y, color, linePadding, alignment, true);
    }

    public void draw(String message, int x, int y, int color, int linePadding, TextAlignment alignment, boolean doBeginEnd) {
        if (doBeginEnd) {
            this.textureManager.bind("/font.png");
            GL11.glBegin(GL11.GL_QUADS);
        }

        float r = (color >> 16 & 0xFF) / 255.0f;
        float g = (color >> 8 & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        GL11.glColor4f(r, g, b, 1.0f);

        if (alignment != TextAlignment.LEFT) {
            int width = -1;
            int lineWidth = 0;

            for (int i = 0, line = 0, il = 0; i < message.length(); i++, il++) {
                char chr = message.charAt(i);
                if (chr == '\n') {
                    il = -1;
                    line++;
                    width = Math.max(lineWidth, width);
                    continue;
                }
                lineWidth++;
            }
            width = Math.max(lineWidth, width) * 8;

            if (alignment == TextAlignment.CENTER) {
                x -= width / 2;
            } else if (alignment == TextAlignment.RIGHT) {
                x -= width;
            }
        }

        for (int i = 0, line = 0, il = 0; i < message.length(); i++, il++) {
            char chr = message.charAt(i);
            if (chr == '\n') {
                il = -1;
                line++;
                continue;
            }
            int ix = this.chars.indexOf(chr);
            if (ix >= 0) {
                int rx = x + il * 8;
                int ry = y + (line) * (8 + linePadding);

                int u = (ix % 32) * 8;
                int v = (ix / 32) * 8;

                float u0 = u / 256.0f;
                float v0 = v / 256.0f;
                float u1 = (u + 8) / 256.0f;
                float v1 = (v + 8) / 256.0f;

                GL11.glTexCoord2f(u0, v0);
                GL11.glVertex3f(rx, ry, 0);
                GL11.glTexCoord2f(u0, v1);
                GL11.glVertex3f(rx, ry + 8, 0);
                GL11.glTexCoord2f(u1, v1);
                GL11.glVertex3f(rx + 8, ry + 8, 0);
                GL11.glTexCoord2f(u1, v0);
                GL11.glVertex3f(rx + 8, ry, 0);
            }
        }

        if (doBeginEnd) {
            GL11.glEnd();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    // TODO: Fix this shit up. I don't want to calculate stuff and use vectors
    public void drawWithMaxWidth(String msg,  int x, int y, int col, int maxCharactersPerLine) {
        this.textureManager.bind("/font.png");
        float r = (col >> 16 & 0xFF) / 255.0f;
        float g = (col >> 8 & 0xFF) / 255.0f;
        float b = (col & 0xFF) / 255.0f;
        GL11.glColor4f(r, g, b, 1.0f);
        GL11.glBegin(GL11.GL_QUADS);

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

            int ix = this.chars.indexOf(msg.charAt(i));

            if (ix >= 0) {
                int rx = x + il * 8;
                int ry = y + line * 8;

                int u = (ix % 32) * 8;
                int v = (ix / 32) * 8;

                float u0 = u / 256.0f;
                float v0 = v / 256.0f;
                float u1 = (u + 8) / 256.0f;
                float v1 = (v + 8) / 256.0f;

                GL11.glTexCoord2f(u0, v0);
                GL11.glVertex3f(rx, ry, 0);
                GL11.glTexCoord2f(u0, v1);
                GL11.glVertex3f(rx, ry + 8, 0);
                GL11.glTexCoord2f(u1, v1);
                GL11.glVertex3f(rx + 8, ry + 8, 0);
                GL11.glTexCoord2f(u1, v0);
                GL11.glVertex3f(rx + 8, ry, 0);
            }
            il++;
        }

        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }
}
