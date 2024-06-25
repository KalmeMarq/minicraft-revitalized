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

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class ServerConsoleGui {
    public JTextArea textArea;
    public JTextField inputField;

    public Runnable onClose = () -> {
    };
    public Consumer<String> onSend = (msg) -> {
    };
    public JFrame frame;

    public ServerConsoleGui() {
        this.frame = new JFrame("Server");
        this.frame.setPreferredSize(new Dimension(400, 400));
        this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ServerConsoleGui.this.onClose.run();
            }
        });

        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        contentPane.add(this.textArea, "Center");

        this.inputField = new JTextField();
        contentPane.add(this.inputField, "South");


        this.inputField.addActionListener(l -> {
            if (this.inputField.getText().trim().isEmpty()) return;
            this.onSend.accept(this.inputField.getText().trim());
            this.inputField.setText("");
        });

        this.frame.setContentPane(contentPane);

        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void setOnSend(Consumer<String> onSend) {
        this.onSend = onSend;
    }
}
