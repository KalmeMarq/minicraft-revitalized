package me.kalmemarq.minicraft.sound;

import com.mojang.ld22.sound.Sound;
import me.kalmemarq.minicraft.IOUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public final class WavFile {
	private WavFile() {
	}

	public static void loadAndSetBufferData(int buffer, String filepath) throws IOException, UnsupportedAudioFileException {
		AudioInputStream inputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(Sound.class.getResource(filepath)));
		AudioFormat audioFormat = inputStream.getFormat();

		int format = AL10.AL_FALSE;
		if (audioFormat.getSampleSizeInBits() == 8) {
			format = audioFormat.getChannels() == 1 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_STEREO8;
		} else if (audioFormat.getSampleSizeInBits() == 16) {
			format = audioFormat.getChannels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
		}

		ByteBuffer data = IOUtils.readInputStreamToByteBuffer(inputStream);
		if (data != null) {
			AL11.alBufferData(buffer, format, data, (int) audioFormat.getSampleRate());
			MemoryUtil.memFree(data);
		}
	}

	public static class WavAudioInputStream {
		public ByteBuffer read(int size) {
			return null;
		}
	}
}
