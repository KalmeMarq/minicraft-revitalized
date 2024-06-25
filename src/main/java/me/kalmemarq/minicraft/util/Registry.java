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

package me.kalmemarq.minicraft.util;

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

    public List<T> getAll() {
        return this.list;
    }

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

