package me.kalmemarq.minicraft.render;

import me.kalmemarq.minicraft.StringUtils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ShaderProgram {
	private static final Pattern INCLUDE_PATTERN = Pattern.compile("#include\s+\"(?<file>[a-zA-Z0-9.]+)\"");

	private static int currentProgram = -1;

	private final int id;
	private final Map<String, Integer> uniformLocations = new HashMap<>();

	public ShaderProgram(String name) {
		String vertexSource = StringUtils.readAllLines(ShaderProgram.class.getResourceAsStream("/shaders/" + name + ".vsh"));
		String fragmentSource = StringUtils.readAllLines(ShaderProgram.class.getResourceAsStream("/shaders/" + name + ".fsh"));

		this.id = GL33.glCreateProgram();

		int vertex = GL33.glCreateShader(GL33.GL_VERTEX_SHADER);
		GL33.glShaderSource(vertex, vertexSource);
		GL33.glCompileShader(vertex);

		if (GL33.glGetShaderi(vertex, GL33.GL_COMPILE_STATUS) == GL33.GL_FALSE) {
			throw new RuntimeException("Could not compile vertex shader: " + GL33.glGetShaderInfoLog(vertex));
		}

		int fragment = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);
		GL33.glShaderSource(fragment, process(fixFragmentSourceCode(fragmentSource)));
		GL33.glCompileShader(fragment);

		if (GL33.glGetShaderi(fragment, GL33.GL_COMPILE_STATUS) == GL33.GL_FALSE) {
			throw new RuntimeException("Could not compile fragment shader: " + GL33.glGetShaderInfoLog(fragment));
		}

		GL33.glAttachShader(this.id, vertex);
		GL33.glAttachShader(this.id, fragment);

		GL33.glLinkProgram(this.id);

		if (GL33.glGetProgrami(this.id, GL33.GL_LINK_STATUS) == GL33.GL_FALSE) {
			throw new RuntimeException("Could not link shader program: " + GL33.glGetProgramInfoLog(this.id));
		}
	}

	// It is what it is
	private static String fixFragmentSourceCode(String source) {
		return source.replaceFirst("vec4\\s+main\\s*\\(", "out vec4 outputColor;\nvec4 main_func(") + "\n" + "void main() { outputColor = main_func(); }";
	}

	private static String process(String source) {
		StringBuilder builder = new StringBuilder();

		var matcher = INCLUDE_PATTERN.matcher(source);
		while (matcher.find()) {
			String file = matcher.group("file");
			matcher.appendReplacement(builder, StringUtils.readAllLines(ShaderProgram.class.getResourceAsStream("/shaders/" + file)));
		}

		matcher.appendTail(builder);
		return builder.toString();
	}

	public int getUniformLocation(String name) {
		return this.uniformLocations.computeIfAbsent(name, (key) -> GL33.glGetUniformLocation(this.id, name));
	}

	public void setUniform(String name, Matrix4f matrix) {
		int location = this.getUniformLocation(name);

		if (location != -1) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				FloatBuffer buffer = stack.mallocFloat(16);
				GL33.glUniformMatrix4fv(location, false, matrix.get(buffer));
			}
		}
	}

	public void setUniform(String name, boolean... values) {
		int location = this.getUniformLocation(name);

		if (location != -1) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer buffer = stack.mallocInt(values.length);

				for (int i = 0; i < values.length; ++i) {
					buffer.put(i, values[i] ? 1 : 0);
				}

				switch (values.length) {
					case 1 -> GL33.glUniform1iv(location, buffer);
					case 2 -> GL33.glUniform2iv(location, buffer);
					case 3 -> GL33.glUniform3iv(location, buffer);
					case 4 -> GL33.glUniform4iv(location, buffer);
				}
			}
		}
	}

	public void setUniform(String name, int... values) {
		int location = this.getUniformLocation(name);

		if (location != -1) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer buffer = stack.ints(values);

				switch (values.length) {
					case 1 -> GL33.glUniform1iv(location, buffer);
					case 2 -> GL33.glUniform2iv(location, buffer);
					case 3 -> GL33.glUniform3iv(location, buffer);
					case 4 -> GL33.glUniform4iv(location, buffer);
				}
			}
		}
	}

	public void setUniform(String name, float... values) {
		int location = this.getUniformLocation(name);

		if (location != -1) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				FloatBuffer buffer = stack.floats(values);

				switch (values.length) {
					case 1 -> GL33.glUniform1fv(location, buffer);
					case 2 -> GL33.glUniform2fv(location, buffer);
					case 3 -> GL33.glUniform3fv(location, buffer);
					case 4 -> GL33.glUniform4fv(location, buffer);
				}
			}
		}
	}

	public void bind() {
		if (currentProgram != this.id) {
			GL33.glUseProgram(this.id);
			currentProgram = this.id;
		}
	}

	public void close() {
		GL33.glDeleteProgram(this.id);
		currentProgram = -1;
	}
}
