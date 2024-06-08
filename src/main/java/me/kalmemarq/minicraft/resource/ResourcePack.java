package me.kalmemarq.minicraft.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;

public interface ResourcePack {
	boolean has(String filePath);
	ResourceSupplier get(String filePath);
	List<ResourceSupplier> list(String dirPath, Predicate<String> filter);

	interface ResourceSupplier {
		InputStream get() throws IOException;
		default BufferedReader getReader() throws IOException {
			return new BufferedReader(new InputStreamReader(this.get(), StandardCharsets.UTF_8));
		}
	}
}
