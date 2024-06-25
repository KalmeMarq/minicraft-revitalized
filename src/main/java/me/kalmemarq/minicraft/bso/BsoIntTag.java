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

public class BsoIntTag implements BsoTag {
	private final int value;

	public BsoIntTag(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

	@Override
	public int getId() {
		return 0x3;
	}

	@Override
	public int getAdditionalData() {
		return this.value < Byte.MIN_VALUE || this.value > Byte.MAX_VALUE ? this.value < Short.MIN_VALUE || this.value > Short.MAX_VALUE ? 0x0 : 0x1 : 0x2;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof BsoIntTag other) return other.value == this.value;
		return false;
	}

	@Override
	public int hashCode() {
		return this.value;
	}
}
