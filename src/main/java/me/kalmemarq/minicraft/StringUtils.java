package me.kalmemarq.minicraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class StringUtils {
	private StringUtils() {
	}

	public static String readAllLines(InputStream inputStream) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			StringBuilder builder = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append('\n');
			}

			builder.deleteCharAt(builder.lastIndexOf("\n"));
			return builder.toString();
		} catch (IOException ignored) {}

		return "";
	}
}
