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
import org.lwjgl.openal.AL11;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public final class OggFile {
	public static void loadAndSetBufferData(int buffer, String filepath) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			ByteBuffer data = IOUtils.readInputStreamToByteBuffer(SoundManager.class.getResourceAsStream(filepath));

			if (data != null) {
				IntBuffer channels = stack.mallocInt(1);
				IntBuffer sampleRate = stack.mallocInt(1);

				ShortBuffer audioData = STBVorbis.stb_vorbis_decode_memory(data, channels, sampleRate);
				if (audioData != null) {
					AL11.alBufferData(buffer, channels.get(0) == 1 ? AL11.AL_FORMAT_MONO16 : AL11.AL_FORMAT_STEREO16, audioData, sampleRate.get(0));
				}
				MemoryUtil.memFree(data);
			}
		}
	}
}
