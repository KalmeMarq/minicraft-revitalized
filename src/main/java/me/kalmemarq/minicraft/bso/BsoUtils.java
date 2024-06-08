package me.kalmemarq.minicraft.bso;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class BsoUtils {
	public static void write(Path path, BsoTag tag) {
		try (DataOutputStream outputStream = new DataOutputStream(Files.newOutputStream(path))) {
			outputStream.writeByte(tag.getId() | tag.getAdditionalData() << 4);
			write(outputStream, tag, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void write(DataOutputStream outputStream, BsoTag tag, boolean allowsAdditionalData) throws IOException {
		switch (tag) {
			case BsoByteTag byteTag -> outputStream.writeByte(byteTag.value());
			case BsoShortTag shortTag -> {
				if (!allowsAdditionalData || shortTag.getAdditionalData() == 0x0) {
					outputStream.writeShort(shortTag.value());
				} else {
					outputStream.writeByte((byte) (shortTag.value() & 0xFF));
				}
			}
			case BsoIntTag intTag -> {
				int ad = intTag.getAdditionalData();
				if (!allowsAdditionalData || ad == 0x0) outputStream.writeInt(intTag.value());
				else if (ad == 0x1) outputStream.writeShort((short) (intTag.value() & 0xFFFF));
				else outputStream.writeByte((byte) (intTag.value() & 0xFF));
			}
			case BsoLongTag longTag -> {
				int ad = longTag.getAdditionalData();
				if (!allowsAdditionalData || ad == 0x0) outputStream.writeLong(longTag.value());
				else if (ad == 0x1) outputStream.writeInt((int) (longTag.value()));
				else if (ad == 0x2) outputStream.writeShort((short) (longTag.value() & 0xFFFFL));
				else outputStream.writeByte((byte) (longTag.value() & 0xFFL));
			}
			case BsoFloatTag floatTag -> outputStream.writeFloat(floatTag.value());
			case BsoDoubleTag doubleTag -> outputStream.writeDouble(doubleTag.value());
			case BsoStringTag stringTag -> outputStream.writeUTF(stringTag.value());
			case BsoMapTag mapTag -> {
				for (var entry : mapTag.entrySet()) {
					BsoTag value = entry.getValue();
					outputStream.writeByte(value.getId() | value.getAdditionalData() << 4);
					outputStream.writeUTF(entry.getKey());
					write(outputStream, value, true);
				}
				outputStream.writeByte(0x0);
			}
			case BsoListTag listTag -> {
				int ad = listTag.getAdditionalData();
				if (ad == 0x2) {
					outputStream.writeByte(listTag.size());
				} else if (ad == 0x1) {
					outputStream.writeShort(listTag.size());
				} else {
					outputStream.writeInt(listTag.size());
				}

				for (BsoTag item : listTag) {
					outputStream.writeByte(item.getId() | item.getAdditionalData() << 4);
					write(outputStream, item, true);
				}
			}
			case BsoArrayTag arrayTag -> {
				int ad = arrayTag.getAdditionalData();
				outputStream.writeByte(arrayTag.getType());
				if (ad == 0x2) {
					outputStream.writeByte(arrayTag.size());
				} else if (ad == 0x1) {
					outputStream.writeShort(arrayTag.size());
				} else {
					outputStream.writeInt(arrayTag.size());
				}

				for (BsoTag item : arrayTag) {
					write(outputStream, item, false);
				}
			}
			case BsoByteArrayTag arrayTag -> {
				int ad = arrayTag.getAdditionalData();
				outputStream.writeByte(0x1);
				if (ad == 0x2) {
					outputStream.writeByte(arrayTag.size());
				} else if (ad == 0x1) {
					outputStream.writeShort(arrayTag.size());
				} else {
					outputStream.writeInt(arrayTag.size());
				}

				for (byte item : arrayTag.getArray()) {
					outputStream.writeByte(item);
				}
			}
			case BsoShortArrayTag arrayTag -> {
				int ad = arrayTag.getAdditionalData();
				outputStream.writeByte(0x2);
				if (ad == 0x2) {
					outputStream.writeByte(arrayTag.size());
				} else if (ad == 0x1) {
					outputStream.writeShort(arrayTag.size());
				} else {
					outputStream.writeInt(arrayTag.size());
				}

				for (short item : arrayTag.getArray()) {
					outputStream.writeShort(item);
				}
			}
			case BsoIntArrayTag arrayTag -> {
				int ad = arrayTag.getAdditionalData();
				outputStream.writeByte(0x3);
				if (ad == 0x2) {
					outputStream.writeByte(arrayTag.size());
				} else if (ad == 0x1) {
					outputStream.writeShort(arrayTag.size());
				} else {
					outputStream.writeInt(arrayTag.size());
				}

				for (int item : arrayTag.getArray()) {
					outputStream.writeInt(item);
				}
			}
			case BsoLongArrayTag arrayTag -> {
				int ad = arrayTag.getAdditionalData();
				outputStream.writeByte(0x4);
				if (ad == 0x2) {
					outputStream.writeByte(arrayTag.size());
				} else if (ad == 0x1) {
					outputStream.writeShort(arrayTag.size());
				} else {
					outputStream.writeInt(arrayTag.size());
				}

				for (long item : arrayTag.getArray()) {
					outputStream.writeLong(item);
				}
			}
			case BsoFloatArrayTag arrayTag -> {
				int ad = arrayTag.getAdditionalData();
				outputStream.writeByte(0x5);
				if (ad == 0x2) {
					outputStream.writeByte(arrayTag.size());
				} else if (ad == 0x1) {
					outputStream.writeShort(arrayTag.size());
				} else {
					outputStream.writeInt(arrayTag.size());
				}

				for (float item : arrayTag.getArray()) {
					outputStream.writeFloat(item);
				}
			}
			case BsoDoubleArrayTag arrayTag -> {
				int ad = arrayTag.getAdditionalData();
				outputStream.writeByte(0x6);
				if (ad == 0x2) {
					outputStream.writeByte(arrayTag.size());
				} else if (ad == 0x1) {
					outputStream.writeShort(arrayTag.size());
				} else {
					outputStream.writeInt(arrayTag.size());
				}

				for (double item : arrayTag.getArray()) {
					outputStream.writeDouble(item);
				}
			}
			default -> throw new RuntimeException("Unknown Bso tag");
		}
	}

	public static BsoTag read(Path path) {
		try (DataInputStream inputStream = new DataInputStream(Files.newInputStream(path))) {
			int id = inputStream.readByte();
			int ad = id >> 4;
			id = id & 0xF;
			return read(inputStream, id, ad);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static BsoTag read(DataInputStream inputStream, int id, int ad) throws IOException {
		return switch (id) {
			case 0x1 -> new BsoByteTag(inputStream.readByte());
			case 0x2 -> ad == 0x0 ? new BsoShortTag(inputStream.readShort()) : new BsoShortTag(inputStream.readByte());
			case 0x3 -> ad == 0x0 ? new BsoIntTag(inputStream.readInt()) : ad == 0x1 ? new BsoIntTag(inputStream.readShort()) : new BsoIntTag(inputStream.readByte());
			case 0x4 -> ad == 0x0 ? new BsoLongTag(inputStream.readLong()) : ad == 0x1 ? new BsoLongTag(inputStream.readInt()) : ad == 0x2 ? new BsoLongTag(inputStream.readShort()) : new BsoLongTag(inputStream.readByte());
			case 0x5 -> new BsoFloatTag(inputStream.readFloat());
			case 0x6 -> new BsoDoubleTag(inputStream.readDouble());
			case 0x7 -> new BsoStringTag(inputStream.readUTF());
			case 0x8 -> {
				BsoMapTag tag = new BsoMapTag();
				int bid;
				while ((id = inputStream.readByte()) != 0x0) {
					int bad = id >> 4;
					bid = id & 0xF;
					String key = inputStream.readUTF();
					BsoTag value = read(inputStream, bid, bad);
					if (value != null) {
						tag.put(key, value);
					}
				}
				yield tag;
			}
			case 0x9 -> {
				BsoListTag tag = new BsoListTag();
				int size = ad == 0x0 ? inputStream.readInt() : ad == 0x1 ? inputStream.readUnsignedShort() : inputStream.readUnsignedByte();

				for (int i = 0; i < size; ++i) {
					int iid = inputStream.readByte();
					BsoTag value = read(inputStream, iid & 0xF, iid >> 4);
					if (value != null) {
						tag.add(value);
					}
				}
				yield tag;
			}
			case 0xA -> {
				int typeOfList = inputStream.readByte();
				int size = ad == 0x0 ? inputStream.readInt() : ad == 0x1 ? inputStream.readUnsignedShort() : inputStream.readUnsignedByte();

				switch (typeOfList) {
					case 0x1 -> {
						byte[] array = new byte[size];
						inputStream.readFully(array);
						yield new BsoByteArrayTag(array);
					}
					case 0x2 -> {
						short[] array = new short[size];
						for (short i = 0; i < size; ++i) {
							array[i] = inputStream.readShort();
						}
						yield new BsoShortArrayTag(array);
					}
					case 0x3 -> {
						int[] array = new int[size];
						for (int i = 0; i < size; ++i) {
							array[i] = inputStream.readInt();
						}
						yield new BsoIntArrayTag(array);
					}
					case 0x4 -> {
						long[] array = new long[size];
						for (int i = 0; i < size; ++i) {
							array[i] = inputStream.readLong();
						}
						yield new BsoLongArrayTag(array);
					}
					case 0x5 -> {
						float[] array = new float[size];
						for (int i = 0; i < size; ++i) {
							array[i] = inputStream.readFloat();
						}
						yield new BsoFloatArrayTag(array);
					}
					case 0x6 -> {
						double[] array = new double[size];
						for (int i = 0; i < size; ++i) {
							array[i] = inputStream.readDouble();
						}
						yield new BsoDoubleArrayTag(array);
					}
					default -> {
						BsoArrayTag tag = new BsoArrayTag();
						for (int i = 0; i < size; ++i) {
							BsoTag value = read(inputStream, typeOfList, 0);
							if (value != null) {
								tag.add(value);
							}
						}
						yield tag;
					}
				}
			}
			default -> null;
		};
	}

	public static String stringify(BsoTag tag) {
		return stringify(tag, 0, 0);
	}

	public static String stringify(BsoTag tag, int indent) {
		return stringify(tag, indent, 0);
	}

	private static String stringify(BsoTag tag, int indent, int level) {
		return switch (tag) {
			case BsoByteTag byteTag -> byteTag.value() + "b";
			case BsoShortTag shortTag -> shortTag.value() + "s";
			case BsoIntTag intTag -> intTag.value() + "i";
			case BsoLongTag longTag -> longTag.value() + "L";
			case BsoFloatTag floatTag -> floatTag.value() + "f";
			case BsoDoubleTag doubleTag -> doubleTag.value() + "D";
			case BsoStringTag stringTag -> "\"" + stringTag.value() + "\"";
			case BsoMapTag mapTag -> {
				StringBuilder builder = new StringBuilder();
				builder.append('{');
				if (indent > 0 && !mapTag.isEmpty()) {
					builder.append('\n');
				}

				int i = 0, size = mapTag.size();
				for (Map.Entry<String, BsoTag> entry : mapTag.entrySet()) {
					if (indent > 0) builder.append(" ".repeat(level * indent + indent));

					builder.append(entry.getKey()).append(':');
					if (indent > 0) builder.append(' ');
					builder.append(stringify(entry.getValue(), indent, level + 1));
					++i;
					if (i < size) builder.append(',');
					if (indent > 0) builder.append('\n');
				}

				if (indent > 0) builder.append(" ".repeat(level * indent));
				builder.append('}');
				yield builder.toString();
			}
			case BsoListTag listTag -> {
				StringBuilder builder = new StringBuilder();
				builder.append('[');
				if (indent > 0 && !listTag.isEmpty()) {
					builder.append('\n');
				}
				int i = 0, size = listTag.size();
				for (BsoTag item : listTag) {
					if (indent > 0) builder.append(" ".repeat(level * indent + indent));
					builder.append(stringify(item, indent, level + 1));
					++i;
					if (i < size) builder.append(',');
					if (indent > 0) builder.append('\n');
				}
				if (indent > 0 && !listTag.isEmpty()) builder.append(" ".repeat(level * indent));
				builder.append(']');
				yield builder.toString();
			}
			case BsoArrayTag arrayTag -> {
				StringBuilder builder = new StringBuilder();
				builder.append('[');
				if (indent > 0 && !arrayTag.isEmpty()) {
					builder.append('\n');
				}
				int i = 0, size = arrayTag.size();
				for (BsoTag item : arrayTag) {
					if (indent > 0) builder.append(" ".repeat(level * indent + indent));
					builder.append(stringify(item, indent, level + 1));
					++i;
					if (i < size) builder.append(',');
					if (indent > 0) builder.append('\n');
				}
				if (indent > 0 && !arrayTag.isEmpty()) builder.append(" ".repeat(level * indent));
				builder.append(']');
				yield builder.toString();
			}
			case BsoByteArrayTag arrayTag -> {
				StringBuilder builder = new StringBuilder();
				builder.append("[b;");
				int i = 0, size = arrayTag.size();
				for (byte item : arrayTag.getArray()) {
					builder.append(item);
					++i;
					if (i < size) builder.append(',').append(' ');
				}
				builder.append(']');
				yield builder.toString();
			}
			case BsoShortArrayTag arrayTag -> {
				StringBuilder builder = new StringBuilder();
				builder.append("[s;");
				int i = 0, size = arrayTag.size();
				for (short item : arrayTag.getArray()) {
					builder.append(item);
					++i;
					if (i < size) builder.append(',').append(' ');
				}
				builder.append(']');
				yield builder.toString();
			}
			case BsoIntArrayTag arrayTag -> {
				StringBuilder builder = new StringBuilder();
				builder.append("[i;");
				int i = 0, size = arrayTag.size();
				for (int item : arrayTag.getArray()) {
					builder.append(item);
					++i;
					if (i < size) builder.append(',').append(' ');
				}
				builder.append(']');
				yield builder.toString();
			}
			case BsoLongArrayTag arrayTag -> {
				StringBuilder builder = new StringBuilder();
				builder.append("[L;");
				int i = 0, size = arrayTag.size();
				for (long item : arrayTag.getArray()) {
					builder.append(item);
					++i;
					if (i < size) builder.append(',').append(' ');
				}
				builder.append(']');
				yield builder.toString();
			}
			case BsoFloatArrayTag arrayTag -> {
				StringBuilder builder = new StringBuilder();
				builder.append("[f;");
				int i = 0, size = arrayTag.size();
				for (float item : arrayTag.getArray()) {
					builder.append(item);
					++i;
					if (i < size) builder.append(',').append(' ');
				}
				builder.append(']');
				yield builder.toString();
			}
			case BsoDoubleArrayTag arrayTag -> {
				StringBuilder builder = new StringBuilder();
				builder.append("[D;");
				int i = 0, size = arrayTag.size();
				for (double item : arrayTag.getArray()) {
					builder.append(item);
					++i;
					if (i < size) builder.append(',').append(' ');
				}
				builder.append(']');
				yield builder.toString();
			}
			default -> throw new RuntimeException("Unknown tag");
		};
	}

	public static void main(String[] args) {
	}
}
