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

package me.kalmemarq.minicraft.client.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;

public final class IOUtils {
    public static final ObjectMapper YAML_OBJECT_MAPPER = new YAMLMapper();
    public static final ObjectMapper TOML_OBJECT_MAPPER = new TomlMapper();
    public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

    private IOUtils() {
    }

    public static ByteBuffer readInputStreamToByteBuffer(InputStream inputStream) {
        ByteBuffer buffer = null;
        try (ReadableByteChannel channel = Channels.newChannel(inputStream)) {
            buffer = MemoryUtil.memAlloc(channel instanceof SeekableByteChannel seekableByteChannel ? (int) seekableByteChannel.size() + 1 : 8192);
            while (true) {
                int bytes = channel.read(buffer);
                if (bytes == -1) break;
                if (buffer.remaining() == 0) {
                    buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() * 2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (buffer != null) buffer.flip();
        return MemoryUtil.memSlice(buffer);
    }

    private static Path resourcesPath;

    public static Path getResourcesPath() {
        if (resourcesPath == null) {
            Path path;
            URI uri;

            try {
                uri = Objects.requireNonNull(IOUtils.class.getResource("/langs")).toURI();
            }  catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            try {
                path = Path.of(uri).getParent();
            } catch (FileSystemNotFoundException e) {
                try {
                    FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                    path = fileSystem.getPath("/textures/tiles").getParent();
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }
            resourcesPath = path;
        }

        return resourcesPath;
    }
}
