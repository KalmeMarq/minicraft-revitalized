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

package me.kalmemarq.minicraft.level.entity;

import me.kalmemarq.minicraft.bso.BsoMapTag;
import me.kalmemarq.minicraft.level.Level;

public class ZombieEntity extends MobEntity {
    private int randomWalkTime = 0;

    public ZombieEntity(Level level) {
        super(level);
        this.data.put("walkDist", 0);
        this.dataDirty = true;
    }

	@Override
	public void write(BsoMapTag map) {
		super.write(map);
		map.put("randomWalkTime", this.randomWalkTime);
	}

	@Override
    public void tickAi() {
        super.tickAi();

        int speed = this.tickTime & 1;
        if (!this.move(this.xa * speed, this.ya * speed) || this.random.nextInt(200) == 0) {
            this.randomWalkTime = 60;
            this.xa = (this.random.nextInt(3) - 1) * this.random.nextInt(2);
            this.ya = (this.random.nextInt(3) - 1) * this.random.nextInt(2);
        }

        if (this.randomWalkTime > 0) this.randomWalkTime--;
        this.data.put("walkDist", this.walkDist);
        this.dataDirty = true;
    }
}
