package me.kalmemarq.minicraft.bso;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BsoMapTag implements BsoTag {
	private final Map<String, BsoTag> map = new LinkedHashMap<>();

	@Override
	public int getId() {
		return 0x8;
	}

	public void clear() {
		this.map.clear();
	}

	public int size() {
		return this.map.size();
	}

	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	public Set<String> keySet() {
		return this.map.keySet();
	}

	public Collection<BsoTag> values() {
		return this.map.values();
	}

	public Set<Map.Entry<String, BsoTag>> entrySet() {
		return this.map.entrySet();
	}

	public boolean containsKey(String key) {
		return this.map.containsKey(key);
	}

	public boolean containsKeyOfType(String key, int id) {
		BsoTag tag = this.map.get(key);
		return tag != null && tag.getId() == id;
	}

	public void put(String key, BsoTag value) {
		this.map.put(key, value);
	}

	public void put(String key, byte value) {
		this.map.put(key, new BsoByteTag(value));
	}

	public void put(String key, boolean value) {
		this.map.put(key, new BsoByteTag((byte) (value ? 1 : 0)));
	}

	public void put(String key, short value) {
		this.map.put(key, new BsoShortTag(value));
	}

	public void put(String key, int value) {
		this.map.put(key, new BsoIntTag(value));
	}

	public void put(String key, long value) {
		this.map.put(key, new BsoLongTag(value));
	}

	public void put(String key, float value) {
		this.map.put(key, new BsoFloatTag(value));
	}

	public void put(String key, double value) {
		this.map.put(key, new BsoDoubleTag(value));
	}

	public void put(String key, String value) {
		this.map.put(key, new BsoStringTag(value));
	}

	public void put(String key, byte[] values) {
		this.map.put(key, new BsoByteArrayTag(values));
	}

	public void put(String key, short[] values) {
		this.map.put(key, new BsoShortArrayTag(values));
	}

	public void put(String key, int[] values) {
		this.map.put(key, new BsoIntArrayTag(values));
	}

	public void put(String key, long[] values) {
		this.map.put(key, new BsoLongArrayTag(values));
	}

	public void put(String key, float[] values) {
		this.map.put(key, new BsoFloatArrayTag(values));
	}

	public void put(String key, double[] values) {
		this.map.put(key, new BsoDoubleArrayTag(values));
	}

	public byte getByte(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoByteTag byteTag) {
			return byteTag.value();
		}
		return 0;
	}

	public boolean getBoolean(String key) {
		return this.getByte(key) != 0;
	}

	public short getShort(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoShortTag shortTag) {
			return shortTag.value();
		}
		return 0;
	}

	public int getInt(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoIntTag intTag) {
			return intTag.value();
		}
		return 0;
	}

	public long getLong(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoLongTag longTag) {
			return longTag.value();
		}
		return 0;
	}

	public float getFloat(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoFloatTag floatTag) {
			return floatTag.value();
		}
		return 0;
	}

	public double getDouble(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoDoubleTag doubleTag) {
			return doubleTag.value();
		}
		return 0;
	}

	public String getString(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoStringTag stringTag) {
			return stringTag.value();
		}
		return "";
	}

	public BsoMapTag getMap(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoMapTag mapTag) {
			return mapTag;
		}
		return new BsoMapTag();
	}

	public BsoListTag getList(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoListTag listTag) {
			return listTag;
		}
		return new BsoListTag();
	}

	public BsoArrayTag getArray(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoArrayTag arrayTag) {
			return arrayTag;
		}
		return new BsoArrayTag();
	}

	public byte[] getByteArray(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoByteArrayTag arrayTag) {
			return arrayTag.getArray();
		}
		return new byte[0];
	}

	public short[] getShortArray(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoShortArrayTag arrayTag) {
			return arrayTag.getArray();
		}
		return new short[0];
	}

	public int[] getIntArray(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoIntArrayTag arrayTag) {
			return arrayTag.getArray();
		}
		return new int[0];
	}

	public long[] getLongArray(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoLongArrayTag arrayTag) {
			return arrayTag.getArray();
		}
		return new long[0];
	}

	public float[] getFloatArray(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoFloatArrayTag arrayTag) {
			return arrayTag.getArray();
		}
		return new float[0];
	}

	public double[] getDoubleArray(String key) {
		BsoTag tag = this.map.get(key);
		if (tag instanceof BsoDoubleArrayTag arrayTag) {
			return arrayTag.getArray();
		}
		return new double[0];
	}

	public BsoTag remove(String key) {
		return this.map.remove(key);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BsoMapTag bsoMap)) {
			return false;
		}

		return Objects.equals(this.map, bsoMap.map);
	}

	@Override
	public int hashCode() {
		return this.map.hashCode();
	}
}
