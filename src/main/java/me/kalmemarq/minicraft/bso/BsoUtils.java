package me.kalmemarq.minicraft.bso;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
				for (BsoTag item : listTag) {
					outputStream.writeByte(item.getId() | item.getAdditionalData() << 4);
					write(outputStream, item, true);
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
				int bid;
				while ((id = inputStream.readByte()) != 0x0) {
					int bad = id >> 4;
					bid = id & 0xF;
					BsoTag value = read(inputStream, bid, bad);
					if (value != null) {
						tag.add(value);
					}
				}
				yield tag;
			}
			default -> null;
		};
	}

	public static String stringify(BsoTag tag) {
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
				int i = 0, size = mapTag.size();
				for (Map.Entry<String, BsoTag> entry : mapTag.entrySet()) {
					builder.append(entry.getKey()).append(':').append(stringify(entry.getValue()));
					++i;
					if (i < size) builder.append(',');
				}
				builder.append('}');
				yield builder.toString();
			}
			case BsoListTag listTag -> {
				StringBuilder builder = new StringBuilder();
				builder.append('[');
				int i = 0, size = listTag.size();
				for (BsoTag item : listTag) {
					builder.append(stringify(item));
					++i;
					if (i < size) builder.append(',');
				}
				builder.append(']');
				yield builder.toString();
			}
			default -> throw new RuntimeException("Unknown tag");
		};
	}

	public static void main(String[] args) {
//		BsoUtils.write(Path.of("bso0.dat"), new BsoByteTag((byte) 1));
//		BsoUtils.write(Path.of("bso1.dat"), new BsoShortTag((short) 128));
//		BsoUtils.write(Path.of("bso2.dat"), new BsoIntTag(1));
//		BsoUtils.write(Path.of("bso3.dat"), new BsoLongTag(1));
//		BsoUtils.write(Path.of("bso4.dat"), new BsoStringTag("Hey"));
//
//		{
//			BsoMapTag tag = new BsoMapTag();
//			tag.put("name", "Kal");
//			tag.put("age", (byte) 19);
//			BsoUtils.write(Path.of("bso5.dat"), tag);
//		}
//
//		System.out.println(((BsoByteTag) BsoUtils.read(Path.of("bso0.dat"))).value());
//		System.out.println(((BsoShortTag) BsoUtils.read(Path.of("bso1.dat"))).value());
//		System.out.println(((BsoIntTag) BsoUtils.read(Path.of("bso2.dat"))).value());
//		System.out.println(((BsoLongTag) BsoUtils.read(Path.of("bso3.dat"))).value());
//		System.out.println(((BsoStringTag) BsoUtils.read(Path.of("bso4.dat"))).value());
//
//		{
//			BsoMapTag tag = (BsoMapTag) BsoUtils.read(Path.of("bso5.dat"));
//			System.out.println(tag.getString("name"));
//			System.out.println(tag.getByte("age"));
//		}
		BsoMapTag tag = new BsoMapTag();
		tag.put("name", "Kal");
		tag.put("age", (byte) 13);
		tag.put("pa", (short) 13);
		tag.put("be", 13);
		tag.put("bn", 13L);

		BsoMapTag tag1 = new BsoMapTag();
		tag1.put("name", "Kal");
		tag1.put("age", (byte) 13);
		tag1.put("pa", (short) 13);
		tag1.put("be", 13);
		tag1.put("bn", 13L);

		System.out.println(new BsoByteTag((byte) 1).equals(new BsoByteTag((byte) 1)));
		System.out.println(tag1.equals(tag));
	}
}
