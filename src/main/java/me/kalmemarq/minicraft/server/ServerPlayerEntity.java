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

import me.kalmemarq.minicraft.level.Level;
import me.kalmemarq.minicraft.level.entity.ItemEntity;
import me.kalmemarq.minicraft.level.entity.PlayerEntity;
import me.kalmemarq.minicraft.network.packet.InventoryPacket;
import me.kalmemarq.minicraft.network.packet.OpenInventoryPacket;
import me.kalmemarq.minicraft.network.packet.PlayerStatsPacket;
import me.kalmemarq.minicraft.server.network.ServerPlayNetworkHandler;

public class ServerPlayerEntity extends PlayerEntity {
    public ServerPlayNetworkHandler networkHandler;
	public String username;
    private int syncHealth = -1;
    private int syncStamina = -1;

    public ServerPlayerEntity(Level level) {
        super(level);
    }

    public void doAttack() {
        if (this.stamina != 0) {
            this.stamina--;
            this.staminaRecharge = 0;
            this.attack();
        }
    }

    public void doUse() {
        if (!this.use()) {
            this.networkHandler.sendPacket(new OpenInventoryPacket());
        }
    }

    public void netTick() {
        if (this.syncHealth != this.health || this.syncStamina != this.stamina) {
            this.networkHandler.sendPacket(new PlayerStatsPacket(this.health, this.stamina));
            this.syncHealth = this.health;
            this.syncStamina = this.stamina;
        }
    }

    @Override
    public void touchItem(ItemEntity itemEntity) {
        super.touchItem(itemEntity);
        if (!this.level.isClient()) {
            this.networkHandler.sendPacket(new InventoryPacket(this.inventory.itemStacks));
        }
    }
}
