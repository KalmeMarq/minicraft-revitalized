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

import me.kalmemarq.minicraft.client.util.IOUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class StbNativeImage extends AbstractNativeImage {
    public StbNativeImage(int width, int height, long pointer) {
        this(width, height, PixelFormat.RGBA, pointer);
    }

    public StbNativeImage(int width, int height, PixelFormat format, long pointer) {
        super(width, height, format);
        this.pointer = pointer;
    }

    public static StbNativeImage read(InputStream inputStream) {
        ByteBuffer buffer = IOUtils.readInputStreamToByteBuffer(inputStream);

        StbNativeImage image = null;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer wP = stack.mallocInt(1);
            IntBuffer hP = stack.mallocInt(1);
            IntBuffer cP = stack.mallocInt(1);

            ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(buffer, wP, hP, cP, 4);

            if (imageBuffer != null) {
                int width = wP.get(0);
                int height = hP.get(0);

                image = new StbNativeImage(width, height, MemoryUtil.memAddress(imageBuffer));
            } else {
                System.out.println("no image u stupid ass mothafucka");
            }
        }

        MemoryUtil.memFree(buffer);

        return image;
    }

    @Override
    public void close() {
        if (this.pointer != 0L) {
            STBImage.nstbi_image_free(this.pointer);
            this.pointer = 0L;
        }
    }
}
