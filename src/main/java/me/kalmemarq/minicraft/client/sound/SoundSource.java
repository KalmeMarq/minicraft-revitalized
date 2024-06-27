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

import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryStack;

public abstract class SoundSource {
	protected int handle;

	public SoundSource() {
		SoundManager.sourcesHandles++;
		this.handle = AL10.alGenSources();
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
			SoundManager.sourcesHandles--;
			this.handle = -1;
		}
	}
}
