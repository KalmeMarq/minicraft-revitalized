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

package me.kalmemarq.minicraft.client.texture;

import org.lwjgl.opengl.GL11;

import java.io.InputStream;

public class SimpleTexture extends Texture {
    public void load(InputStream inputStream) {
        this.handle = GL11.glGenTextures();
        this.bind();
        this.setFilter(FilterMode.NEAREST);
        this.setWrap(WrapMode.REPEAT);

        StbNativeImage image = StbNativeImage.read(inputStream);

        this.width = image.getWidth();
        this.height = image.getHeight();
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image.getPointer());

        image.close();
    }

    public void load(int width, int height, int internalFormat, long buffer, int pixelFormat) {
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, width, height, 0, pixelFormat, GL11.GL_UNSIGNED_BYTE, buffer);
    }
}
