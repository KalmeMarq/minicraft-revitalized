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

import me.kalmemarq.minicraft.util.Registry;
import me.kalmemarq.minicraft.level.entity.furniture.AnvilFurniture;
import me.kalmemarq.minicraft.level.entity.furniture.ChestFurniture;
import me.kalmemarq.minicraft.level.entity.furniture.FurnaceFurniture;
import me.kalmemarq.minicraft.level.entity.furniture.OvenFurniture;
import me.kalmemarq.minicraft.level.entity.furniture.WorkbenchFurniture;
import me.kalmemarq.minicraft.level.entity.particle.SmashParticle;
import me.kalmemarq.minicraft.level.entity.particle.TextParticle;

public class EntityType<T extends Entity> {
    public static Registry<EntityType<?>> REGISTRY = new Registry<>();

    public static <T extends Entity> EntityType<T> register(String id, EntityType<T> entityType) {
        REGISTRY.register(id, entityType);
        return entityType;
    }

    public static final EntityType<AnvilFurniture> ANVIL = register("anvil", new EntityType<>(AnvilFurniture.class));
    public static final EntityType<ChestFurniture> CHEST = register("chest", new EntityType<>(ChestFurniture.class));
    public static final EntityType<FurnaceFurniture> FURNACE = register("furnace", new EntityType<>(FurnaceFurniture.class));
    public static final EntityType<OvenFurniture> OVEN = register("oven", new EntityType<>(OvenFurniture.class));
    public static final EntityType<WorkbenchFurniture> WORKBENCH = register("workbench", new EntityType<>(WorkbenchFurniture.class));
    public static final EntityType<ItemEntity> ITEM_ENTITY = register("item_entity", new EntityType<>(ItemEntity.class));
    public static final EntityType<PlayerEntity> PLAYER = register("player", new EntityType<>(PlayerEntity.class));
    public static final EntityType<SlimeEntity> SLIME = register("slime", new EntityType<>(SlimeEntity.class));
    public static final EntityType<ZombieEntity> ZOMBIE = register("zombie", new EntityType<>(ZombieEntity.class));
    public static final EntityType<SmashParticle> SMASH_PARTICLE = register("smash_particle", new EntityType<>(SmashParticle.class));
    public static final EntityType<TextParticle> TEXT_PARTICLE = register("text_particle", new EntityType<>(TextParticle.class));

    public Class<T> clazz;

    public EntityType(Class<T> clazz) {
        this.clazz = clazz;
    }
}
