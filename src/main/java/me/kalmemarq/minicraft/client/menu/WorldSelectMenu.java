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

package me.kalmemarq.minicraft.client.menu;

import me.kalmemarq.minicraft.client.Client;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorldSelectMenu extends Menu {
    private final Menu parent;
    private Path savesPath;
    private List<String> worlds = new ArrayList<>();
    private int selected;

    public WorldSelectMenu(Menu parent) {
        this.parent = parent;
    }

    @Override
    public void init(Client client) {
        super.init(client);
        this.savesPath = client.saveDir.resolve("saves");

        try (var paths = Files.walk(this.savesPath)) {
            for (Path path : paths.filter(Files::isDirectory).filter(p -> !p.equals(this.savesPath)).toList()) {
                this.worlds.add(path.getFileName().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(int key) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setMenu(this.parent);
        }

        if (key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_UP) this.selected--;
        if (key == GLFW.GLFW_KEY_S || key == GLFW.GLFW_KEY_DOWN) this.selected++;

        int len = this.worlds.size();
        if (this.selected < 0) this.selected += len;
        if (this.selected >= len) this.selected -= len;

        if (len > 0) {
            if (key == GLFW.GLFW_KEY_D && (GLFW.glfwGetKey(this.client.window.getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) != GLFW.GLFW_RELEASE || GLFW.glfwGetKey(this.client.window.getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) != GLFW.GLFW_RELEASE)) {
                Path worldPath = this.savesPath.resolve(this.worlds.get(this.selected));
                try {
                    Files.walk(worldPath)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.worlds.remove(this.selected);
                this.selected = 0;
            }
        }
    }

    @Override
    public void render() {
        this.client.font.draw("Select Menu", (this.getWidth() - 88) / 2, 8, 0xFFFFFF);
        this.client.font.draw("Enter to Confirm", (this.getWidth() - 16 * 8) / 2, this.getHeight() - 32 + 1, 0x707070);
        this.client.font.draw("Escape to Return", (this.getWidth() - 16 * 8) / 2, this.getHeight() - 24 + 2, 0x707070);
        this.client.font.draw("Shift-D to Delete", (this.getWidth() - 17 * 8) / 2, this.getHeight() - 16 + 3, 0x702020);

        for (int i = 0; i < this.worlds.size(); i++) {
            String msg = this.worlds.get(i);
            int col = 0x808080;
            if (i == this.selected) {
                msg = "> " + msg + " <";
                col = 0xFFFFFF;
            }
            this.client.font.draw(msg, (this.getWidth() - msg.length() * 8) / 2, 8 * 4 + i * 12 - 8, col);
        }
    }
}
