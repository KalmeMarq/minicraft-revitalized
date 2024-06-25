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

import me.kalmemarq.minicraft.level.Level;

public class SlimeEntity extends MobEntity {
    private int jumpTime = 0;

    public SlimeEntity(Level level) {
        super(level);
        this.data.put("jumpTime", 0);
        this.dataDirty = true;
    }

    @Override
    public void tickAi() {
        super.tickAi();

        int speed = 1;

        if (!this.move(this.xa * speed, this.ya * speed)  || this.random.nextInt(40) == 0) {
            if (this.jumpTime <= -10) {
                this.xa = (this.random.nextInt(3) - 1);
                this.ya = (this.random.nextInt(3) - 1);

                if (this.xa != 0 || this.ya != 0) this.jumpTime = 10;
            }
        }

        this.jumpTime--;
        if (this.jumpTime == 0) {
            this.xa = this.ya = 0;
        }
        this.data.put("jumpTime", this.jumpTime);
        this.dataDirty = true;
    }
}
