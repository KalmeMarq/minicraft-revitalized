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

import org.lwjgl.glfw.GLFW;

public class InputHandler {
    public static Key[] KEYS = { Key.UP, Key.DOWN, Key.LEFT, Key.RIGHT, Key.ATTACK, Key.MENU };

    public void onKey(int key, int scancode, int action, int mods) {
        this.toggle(key, action != GLFW.GLFW_RELEASE);
    }

    public void releaseAll() {
        for (Key key : KEYS) {
            key.down = false;
        }
    }

    public void tick() {
        for (Key key : KEYS) {
            key.tick();
        }
    }

    private void toggle(int ke, boolean pressed) {
        for (Key key : KEYS) {
            if (key.test(ke)) {
                key.toggle(pressed);
            }
        }
    }

    public static class Key {
        public static Key UP = new Key(GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_UP);
        public static Key DOWN = new Key(GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_DOWN);
        public static Key LEFT = new Key(GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT);
        public static Key RIGHT = new Key(GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_RIGHT);
        public static Key ATTACK = new Key(GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_ENTER,GLFW.GLFW_KEY_C);
        public static Key MENU = new Key(GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_X);

        public int[] keys;
        public int presses, absorbs;
        public boolean down, clicked;

        public Key(int... keys) {
            this.keys = keys;
        }

        public void toggle(boolean pressed) {
            if (pressed != this.down) {
                this.down = pressed;
            }
            if (pressed) {
                this.presses++;
            }
        }

        public void tick() {
            if (this.absorbs < this.presses) {
                this.absorbs++;
                this.clicked = true;
            } else {
                this.clicked = false;
            }
        }

        public boolean test(int key) {
            for (int keycode : this.keys) {
                if (key == keycode) return true;
            }
            return false;
        }

        public boolean testPressed(long window) {
            for (int keycode : this.keys) {
                if (GLFW.glfwGetKey(window, keycode) != GLFW.GLFW_RELEASE) return true;
            }
            return false;
        }
    }
}
