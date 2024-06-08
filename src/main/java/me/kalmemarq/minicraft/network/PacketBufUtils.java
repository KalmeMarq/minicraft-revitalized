package me.kalmemarq.minicraft.network;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class PacketBufUtils {
	private PacketBufUtils() {
	}

	public static void writeString(ByteBuf buffer, String value) {
		writeString(buffer, value, StandardCharsets.UTF_8);
	}

	public static void writeString(ByteBuf buffer, String value, Charset charset) {
		byte[] data = value.getBytes(charset);
		writeSignedVarInt(data.length, buffer);
		buffer.writeBytes(data);
	}

	public static String readString(ByteBuf buffer) {
		return readString(buffer, StandardCharsets.UTF_8);
	}

	public static String readString(ByteBuf buffer, Charset charset) {
		int len = readSignedVarInt(buffer);
		String value = buffer.toString(buffer.readerIndex(), len, charset);
		buffer.readerIndex(buffer.readerIndex() + len);
		return value;
	}

	// https://github.com/apache/mahout/blob/trunk/engine/hdfs/src/main/java/org/apache/mahout/math/Varint.java#L52
	public static void writeSignedVarLong(long value, ByteBuf out) {
		writeUnsignedVarLong((value << 1) ^ (value >> 63), out);
	}

	// https://github.com/apache/mahout/blob/trunk/engine/hdfs/src/main/java/org/apache/mahout/math/Varint.java#L68
	public static void writeUnsignedVarLong(long value, ByteBuf out) {
		while ((value & 0xFFFFFFFFFFFFFF80L) != 0L) {
			out.writeByte(((int) value & 0x7F) | 0x80);
			value >>>= 7;
		}
		out.writeByte((int) value & 0x7F);
	}

	// https://github.com/apache/mahout/blob/trunk/engine/hdfs/src/main/java/org/apache/mahout/math/Varint.java#L79
	public static void writeSignedVarInt(int value, ByteBuf out) {
		writeUnsignedVarInt((value << 1) ^ (value >> 31), out);
	}

	// https://github.com/apache/mahout/blob/trunk/engine/hdfs/src/main/java/org/apache/mahout/math/Varint.java#L87
	public static void writeUnsignedVarInt(int value, ByteBuf out) {
		while ((value & 0xFFFFFF80) != 0L) {
			out.writeByte((value & 0x7F) | 0x80);
			value >>>= 7;
		}
		out.writeByte(value & 0x7F);
	}

	// https://github.com/apache/mahout/blob/trunk/engine/hdfs/src/main/java/org/apache/mahout/math/Varint.java#L103
	public static long readSignedVarLong(ByteBuf in) {
		long raw = readUnsignedVarLong(in);
		long temp = (((raw << 63) >> 63) ^ raw) >> 1;
		return temp ^ (raw & (1L << 63));
	}

	// https://github.com/apache/mahout/blob/trunk/engine/hdfs/src/main/java/org/apache/mahout/math/Varint.java#L121
	public static long readUnsignedVarLong(ByteBuf in) {
		long value = 0L;
		int i = 0;
		long b;
		while (((b = in.readByte()) & 0x80L) != 0) {
			value |= (b & 0x7F) << i;
			i += 7;
			if (i > 63) throw new RuntimeException("Variable length quantity is too long (must be <= 63): " + i);
		}
		return value | (b << i);
	}

	// https://github.com/apache/mahout/blob/trunk/engine/hdfs/src/main/java/org/apache/mahout/math/Varint.java#L139
	public static int readSignedVarInt(ByteBuf in) {
		int raw = readUnsignedVarInt(in);
		int temp = (((raw << 31) >> 31) ^ raw) >> 1;
		return temp ^ (raw & (1 << 31));
	}

	// https://github.com/apache/mahout/blob/trunk/engine/hdfs/src/main/java/org/apache/mahout/math/Varint.java#L155
	public static int readUnsignedVarInt(ByteBuf in) {
		int value = 0;
		int i = 0;
		int b;
		while (((b = in.readByte()) & 0x80) != 0) {
			value |= (b & 0x7F) << i;
			i += 7;
			if (i > 35) throw new RuntimeException("Variable length quantity is too long (must be <= 35): " + i);
		}
		return value | (b << i);
	}

	public static int getVarIntLength(int value) {
		for (int j = 1; j < 5; ++j) {
			if ((value & -1 << j * 7) != 0) continue;
			return j;
		}
		return 5;
	}
}
