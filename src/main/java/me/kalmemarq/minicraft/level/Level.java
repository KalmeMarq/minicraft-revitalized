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

package me.kalmemarq.minicraft.level;

import me.kalmemarq.minicraft.level.entity.Entity;
import me.kalmemarq.minicraft.level.entity.MobEntity;
import me.kalmemarq.minicraft.level.entity.SlimeEntity;
import me.kalmemarq.minicraft.level.entity.ZombieEntity;
import me.kalmemarq.minicraft.level.tile.Tile;
import me.kalmemarq.minicraft.level.tile.Tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Level {
    protected static final Random RANDOM = new Random();
    public int width;
    public int height;
    public byte[] tiles;
    public byte[] data;
    public List<Entity>[] entitiesInTiles;
    public boolean loaded;

    abstract public boolean isClient();

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) return Tiles.ROCK;
        return Tiles.REGISTRY.getByNumericId(this.tiles[x + y * this.width]);
    }

    public void setTile(int x, int y, Tile t) {
        this.setTile(x, y, t, 0);
    }

    public void setTile(int x, int y, Tile t, int data) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) return;
        this.tiles[x + y * this.width] = (byte) t.getNumericId();
        this.data[x + y * this.width] = (byte) data;
    }

    public void setData(int x, int y, int val) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) return;
        this.data[x + y * this.width] = (byte) val;
    }

    public int getData(int x, int y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) return 0;
        return this.data[x + y * this.width] & 0xff;
    }

    public void add(Entity entity) {
    }

    public void insertEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) return;
        this.entitiesInTiles[x + y * this.width].add(e);
    }

    public void removeEntity(int x, int y, Entity e) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) return;
        this.entitiesInTiles[x + y * this.width].remove(e);
    }

    public void trySpawn(int count) {
        for (int i = 0; i < count; i++) {
            MobEntity mob;

            if (RANDOM.nextInt(2) == 0)
                mob = new SlimeEntity(this);
            else
                mob = new ZombieEntity(this);

            if (mob.findStartPos(this)) {
                System.out.println("Added entity " + mob.uuid);
                this.add(mob);
            }
        }
    }

    public List<Entity> getEntities(int x0, int y0, int x1, int y1) {
        List<Entity> result = new ArrayList<>();
        int xt0 = (x0 >> 4) - 1;
        int yt0 = (y0 >> 4) - 1;
        int xt1 = (x1 >> 4) + 1;
        int yt1 = (y1 >> 4) + 1;
        for (int y = yt0; y <= yt1; y++) {
            for (int x = xt0; x <= xt1; x++) {
                if (x < 0 || y < 0 || x >= this.width || y >= this.height) continue;
                List<Entity> entities = this.entitiesInTiles[x + y * this.width];
                for (Entity e : entities) {
                    if (e.intersects(x0, y0, x1, y1)) result.add(e);
                }
            }
        }
        return result;
    }
}
