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
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class AtlasTexture extends Texture {
    private final Map<String, SpriteInfo> spriteInfoMap = new HashMap<>();

    public void load(String directory) {
        this.handle = GL11.glGenTextures();
        this.bind();
        this.setFilter(FilterMode.NEAREST);
        this.setWrap(WrapMode.REPEAT);
        this.width = 256;
        this.height = 256;

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        List<SpriteTexture> spriteTextures = new ArrayList<>();
        try {
            List<Path> paths = Files.walk(IOUtils.getResourcesPath().resolve(directory)).filter(Files::isRegularFile).toList();
            Slot slot = new Slot(0, 0, this.width, this.height);

            for (Path path : paths) {
                SpriteTexture spriteTexture = new SpriteTexture(
                        IOUtils.getResourcesPath().resolve(directory).relativize(path).toString().replaceAll("\\\\", "/"),
                        StbNativeImage.read(Files.newInputStream(path))
                );
                spriteTextures.add(spriteTexture);
            }

            spriteTextures.sort((a, b) -> Integer.compare(b.width * b.height, a.width * a.height));

            for (SpriteTexture texture : spriteTextures) {
                slot.fit(texture);
            }

            slot.gather((texture, pos) -> {
                SpriteInfo sprite = new SpriteInfo(pos[0], pos[1], texture.width, texture.height, pos[0] / (float) this.width, pos[1] / (float) this.height, (pos[0] + texture.width) / (float) this.width,  (pos[1] + texture.height) / (float) this.height);
                this.spriteInfoMap.put(texture.path, sprite);
                texture.data.loadToTextureAsSub(pos[0], pos[1], sprite.width, sprite.height);
            });

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Cleaning up sprite textures");
            spriteTextures.forEach(SpriteTexture::close);
        }
    }

    public SpriteInfo getSpriteInfo(String path) {
        return this.spriteInfoMap.get(path);
    }

    public record SpriteTexture(String path, int width, int height, StbNativeImage data) {
        public SpriteTexture(String path, StbNativeImage data) {
            this(path, data.getWidth(), data.getHeight(), data);
        }

        public void close() {
            this.data.close();
        }
    }

    public record SpriteInfo(int u, int v, int width, int height, float minU, float minV, float maxU, float maxV) {
    }

    public static class Slot {
        public final int x;
        public final int y;
        public final int width;
        public final int height;
        public List<Slot> subSlots;
        private SpriteTexture texture;

        public Slot(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void gather(BiConsumer<SpriteTexture, int[]> consumer) {
            if (this.texture != null) {
                consumer.accept(this.texture, new int[] { this.x, this.y });
            } else if (this.subSlots != null) {
                for (Slot lv : this.subSlots) {
                    lv.gather(consumer);
                }
            }
        }

        public boolean fit(SpriteTexture texture) {
            if (this.texture != null) {
                return false;
            }

            int textureWidth = texture.width;
            int textureHeight = texture.height;

            if (textureWidth > this.width || textureHeight > this.height) {
                return false;
            }

            if (textureWidth == this.width && textureHeight == this.height) {
                this.texture = texture;
                return true;
            }

            if (this.subSlots == null) {
                this.subSlots = new ArrayList<>(1);
                this.subSlots.add(new Slot(this.x, this.y, textureWidth, textureHeight));

                int remainingWidth = this.width - textureWidth;
                int remainingHeight = this.height - textureHeight;

                if (remainingHeight > 0 && remainingWidth > 0) {
                    if (Math.max(this.height, remainingWidth) >= Math.max(this.width, remainingHeight)) {
                        this.subSlots.add(new Slot(this.x, this.y + textureHeight, textureWidth, remainingHeight));
                        this.subSlots.add(new Slot(this.x + textureWidth, this.y, remainingWidth, this.height));
                    } else {
                        this.subSlots.add(new Slot(this.x + textureWidth, this.y, remainingWidth, textureHeight));
                        this.subSlots.add(new Slot(this.x, this.y + textureHeight, this.width, remainingHeight));
                    }
                } else if (remainingWidth == 0) {
                    this.subSlots.add(new Slot(this.x, this.y + textureHeight, textureWidth, remainingHeight));
                } else if (remainingHeight == 0) {
                    this.subSlots.add(new Slot(this.x + textureWidth, this.y, remainingWidth, textureHeight));
                }
            }

            for (Slot slot : this.subSlots) {
                if (!slot.fit(texture)) continue;
                return true;
            }
            return false;
        }
    }
}
