package me.kalmemarq.minicraft.sound;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import me.kalmemarq.minicraft.IOUtils;
import org.lwjgl.openal.AL11;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.rmi.RemoteException;

public final class OggFile {
	private OggFile() {
	}

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

	// Mixing everything ;-;
	public static class OggAudioInputStream {
		private final InputStream inputStream;
		private final Page page = new Page();
		private final SyncState syncState = new SyncState();
		private final StreamState streamState = new StreamState();
		private final Packet packet = new Packet();
		private final Info info = new Info();
		private final DspState dspState = new DspState();
		private final Block block = new Block(this.dspState);
		private final Comment comment = new Comment();
		private AudioFormat format;
		private final float[][][] pcm = new float[1][][];
		private int[] index;

		public OggAudioInputStream(InputStream inputStream) throws IOException {
			this.inputStream = inputStream;
			this.init();
		}

		public AudioFormat getFormat() {
			return this.format;
		}

		public void init() throws IOException {
			this.syncState.init();

			int result = this.getNextPage();
			if (result == -1) {
				throw new IOException("Input does not appear to be an Ogg bitstream.");
			}

			this.streamState.init(this.page.serialno());
			this.info.init();
			this.comment.init();

			if (this.streamState.pagein(this.page) < 0) {
				throw new IOException("Error reading first page of Ogg bitstream data.");
			}

			if (this.streamState.packetout(this.packet) != 1) {
				throw new IOException("Error reading initial header packet.");
			}

			if (this.info.synthesis_headerin(this.comment, this.packet) < 0) {
				throw new IOException("This Ogg bitstream does not contain Vorbis audio data.");
			}

			for (int i = 0; i < 2; ++i) {
				result = this.getNextPacket();
				if (result == -1) {
					throw new IOException("Unexpected end of Ogg stream.");
				}
				if (this.info.synthesis_headerin(this.comment, this.packet) >= 0) continue;
				throw new IOException("Invalid Ogg header packet.");
			}

			this.dspState.synthesis_init(this.info);
			this.block.init(this.dspState);

			this.format = new AudioFormat(this.info.rate, 16, this.info.channels, true, false);
			this.index = new int[this.info.channels];
		}

		public int getNextPage() throws IOException {
			while (true) {
				int state = this.syncState.pageout(this.page);

				switch (state) {
					case 1:
						return 0;
					case 0: {
						int offset = this.syncState.buffer(8192);
						int bytesRead = this.inputStream.read(this.syncState.data, offset, 8192);
						if (bytesRead == -1) {
							return -1;
						}
						this.syncState.wrote(bytesRead);
						break;
					}
					case -1:
						throw new IllegalStateException("Corrupt or missing data in bitstream.");
				}
			}
		}

		public int getNextPacket() throws IOException {
			while (true) {
				int state = this.streamState.packetout(this.packet);

				switch (state) {
					case -1:
						throw new IOException("Failed to parse packet.");
					case 0:
						int result = this.getNextPage();
						if (result == -1) {
							return -1;
						}

						if (this.streamState.pagein(this.page) >= 0) {
							break;
						}

						throw new IOException("Failed to parse page.");
					case 1:
						return 0;
					default:
						throw new IllegalStateException("Unknown packet decode result: " + state);
				}
			}
		}

		private int currentConvBufferSize;

		public ByteBuffer read(int size) throws IOException {
			this.currentConvBufferSize = 0;
			int bufferSize = size + 8192;
			final ByteBuffer buffer = MemoryUtil.memAlloc(bufferSize);

			FloatConsumer consumer = (value) -> {
				if (buffer.remaining() == 0) {
					MemoryUtil.memRealloc(buffer, buffer.capacity() + bufferSize);
				}
				int i = Math.clamp((int)(value * 32767), Short.MIN_VALUE, Short.MAX_VALUE);
				buffer.putShort((short)i);
				this.currentConvBufferSize += 2;
			};

			while (true) {
				int result = this.getNextPacket();
				if (result == -1) break;

				if (!this.decodePacket(consumer) || this.currentConvBufferSize >= size) {
					break;
				}
			}

			buffer.flip();
			return buffer;
		}

		public boolean decodePacket(FloatConsumer consumer) throws IOException {
			if (this.block.synthesis(this.packet) == 0) {
				this.dspState.synthesis_blockin(this.block);
			}

			int samples;
			while ((samples = this.dspState.synthesis_pcmout(this.pcm, this.index)) > 0) {
				float[][] localPcm = this.pcm[0];

				switch (this.info.channels) {
					case 1: {
						int j = this.index[0];
						System.out.println(j);
						while ((long)j < (long)samples + samples) {
							consumer.accept(localPcm[0][j]);
							++j;
						}
						break;
					}
					case 2: {
						int k = 0;
						while ((long)k < samples) {
							consumer.accept(localPcm[0][this.index[0] + k]);
							consumer.accept(localPcm[1][this.index[1] + k]);
							++k;
						}
						break;
					}
					default: {
						throw new RemoteException("Channel not supported");
					}
				}
				this.dspState.synthesis_read(samples);
			}
			return true;
		}

		public void close() throws IOException {
			this.inputStream.close();
		}
	}

	interface FloatConsumer {
		void accept(float value);
	}
}
