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

package me.kalmemarq.minicraft.client;

import me.kalmemarq.minicraft.client.network.ClientPlayNetworkHandler;
import me.kalmemarq.minicraft.level.Level;
import me.kalmemarq.minicraft.level.entity.PlayerEntity;
import me.kalmemarq.minicraft.network.packet.PlayerAttackPacket;
import me.kalmemarq.minicraft.network.packet.PlayerUsePacket;

public class ClientPlayerEntity extends PlayerEntity {
    private final Client client;
    private final ClientPlayNetworkHandler networkHandler;

    public ClientPlayerEntity(Client client, Level level, ClientPlayNetworkHandler networkHandler) {
        super(level);
        this.client = client;
        this.networkHandler = networkHandler;
    }

    @Override
    public void tick() {
        super.tick();

        int xa = 0;
        int ya = 0;

        if (InputHandler.Key.UP.down) {
            ya -= 1;
        }

        if (InputHandler.Key.DOWN.down) {
            ya += 1;
        }

        if (InputHandler.Key.LEFT.down) {
            xa -= 1;
        }

        if (InputHandler.Key.RIGHT.down) {
            xa += 1;
        }

        this.move(xa, ya);

        if (InputHandler.Key.ATTACK.clicked) {
            this.networkHandler.send(new PlayerAttackPacket());
        }

        if (InputHandler.Key.MENU.clicked) {
            this.networkHandler.send(new PlayerUsePacket());
        }
    }
}
