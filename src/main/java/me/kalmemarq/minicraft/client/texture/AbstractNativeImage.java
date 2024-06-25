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
import org.lwjgl.opengl.GL45;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class AbstractNativeImage implements AutoCloseable {
    protected long pointer;

    private final PixelFormat pixelFormat;
    private final int width;
    private final int height;

    public AbstractNativeImage(int width, int height) {
        this(width, height, PixelFormat.RGBA);
    }

    public AbstractNativeImage(int width, int height, PixelFormat format) {
        this.width = Math.max(width, 1);
        this.height = Math.max(height, 1);
        this.pixelFormat = format;
    }

    public long getPointer() {
        return this.pointer;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public PixelFormat getPixelFormat() {
        return this.pixelFormat;
    }

    public void loadToTexture(SimpleTexture texture) {
        texture.load(this.width, this.height, GL33.GL_RGBA, this.pointer, GL33.GL_RGBA);
    }

    public void loadToTextureAsSub(int offsetX, int offsetY, int width, int height) {
        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, offsetX, offsetY, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.pointer);
    }

    public void flip(Mirroring mirroring) {
        long lineWidth =  (long) this.width * this.pixelFormat.channelCount;

        if (mirroring == Mirroring.VERTICAL || mirroring == Mirroring.BOTH) {
            long tempBuffer = MemoryUtil.nmemAlloc(lineWidth);

            try {
                for (int y = 0; y < this.height / 2; ++y) {
                    long linePointer = this.pointer + (lineWidth * y);
                    long oppositeLinePointer = this.pointer + (lineWidth * (this.height - y - 1));
                    MemoryUtil.memCopy(linePointer, tempBuffer, lineWidth);
                    MemoryUtil.memCopy(oppositeLinePointer, linePointer, lineWidth);
                    MemoryUtil.memCopy(tempBuffer, oppositeLinePointer, lineWidth);
                }
            } finally {
                MemoryUtil.nmemFree(tempBuffer);
            }
        }

        if (mirroring == Mirroring.HORIZONTAL || mirroring == Mirroring.BOTH) {
            byte[] swap = { 0, 0, 0 };
            for (int y = 0; y < this.height; ++y) {
                long linePointer = this.pointer + (lineWidth * y);

                long lo = linePointer;
                long hi = linePointer + lineWidth - 1;

                while (lo < hi) {
                    swap[0] = MemoryUtil.memGetByte(lo);
                    swap[1] = MemoryUtil.memGetByte(lo + 1);
                    swap[2] = MemoryUtil.memGetByte(lo + 2);

                    MemoryUtil.memPutByte(lo, MemoryUtil.memGetByte(hi - 2));
                    MemoryUtil.memPutByte(lo + 1, MemoryUtil.memGetByte(hi - 1));
                    MemoryUtil.memPutByte(lo + 2, MemoryUtil.memGetByte(hi));

                    MemoryUtil.memPutByte(hi - 2, swap[0]);
                    MemoryUtil.memPutByte(hi - 1, swap[1]);
                    MemoryUtil.memPutByte(hi, swap[2]);

                    lo += 3;
                    hi -= 3;
                }
            }
        }
    }

    public int getPixelAt(int x, int y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            return 0;
        }

        if (this.pixelFormat == PixelFormat.RGBA) {
            return MemoryUtil.memGetInt(this.pointer + ((long) y * (long) this.width + (long) x) * (long) this.pixelFormat.channelCount);
        }

        long pixelPointer = this.pointer + ((long) y * (long) this.width + (long) x) * 3L;
        int red = MemoryUtil.memGetByte(pixelPointer);
        int green = MemoryUtil.memGetByte(pixelPointer + 1);
        int blue = MemoryUtil.memGetByte(pixelPointer + 2);
        return red << 16 | green << 8 | blue;
    }

    public void setPixelAt(int x, int y, int color) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            return;
        }

        if (this.pixelFormat == PixelFormat.RGBA) {
            MemoryUtil.memPutInt(this.pointer + ((long) y * (long) this.width + (long) x) * ((long) this.pixelFormat.channelCount), color);
            return;
        }

        // What in the bloody hell is going on?

        byte red = (byte) ((color >> 16) & 0xFF);
        byte green = (byte) ((color >> 8) & 0xFF);
        byte blue = (byte) (color & 0xFF);

        long pixelPointer = this.pointer + ((long) y * (long) this.width + (long) x) * 3L;
        MemoryUtil.memPutByte(pixelPointer, red);
        MemoryUtil.memPutByte(pixelPointer + 1, green);
        MemoryUtil.memPutByte(pixelPointer + 2, blue);
    }

    public void fill(ColorSupplier colorSupplier) {
        for (int y = 0; y < this.height; ++y) {
            for (int x = 0; x < this.width; ++x) {
                this.setPixelAt(x, y, colorSupplier.get(x, y, this.width, this.height));
            }
        }
    }

    public interface ColorSupplier {
        int get(int x, int y, int width, int height);
    }

    public void saveTo(Path path) {
        try (SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            STBWriteCallback callback = new STBWriteCallback(channel);

            try {
                if (STBImageWrite.nstbi_write_png_to_func(callback.address(), 0L, this.width, this.height, this.pixelFormat.channelCount, this.pointer, 0) == 0) {
                    return;
                }

                if (callback.getException() != null) {
                    System.out.println(callback.getException().getMessage());
                }
            } finally {
                callback.free();
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    abstract public void close();

    static class STBWriteCallback extends STBIWriteCallback {
        private final WritableByteChannel channel;
        private IOException exception;

        public STBWriteCallback(WritableByteChannel channel) {
            this.channel = channel;
        }

        @Override
        public void invoke(long context, long data, int size) {
            ByteBuffer buffer = STBIWriteCallback.getData(data, size);
            try {
                this.channel.write(buffer);
            } catch (IOException exception) {
                this.exception = exception;
            }
        }

        public IOException getException() {
            return this.exception;
        }
    }

    public enum Mirroring {
        HORIZONTAL,
        VERTICAL,
        BOTH;
    }

    public enum PixelFormat {
        RGB(3, GL33.GL_RGB),
        RGBA(4, GL33.GL_RGBA);

        public final int channelCount;
        public final int glEnum;

        PixelFormat(int channelCount, int glEnum) {
            this.channelCount = channelCount;
            this.glEnum = glEnum;
        }
    }
}
