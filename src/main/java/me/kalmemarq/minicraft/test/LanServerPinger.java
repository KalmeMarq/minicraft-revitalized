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

package me.kalmemarq.minicraft.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class LanServerPinger extends Thread {
    private DatagramSocket socket;
    private final byte[] buf;

    public LanServerPinger(String motd, int port) {
        this.setDaemon(true);
        this.buf = ("<motd>" + motd + "</motd><addressport>" + port + "</addressport>").getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void run() {
        try {
            this.socket = new DatagramSocket();

            while (!this.isInterrupted()) {
                InetAddress inetAddress = InetAddress.getByName("230.0.0.1");
                DatagramPacket datagramPacket = new DatagramPacket(this.buf, this.buf.length, inetAddress, 5000);
                this.socket.send(datagramPacket);

                try {
                    LanServerPinger.sleep(1500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
