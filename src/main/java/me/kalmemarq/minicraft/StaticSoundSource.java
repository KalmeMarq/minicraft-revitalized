package me.kalmemarq.minicraft;

import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryStack;

public class StaticSoundSource {
	private int handle;

	public StaticSoundSource() {
		this.handle = AL10.alGenSources();
	}

	public void setBuffer(int buffer) {
		AL10.alSourcei(this.handle, AL10.AL_BUFFER, buffer);
	}

	public void play() {
		AL10.alSourcePlay(this.handle);
	}

	public void pause() {
		if (this.isPlaying()) {
			AL10.alSourcePause(this.handle);
		}
	}

	public void resume() {
		if (AL10.alGetSourcei(this.handle, AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED) {
			AL10.alSourcePlay(this.handle);
		}
	}

	public void stop() {
		AL10.alSourceStop(this.handle);
	}

	public boolean isPlaying() {
		return AL10.alGetSourcei(this.handle, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public boolean isStopped() {
		return AL10.alGetSourcei(this.handle, AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED;
	}

	public void setPosition(float x, float y, float z) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			AL10.alSourcefv(this.handle, AL10.AL_POSITION, stack.floats(x, y, z));
		}
	}

	public void setVolume(float volume) {
		AL10.alSourcef(this.handle, AL10.AL_GAIN, volume);
	}

	public void setPitch(float volume) {
		AL10.alSourcef(this.handle, AL10.AL_PITCH, volume);
	}

	public void close() {
		if (this.handle != -1) {
			AL10.alSourceStop(this.handle);
			AL10.alDeleteSources(this.handle);
			this.handle = -1;
		}
	}
}
