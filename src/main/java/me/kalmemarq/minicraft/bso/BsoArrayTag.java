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

package me.kalmemarq.minicraft.bso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BsoArrayTag implements BsoTag, Iterable<BsoTag> {
	private final List<BsoTag> list;
	private int type;

	public BsoArrayTag() {
		this.list = new ArrayList<>();
	}

	public int getType() {
		return this.type;
	}

	public BsoArrayTag(List<BsoTag> list, int type) {
		this.list = list;
		this.type = type;
	}

	@Override
	public int getId() {
		return 0xA;
	}

	@Override
	public int getAdditionalData() {
		if (this.list.size() <= (Byte.MAX_VALUE * 2) + 1) {
			return 0x2;
		} else if (this.list.size() <= (Short.MAX_VALUE * 2) + 1) {
			return 0x1;
		}
		return 0x0;
	}

	public void clear() {
		this.list.clear();
	}

	public int size() {
		return this.list.size();
	}

	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	public void add(BsoTag tag) {
		if (this.type == 0) {
			this.type = tag.getId();
		} else if (this.type != tag.getId()) {
			throw new UnsupportedOperationException("Trying to add tag " + tag.getId() + " to list of tag " + this.type);
		}

		this.list.add(tag);
	}

	public BsoTag get(int index) {
		return this.list.get(index);
	}

	@NotNull
	@Override
	public Iterator<BsoTag> iterator() {
		return this.list.iterator();
	}
}
