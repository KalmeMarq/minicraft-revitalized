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

package me.kalmemarq.minicraft.client.sound;

import me.kalmemarq.minicraft.client.util.IOUtils;
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
	public static void loadAndSetBufferData(int buffer, String filepath) throws IOException, UnsupportedAudioFileException {
		AudioInputStream inputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(WavFile.class.getResource(filepath)));
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
}
