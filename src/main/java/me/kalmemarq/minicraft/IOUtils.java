package me.kalmemarq.minicraft;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.ld22.crafting.Crafting;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;

public final class IOUtils {
	public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();
	private static Path resourcesPath;

	private IOUtils() {
	}

	public static Path getResourcesPath() {
		if (resourcesPath == null) {
			Path path;
			URI uri;

			try {
				uri = Objects.requireNonNull(Crafting.class.getResource("/icons")).toURI();
			}  catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}

			try {
				path = Path.of(uri).getParent();
			} catch (FileSystemNotFoundException e) {
				try {
					FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
					path = fileSystem.getPath("/icons").getParent();
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
			}
			resourcesPath = path;
		}

		return resourcesPath;
	}

	public static ByteBuffer readInputStreamToByteBuffer(InputStream inputStream) {
		ByteBuffer buffer = null;

		try (ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream)) {
			buffer = MemoryUtil.memAlloc(8196);

			while (true) {
				int bytes = readableByteChannel.read(buffer);
				if (bytes == -1) {
					break;
				}
				if (buffer.remaining() == 0) {
					buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() * 3 / 2);
				}
			}
		} catch (IOException e) {
			MemoryUtil.memFree(buffer);
			return null;
		}

		buffer.flip();
		return MemoryUtil.memSlice(buffer);
	}
}
