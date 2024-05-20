package me.kalmemarq.minicraft.bso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BsoListTag implements BsoTag, Iterable<BsoTag> {
	private final List<BsoTag> list = new ArrayList<>();

	@Override
	public int getId() {
		return 0x9;
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
		this.list.add(tag);
	}

	public BsoTag get(int index) {
		return this.list.get(index);
	}

	@Override
	public Iterator<BsoTag> iterator() {
		return this.list.iterator();
	}
}
