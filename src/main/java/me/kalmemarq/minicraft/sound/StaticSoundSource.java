package me.kalmemarq.minicraft.sound;

import org.lwjgl.openal.AL10;

public class StaticSoundSource extends SoundSource {
	public void setBuffer(int buffer) {
		AL10.alSourcei(this.handle, AL10.AL_BUFFER, buffer);
	}
}
