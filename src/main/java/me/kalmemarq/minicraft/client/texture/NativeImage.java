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

import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;

public class NativeImage extends AbstractNativeImage {
    public NativeImage(int width, int height) {
        this(width, height, PixelFormat.RGBA);
    }

    public NativeImage(int width, int height, PixelFormat format) {
        super(width, height, format);
        this.pointer = MemoryUtil.nmemAlloc(this.getWidth() * this.getHeight() * (long) format.channelCount);
    }

    public static NativeImage read(Texture texture) {
        NativeImage image = new NativeImage(texture.getWidth(), texture.getHeight());
        image.loadFromTexture(texture);
        return image;
    }

    public void loadFromTexture(Texture texture) {
        GL45.glGetTextureImage(texture.getHandle(), 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, this.getWidth() * this.getHeight() * 4, this.pointer);
    }

    @Override
    public void close() {
        if (this.pointer != 0L) {
            MemoryUtil.nmemFree(this.pointer);
            this.pointer = 0L;
        }
    }
}
