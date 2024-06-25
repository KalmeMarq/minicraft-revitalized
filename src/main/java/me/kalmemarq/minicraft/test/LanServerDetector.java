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

import me.kalmemarq.minicraft.util.ThreadExecutor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.function.Consumer;

// https://www.developer.com/design/how-to-multicast-using-java-sockets/
public class LanServerDetector extends Thread {
    private MulticastSocket socket;
    private byte[] buf = new byte[512];
    private final ThreadExecutor executor;
    private final Consumer<String> consumer;

    public LanServerDetector(ThreadExecutor executor, Consumer<String> consumer) {
        this.executor = executor;
        this.consumer = consumer;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        InetSocketAddress group = null;
        try {
            group = new InetSocketAddress(InetAddress.getByName("230.0.0.1"), 5000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (group == null) {
            return;
        }

        try {
            this.socket = new MulticastSocket(4446);
            this.socket.setSoTimeout(64);
            this.socket.joinGroup(group, null);

            while (!this.isInterrupted()) {
                DatagramPacket packet = new DatagramPacket(this.buf, this.buf.length);
                this.socket.receive(packet);
                String received = new String(packet.getData(), packet.getOffset(), packet.getLength());
                this.executor.execute(() -> this.consumer.accept(received));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (this.socket != null) {
                    this.socket.leaveGroup(group, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (this.socket != null) {
                this.socket.close();
            }
        }
    }
}
