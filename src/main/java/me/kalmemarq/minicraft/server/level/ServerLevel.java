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

package me.kalmemarq.minicraft.server.level;

import me.kalmemarq.minicraft.level.Level;
import me.kalmemarq.minicraft.level.gen.TopMapGenerator;
import me.kalmemarq.minicraft.level.entity.Entity;
import me.kalmemarq.minicraft.level.tile.Tile;
import me.kalmemarq.minicraft.network.packet.EntityRemovePacket;
import me.kalmemarq.minicraft.network.packet.SetTilePacket;
import me.kalmemarq.minicraft.server.EntityTracker;
import me.kalmemarq.minicraft.server.Server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerLevel extends Level {
    private final Server server;
    private final List<Entity> pendingToAdd = new ArrayList<>();

    public ServerLevel(Server server) {
        this.server = server;
    }

    @Override
    public void setTile(int x, int y, Tile t, int data) {
        super.setTile(x, y, t, data);
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) return;
        System.out.println(x + "," + y + ";" + t.getStringId() + "," + data);
        this.server.broadcast(new SetTilePacket(x, y, t.getNumericId(), data));
    }

    @Override
    public void setData(int x, int y, int val) {
        super.setData(x, y, val);
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) return;
        System.out.println(x + "," + y + ";-," + val);
        this.server.broadcast(new SetTilePacket(x, y, this.tiles[x + y * this.width], val));
    }

    @Override
    public void add(Entity entity) {
        this.pendingToAdd.add(entity);
        this.insertEntity(entity.x >> 4, entity.y >> 4, entity);
    }

    public void remove(Entity e) {
        int xto = e.x >> 4;
        int yto = e.y >> 4;
        this.removeEntity(xto, yto, e);
    }

    @SuppressWarnings("unchecked")
    public void generate(int width, int height) {
        this.width = width;
        this.height = height;
        byte[][] result = new TopMapGenerator(this.width, this.height, 4L).generateAndValidate();
        this.tiles = result[0];
        this.data = result[1];
        this.loaded = true;
        this.entitiesInTiles = new ArrayList[this.width * this.height];
        for (int i = 0; i < this.width * this.height; i++) {
            this.entitiesInTiles[i] = new ArrayList<>();
        }
    }

    public void tick() {
        if (RANDOM.nextInt(400) < 1) this.trySpawn(1);

        Iterator<Entity> iter = this.server.entities.iterator();

        while (iter.hasNext()) {
            Entity entity = iter.next();

            int xto = entity.x >> 4;
            int yto = entity.y >> 4;
            entity.tick();

            if (entity.removed) {
                iter.remove();
                this.removeEntity(xto, yto, entity);

                for (int i = 0; i < this.server.entityTrackers.size(); ++i) {
                    if (this.server.entityTrackers.get(i).getEntity() == entity) {
                        this.server.entityTrackers.remove(i);
                        this.server.broadcast(new EntityRemovePacket(entity.uuid));
                        break;
                    }
                }
            } else {
                int xt = entity.x >> 4;
                int yt = entity.y >> 4;

                if (xto != xt || yto != yt) {
                    this.removeEntity(xto, yto, entity);
                    this.insertEntity(xt, yt, entity);
                }
            }
        }

        for (EntityTracker tracker : this.server.entityTrackers) {
            tracker.tick();
        }

        for (Entity entity : this.pendingToAdd) {
            this.server.loadEntity(entity);
        }
        this.pendingToAdd.clear();
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
