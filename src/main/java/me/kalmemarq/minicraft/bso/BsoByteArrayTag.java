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

import java.util.Arrays;

public class BsoByteArrayTag implements BsoTag {
	private byte[] values;

	public BsoByteArrayTag(byte[] values) {
		this.values = values;
	}

	@Override
	public int getId() {
		return 0x0A;
	}

	@Override
	public int getAdditionalData() {
		if (this.values.length <= (Byte.MAX_VALUE * 2) + 1) {
			return 0x2;
		} else if (this.values.length <= (Short.MAX_VALUE * 2) + 1) {
			return 0x1;
		}
		return 0x0;
	}

	public void clear() {
		this.values = new byte[0];
	}

	public byte[] getArray() {
		return this.values;
	}

	public int size() {
		return this.values.length;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.values);
	}
}
