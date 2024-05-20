package com.mojang.ld22;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import me.kalmemarq.minicraft.render.NativeImage;
import org.lwjgl.glfw.GLFW;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class InputHandler implements KeyListener {
	public class Key {
		public int presses, absorbs;
		public boolean down, clicked;

		public Key() {
            InputHandler.this.keys.add(this);
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
	}

	public List<Key> keys = new ArrayList<Key>();

	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();
	public Key attack = new Key();
	public Key menu = new Key();

	public void releaseAll() {
		for (Key key : this.keys) {
			key.down = false;
		}
	}

	public void tick() {
		for (Key key : this.keys) {
			key.tick();
		}
	}

	public InputHandler(Game game) {
		if (Game.SHOW_JFRAME) game.canvas.addKeyListener(this);
	}

	public void keyPressed(KeyEvent ke) {
        this.toggle(ke.getKeyCode(), true);
	}

	public void keyReleased(KeyEvent ke) {
        this.toggle(ke.getKeyCode(), false);
	}

	public void onKey(int key, int scancode, int action, int mods) {
		if (key == GLFW.GLFW_KEY_F11 && action == GLFW.GLFW_RELEASE) {
			Game.instance.window.toggleFullscreen();
		}

		if (Game.DEBUG_KEYS) {
			if (key == GLFW.GLFW_KEY_F7 && action == GLFW.GLFW_RELEASE) {
				Game.instance.level.tpToStairs(false);
			}
			if (key == GLFW.GLFW_KEY_F8 && action == GLFW.GLFW_RELEASE) {
				Game.instance.level.tpToStairs(true);
			}
			if (key == GLFW.GLFW_KEY_F12 && action == GLFW.GLFW_RELEASE) {
				NativeImage image = NativeImage.read(Game.instance.colorsTexture);
				if (!image.saveTo(Path.of("bruv.png"))) {
					System.out.println("Could not save bruh.png");
				}
				image.close();
			}
			if (key == GLFW.GLFW_KEY_F9 && action == GLFW.GLFW_RELEASE) {
				Game.instance.screenshotRequested = true;
			}
		}

		int kc = keyCodes.getOrDefault(key, -1);
		if (kc == -1) return;
		this.toggle(kc, action != GLFW.GLFW_RELEASE);
	}

	private void toggle(int ke, boolean pressed) {
		if (ke == KeyEvent.VK_NUMPAD8) this.up.toggle(pressed);
		if (ke == KeyEvent.VK_NUMPAD2) this.down.toggle(pressed);
		if (ke == KeyEvent.VK_NUMPAD4) this.left.toggle(pressed);
		if (ke == KeyEvent.VK_NUMPAD6) this.right.toggle(pressed);
		if (ke == KeyEvent.VK_W) this.up.toggle(pressed);
		if (ke == KeyEvent.VK_S) this.down.toggle(pressed);
		if (ke == KeyEvent.VK_A) this.left.toggle(pressed);
		if (ke == KeyEvent.VK_D) this.right.toggle(pressed);
		if (ke == KeyEvent.VK_UP) this.up.toggle(pressed);
		if (ke == KeyEvent.VK_DOWN) this.down.toggle(pressed);
		if (ke == KeyEvent.VK_LEFT) this.left.toggle(pressed);
		if (ke == KeyEvent.VK_RIGHT) this.right.toggle(pressed);

		if (ke == KeyEvent.VK_TAB) this.menu.toggle(pressed);
		if (ke == KeyEvent.VK_ALT) this.menu.toggle(pressed);
		if (ke == KeyEvent.VK_ALT_GRAPH) this.menu.toggle(pressed);
		if (ke == KeyEvent.VK_SPACE) this.attack.toggle(pressed);
		if (ke == KeyEvent.VK_CONTROL) this.attack.toggle(pressed);
		if (ke == KeyEvent.VK_NUMPAD0) this.attack.toggle(pressed);
		if (ke == KeyEvent.VK_INSERT) this.attack.toggle(pressed);
		if (ke == KeyEvent.VK_ENTER) this.menu.toggle(pressed);

		if (ke == KeyEvent.VK_X) this.menu.toggle(pressed);
		if (ke == KeyEvent.VK_C) this.attack.toggle(pressed);
	}

	public void keyTyped(KeyEvent ke) {
	}

	public static Int2IntMap keyCodes = new Int2IntOpenHashMap();

	static {
		keyCodes.put(GLFW.GLFW_KEY_KP_8, KeyEvent.VK_W);
		keyCodes.put(GLFW.GLFW_KEY_KP_2, KeyEvent.VK_A);
		keyCodes.put(GLFW.GLFW_KEY_KP_4, KeyEvent.VK_S);
		keyCodes.put(GLFW.GLFW_KEY_KP_6, KeyEvent.VK_D);
		keyCodes.put(GLFW.GLFW_KEY_W, KeyEvent.VK_W);
		keyCodes.put(GLFW.GLFW_KEY_S, KeyEvent.VK_S);
		keyCodes.put(GLFW.GLFW_KEY_A, KeyEvent.VK_A);
		keyCodes.put(GLFW.GLFW_KEY_D, KeyEvent.VK_D);
		keyCodes.put(GLFW.GLFW_KEY_UP, KeyEvent.VK_UP);
		keyCodes.put(GLFW.GLFW_KEY_DOWN, KeyEvent.VK_DOWN);
		keyCodes.put(GLFW.GLFW_KEY_LEFT, KeyEvent.VK_LEFT);
		keyCodes.put(GLFW.GLFW_KEY_RIGHT, KeyEvent.VK_RIGHT);
		keyCodes.put(GLFW.GLFW_KEY_TAB, KeyEvent.VK_TAB);
		keyCodes.put(GLFW.GLFW_KEY_LEFT_ALT, KeyEvent.VK_ALT);
		keyCodes.put(GLFW.GLFW_KEY_RIGHT_ALT, KeyEvent.VK_ALT);
		keyCodes.put(GLFW.GLFW_KEY_SPACE, KeyEvent.VK_SPACE);
		keyCodes.put(GLFW.GLFW_KEY_LEFT_CONTROL, KeyEvent.VK_CONTROL);
		keyCodes.put(GLFW.GLFW_KEY_RIGHT_CONTROL, KeyEvent.VK_CONTROL);
		keyCodes.put(GLFW.GLFW_KEY_KP_0, KeyEvent.VK_NUMPAD0);
		keyCodes.put(GLFW.GLFW_KEY_ENTER, KeyEvent.VK_ENTER);
		keyCodes.put(GLFW.GLFW_KEY_X, KeyEvent.VK_X);
		keyCodes.put(GLFW.GLFW_KEY_C, KeyEvent.VK_C);
	}
}
