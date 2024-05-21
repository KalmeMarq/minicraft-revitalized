package me.kalmemarq.minicraft;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class Registry<T> {
	private final ObjectList<T> list = new ObjectArrayList<>();
	private final Reference2IntMap<T> valueToNumericId = new Reference2IntOpenHashMap<>();
	private final Map<String, T> stringIdToValue = new HashMap<>();
	private final Map<T, String> valueToStringId = new IdentityHashMap<>();

	public T register(String stringId, T item) {
		this.stringIdToValue.put(stringId, item);
		this.valueToStringId.put(item, stringId);
		this.valueToNumericId.put(item, this.valueToNumericId.size());
		this.list.add(item);
		return item;
	}

	public T getByStringId(String id) {
		return this.stringIdToValue.get(id);
	}

	public T getByNumericId(int id) {
		return this.list.get(id);
	}

	public String getStringId(T item) {
		return this.valueToStringId.get(item);
	}

	public int getNumericId(T item) {
		return this.valueToNumericId.getInt(item);
	}
}
