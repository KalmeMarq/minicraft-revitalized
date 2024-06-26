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

import me.kalmemarq.minicraft.client.Client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    public Map<String, AtlasTexture> atlasTextures = new HashMap<>();
    public Map<String, Texture> textures = new HashMap<>();
    private final Client client;

    public TextureManager(Client client) {
        this.client = client;
    }

    public void addBuiltinTextures() {
        // White texture (For drawing colored rectangles)
        NativeImage im = new NativeImage(1, 1);
        im.fill((x, y, w, h) -> 0xFF_FFFFFF);

        SimpleTexture texture = new SimpleTexture();
        texture.bind();
        texture.setFilter(Texture.FilterMode.NEAREST);
        texture.setWrap(Texture.WrapMode.REPEAT);
        im.loadToTexture(texture);

        this.textures.put("White", texture);

        // Missing texture
        NativeImage im1 = new NativeImage(2, 2);
        im1.fill((x, y, w, h) -> x == y ? 0xFF000000 : 0xFFFF00FF);

        SimpleTexture texture1 = new SimpleTexture();
        texture1.bind();
        texture1.setFilter(Texture.FilterMode.NEAREST);
        texture1.setWrap(Texture.WrapMode.REPEAT);
        im1.loadToTexture(texture1);

        this.textures.put("Missing", texture1);

        Path debugPath = this.client.saveDir.resolve("debug");
        try {
            Files.createDirectories(debugPath);
            im.saveTo(debugPath.resolve("debug-White.png"));
            im1.saveTo(debugPath.resolve("debug-Missing.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        im.close();
        im1.close();
    }

    public void addAtlases() {
        AtlasTexture tilesAtlas = new AtlasTexture();
        tilesAtlas.load("textures/tiles");

        AtlasTexture itemsAtlas = new AtlasTexture();
        itemsAtlas.load("textures/items");

        AtlasTexture entitiesAtlas = new AtlasTexture();
        entitiesAtlas.load("textures/entities");

        AtlasTexture uiAtlas = new AtlasTexture();
        uiAtlas.load("textures/ui");

        this.textures.put("textures/tiles", tilesAtlas);
        this.textures.put("textures/items", itemsAtlas);
        this.textures.put("textures/entities", entitiesAtlas);
        this.textures.put("textures/ui", uiAtlas);

        this.atlasTextures.put("textures/tiles", tilesAtlas);
        this.atlasTextures.put("textures/items", itemsAtlas);
        this.atlasTextures.put("textures/entities", entitiesAtlas);
        this.atlasTextures.put("textures/ui", uiAtlas);

        Path debugPath = this.client.saveDir.resolve("debug");
        try {
            Files.createDirectories(debugPath);
            tilesAtlas.saveTo(debugPath.resolve("debug-tiles-atlas.png"));
            itemsAtlas.saveTo(debugPath.resolve("debug-items-atlas.png"));
            entitiesAtlas.saveTo(debugPath.resolve("debug-entities-atlas.png"));
            uiAtlas.saveTo(debugPath.resolve("debug-ui-atlas.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AtlasTexture getAtlas(String path) {
        return this.atlasTextures.get(path);
    }

    public Texture getTexture(String path) {
        return this.textures.get(path);
    }

    public void bind(String path) {
        Texture texture = this.textures.get(path);
        if (texture == null) {
            InputStream i = TextureManager.class.getResourceAsStream(path);
            if (i != null) {
                texture = new SimpleTexture();
                ((SimpleTexture) texture).load(i);
                this.textures.put(path, texture);
            } else {
                this.textures.put(path, this.textures.get("Missing"));
            }
        }
        texture.bind();
    }

    public void close() {
        this.textures.values().forEach(Texture::close);
        this.textures.clear();
    }
}
