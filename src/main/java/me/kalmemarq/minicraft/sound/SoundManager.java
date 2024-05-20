package me.kalmemarq.minicraft.sound;

import com.mojang.ld22.Game;
import com.mojang.ld22.sound.Sound;
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

import java.io.IOException;
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

	private final Map<String, Integer> buffers = new HashMap<>();
	private final List<StaticSoundSource> sources = new ArrayList<>();
	private final List<StreamSoundSource> streamSources = new ArrayList<>();

	private boolean initialized;

	public SoundManager() {
		this.deviceHandle = Game.USE_OPENAL ? ALC10.alcOpenDevice((ByteBuffer) null) : 0L;

		if (this.deviceHandle == 0L) {
			if (!Game.USE_OPENAL) LOGGER.info("Using javax.sound instead of OpenAL");
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

	public void play(Sound sound) {
		if (!this.initialized) {
			sound.play();
			return;
		}
		this.play(sound.getPath(), 1.0f, 1.0f);
	}

	public void playStream(String filepath, float volume, float pitch) {
		if (filepath.endsWith(".wav")) {
			LOGGER.error("Streaming not supported for WAV files");
			return;
		}
		OggFile.OggAudioInputStream inputStream;
		try {
			inputStream = new OggFile.OggAudioInputStream(SoundManager.class.getResourceAsStream(filepath));
		} catch (IOException e) {
			LOGGER.error(e);
			return;
		}
		StreamSoundSource source = new StreamSoundSource();
		source.setStream(inputStream);
		source.setVolume(Math.clamp(volume, 0.1f, 10.0f));
		source.setPitch(Math.clamp(pitch, 0.1f, 10.0f));
		source.play();
		this.streamSources.add(source);
	}

	public void play(String filepath, float volume, float pitch) {
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

		for (StreamSoundSource source : this.streamSources) {
			source.pause();
		}
	}

	public void resumeAll() {
		for (StaticSoundSource source : this.sources) {
			source.resume();
		}

		for (StreamSoundSource source : this.streamSources) {
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

		Iterator<StreamSoundSource> iterS = this.streamSources.iterator();
		while (iterS.hasNext()) {
			StreamSoundSource source = iterS.next();
			source.tick();
			if (source.isStopped()) {
				source.close();
				iterS.remove();
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

		for (StreamSoundSource source : this.streamSources) {
			source.close();
		}
		this.streamSources.clear();

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
