package me.kalmemarq.minicraft.resource;

import me.kalmemarq.minicraft.IOUtils;

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
