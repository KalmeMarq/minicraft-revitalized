package me.kalmemarq.minicraft;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public final class IOUtils {
	public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

	private IOUtils() {
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
