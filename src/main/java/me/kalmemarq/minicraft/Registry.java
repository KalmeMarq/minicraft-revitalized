package me.kalmemarq.minicraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class Registry<T> {
	private final List<T> list = new ArrayList<>();
	private final Map<T, Integer> valueToNumericId = new IdentityHashMap<>();
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
		return this.valueToNumericId.get(item);
	}
}
