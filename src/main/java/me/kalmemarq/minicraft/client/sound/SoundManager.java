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

import me.kalmemarq.minicraft.client.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SoundManager {
	protected static final Logger LOGGER = LogManager.getLogger(SoundManager.class);
	private final long deviceHandle;
	private long contextHandle = 0L;
	private ALCapabilities capabilities = null;
	private boolean initialized;
	private final Map<String, Integer> buffers = new HashMap<>();
	private final List<StaticSoundSource> sources = new ArrayList<>();
	private final Client client;

	public SoundManager(Client client) {
		this.client = client;
		this.deviceHandle = ALC10.alcOpenDevice((ByteBuffer) null);

		if (this.deviceHandle == 0L) {
			return;
		}

		ALCCapabilities deviceCapabilities = ALC.createCapabilities(this.deviceHandle);

		LOGGER.info("OpenALC10: {}", deviceCapabilities.OpenALC10);
		LOGGER.info("OpenALC11: {}", deviceCapabilities.OpenALC11);
		LOGGER.info("ALC_EXT_EFX: {}", deviceCapabilities.ALC_EXT_EFX);
		LOGGER.info("ALC_ENUMERATE_ALL_EXT: {}", deviceCapabilities.ALC_ENUMERATE_ALL_EXT);

		this.contextHandle = ALC10.alcCreateContext(this.deviceHandle, (IntBuffer) null);
		ALC10.alcMakeContextCurrent(this.contextHandle);

		this.capabilities = AL.createCapabilities(deviceCapabilities, MemoryUtil::memCallocPointer);

		LOGGER.info("ALC_FREQUENCY     : {}Hz", ALC10.alcGetInteger(this.deviceHandle, ALC10.ALC_FREQUENCY));
		LOGGER.info("ALC_REFRESH       : {}Hz", ALC10.alcGetInteger(this.deviceHandle, ALC10.ALC_REFRESH));
		LOGGER.info("ALC_SYNC          : {}", ALC10.alcGetInteger(this.deviceHandle, ALC10.ALC_SYNC) == ALC10.ALC_TRUE);
		LOGGER.info("ALC_MONO_SOURCES  : {}", ALC10.alcGetInteger(this.deviceHandle, ALC11.ALC_MONO_SOURCES));
		LOGGER.info("ALC_STEREO_SOURCES: {}", ALC10.alcGetInteger(this.deviceHandle, ALC11.ALC_STEREO_SOURCES));

		this.initialized = true;
	}

	public void play(String filepath, float volume, float pitch) {
		if (!this.client.sound) {
			return;
		}

		if (!this.buffers.containsKey(filepath)) {
			int buffer = AL11.alGenBuffers();
			try {
				if (filepath.endsWith(".wav")) {
					WavFile.loadAndSetBufferData(buffer, filepath);
				} else {
					OggFile.loadAndSetBufferData(buffer, filepath);
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
			this.buffers.put(filepath, buffer);
		}
		StaticSoundSource source = new StaticSoundSource();
		source.setBuffer(this.buffers.get(filepath));
		source.setVolume(Math.clamp(volume, 0.1f, 10.0f));
		source.setPitch(Math.clamp(pitch, 0.1f, 10.0f));
		source.play();
		this.sources.add(source);
	}

	public void pauseAll() {
		for (StaticSoundSource source : this.sources) {
			source.pause();
		}
	}

	public void resumeAll() {
		for (StaticSoundSource source : this.sources) {
			source.resume();
		}
	}

	public void tick() {
		Iterator<StaticSoundSource> iter = this.sources.iterator();
		while (iter.hasNext()) {
			StaticSoundSource source = iter.next();
			if (source.isStopped()) {
				source.close();
				iter.remove();
			}
		}
	}

	public void close() {
		if (!this.initialized) {
			return;
		}

		LOGGER.info("Clearing sound sources and buffers");
		for (StaticSoundSource source : this.sources) {
			source.close();
		}
		this.sources.clear();

		for (int buffer : this.buffers.values()) {
			AL11.alDeleteBuffers(buffer);
		}
		this.buffers.clear();

		LOGGER.info("Destroying sound context");

		ALC10.alcMakeContextCurrent(0L);
		MemoryUtil.memFree(this.capabilities.getAddressBuffer());
		ALC10.alcDestroyContext(this.contextHandle);

		LOGGER.info("Closing audio device");
		ALC10.alcCloseDevice(this.deviceHandle);
	}

	public static void checkALError() {
		int err = AL10.alGetError();
		if (err != AL10.AL_NO_ERROR) {
			throw new RuntimeException(AL11.alGetString(err));
		}
	}
}
