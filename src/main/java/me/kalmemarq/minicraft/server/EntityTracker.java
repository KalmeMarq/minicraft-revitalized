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

package me.kalmemarq.minicraft.server;

import me.kalmemarq.minicraft.level.entity.Entity;
import me.kalmemarq.minicraft.level.entity.ItemEntity;
import me.kalmemarq.minicraft.level.entity.SlimeEntity;
import me.kalmemarq.minicraft.level.entity.ZombieEntity;
import me.kalmemarq.minicraft.level.entity.particle.TextParticle;
import me.kalmemarq.minicraft.network.packet.EntityItemPacket;
import me.kalmemarq.minicraft.network.packet.EntityPacket;
import me.kalmemarq.minicraft.network.packet.EntityParticlePacket;

public class EntityTracker {
    private final Server server;
    private final Entity entity;
    private int trackingTicks;
    private final int tickInterval;
    private int x;
    private int y;
    private int zz;

    public EntityTracker(Server server, Entity entity) {
        this.server = server;
        this.entity = entity;
        this.tickInterval = 2;
        this.x = entity.x;
        this.y = entity.y;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void tick() {
        if (this.trackingTicks % this.tickInterval == 0) {
            boolean moved = this.entity.x != this.x || this.entity.y != y;

            if (moved || this.entity.dataDirty) {
                this.entity.dataDirty = false;
                int type = this.entity instanceof ZombieEntity ? 1 : this.entity instanceof SlimeEntity ? 3 : this.entity instanceof TextParticle ? 4 : this.entity instanceof ItemEntity ? 5 : 2;
                if (this.entity instanceof TextParticle textParticle) {
                    this.server.broadcast(new EntityParticlePacket(type, this.entity.uuid, this.entity.x, this.entity.y, textParticle.msg, textParticle.col));
                } else if (this.entity instanceof ItemEntity item) {
                    this.server.broadcast(new EntityItemPacket(type, item.uuid, item.x, item.y, item.zz, item.stack, item.data));
                } else {
                    this.server.broadcast(new EntityPacket(type, this.entity.uuid, this.entity.x, this.entity.y, this.entity.dir, this.entity.data));
                }
            }
        }

        ++this.trackingTicks;
    }
}
