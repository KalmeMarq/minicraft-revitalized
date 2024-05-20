package me.kalmemarq.minicraft.sound;

import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;

public class StreamSoundSource extends SoundSource {
	private OggFile.OggAudioInputStream stream;
	private int bufferSize;

	public void setStream(OggFile.OggAudioInputStream stream) {
		this.stream = stream;
		this.bufferSize = getBufferSize(stream.getFormat());
		this.read(4);
	}

	private void read(int count) {
		if (this.stream != null) {
			try {
				for (int j = 0; j < count; ++j) {
					ByteBuffer buffer = this.stream.read(this.bufferSize);
					if (buffer == null) continue;

					int b = AL10.alGenBuffers();

					int format = AL10.AL_FALSE;
					if (this.stream.getFormat().getSampleSizeInBits() == 8) {
						format = this.stream.getFormat().getChannels() == 1 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_STEREO8;
					} else if (this.stream.getFormat().getSampleSizeInBits() == 16) {
						format = this.stream.getFormat().getChannels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
					}

					AL10.alBufferData(b, format, buffer, (int)this.stream.getFormat().getSampleRate());

					AL10.alSourceQueueBuffers(this.handle, new int[] { b });
					MemoryUtil.memFree(buffer);
				}
			} catch (IOException iOException) {
				SoundManager.LOGGER.error("Failed to read from audio stream", iOException);
			}
		}
	}

	private static int getBufferSize(AudioFormat format) {
		return (int)((float)(format.getSampleSizeInBits()) / 8.0f * (float)format.getChannels() * format.getSampleRate());
	}

	public void tick() {
		if (this.stream != null) {
			int i = this.removeProcessedBuffers();
			this.read(i);
		}
	}

	private int removeProcessedBuffers() {
		int i = AL10.alGetSourcei(this.handle, 4118);
		if (i > 0) {
			int[] is = new int[i];
			AL10.alSourceUnqueueBuffers(this.handle, is);
			SoundManager.checkALError();
			AL10.alDeleteBuffers(is);
			SoundManager.checkALError();
		}
		return i;
	}

	@Override
	public void close() {
		if (this.handle != -1) {
			try {
				this.stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				this.stream = null;
			}
			this.removeProcessedBuffers();
			this.stream = null;
		}

		super.close();
	}
}
