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
import org.lwjgl.opengl.GL33;

import java.nio.file.Path;

public class Texture {
    protected int handle = -1;
    protected int width;
    protected int height;

    public int getHandle() {
        return this.handle;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setFilter(FilterMode mode) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, mode.glEnum);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, mode.glEnum);
    }

    public void setWrap(WrapMode mode) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, mode.glEnum);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, mode.glEnum);
    }

    public void bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.handle);
    }

    public void bind(int unit) {
        GL33.glActiveTexture(unit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.handle);
    }

    public void saveTo(Path path) {
        NativeImage nativeImage = NativeImage.read(this);
        nativeImage.saveTo(path);
        nativeImage.close();
    }

    public void close() {
        if (this.handle != -1) {
            GL33.glDeleteTextures(this.handle);
            this.handle = -1;
        }
    }

    public enum FilterMode {
        NEAREST(GL33.GL_NEAREST),
        LINEAR(GL33.GL_LINEAR);

        public final int glEnum;

        FilterMode(int glEnum) {
            this.glEnum = glEnum;
        }
    }

    public enum WrapMode {
        REPEAT(GL33.GL_REPEAT),
        CLAMP_TO_EDGE(GL33.GL_CLAMP_TO_EDGE);

        public final int glEnum;

        WrapMode(int glEnum) {
            this.glEnum = glEnum;
        }
    }
}
