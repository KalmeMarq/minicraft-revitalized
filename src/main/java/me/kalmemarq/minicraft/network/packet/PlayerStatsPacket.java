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

package me.kalmemarq.minicraft.network.packet;

import io.netty.buffer.ByteBuf;
import me.kalmemarq.minicraft.network.Packet;

public class PlayerStatsPacket extends Packet {
    private int health;
    private int stamina;

    public PlayerStatsPacket() {
    }

    public PlayerStatsPacket(int health, int stamina) {
        this.health = health;
        this.stamina = stamina;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeByte(this.health);
        buffer.writeByte(this.stamina);
    }

    @Override
    public void read(ByteBuf buffer) {
        this.health = buffer.readByte();
        this.stamina = buffer.readByte();
    }

    public int getHealth() {
        return this.health;
    }

    public int getStamina() {
        return this.stamina;
    }
}
