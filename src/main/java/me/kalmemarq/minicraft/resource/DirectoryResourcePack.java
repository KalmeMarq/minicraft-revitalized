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

package me.kalmemarq.minicraft.resource;

import me.kalmemarq.minicraft.client.util.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DirectoryResourcePack implements ResourcePack {
    private final Path root;

    public DirectoryResourcePack(Path path) {
        this.root = path;
    }

    @Override
    public boolean has(String filePath) {
        return Files.exists(this.root.resolve(filePath));
    }

    @Override
    public ResourceSupplier get(String filePath) {
        return () -> Files.newInputStream(this.root.resolve(filePath));
    }

    @Override
    public List<ResourceSupplier> list(String dirPath, Predicate<String> filter) {
        List<ResourceSupplier> files = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(IOUtils.getResourcesPath().resolve(dirPath))) {
            for (Iterator<Path> it = paths.iterator(); it.hasNext(); ) {
                Path path = it.next();
                if (!Files.isDirectory(path) && filter.test(path.toString())) {
                    files.add(() -> Files.newInputStream(path));
                }
            }

        } catch (IOException e) {
            return files;
        }

        return files;
    }
}
