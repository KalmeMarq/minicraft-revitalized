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

import me.kalmemarq.minicraft.client.util.IOUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class GameWindow {
    private final long handle;

    private int x;
    private int y;
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;

    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;

    private boolean fullscreen;
    private boolean currentFullscreen;

    private WindowEventHandler windowEventHandler;
    private KeyboardEventHandler keyboardEventHandler;
    private Callback debugCallback;

    public GameWindow(int width, int height, String title) {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Could not initialize GLFW");
        }

//        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
//        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);

        this.handle = GLFW.glfwCreateWindow(width, height, title, 0L, 0L);

        if (this.handle == 0L) {
            throw new RuntimeException("Could not create GLFW window");
        }

        GLFW.glfwMakeContextCurrent(this.handle);
        GLFW.glfwSwapInterval(GLFW.GLFW_TRUE);
        this.setIcon();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pW = stack.mallocInt(1);
            IntBuffer pH = stack.mallocInt(1);
            IntBuffer pFW = stack.mallocInt(1);
            IntBuffer pFH = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(this.handle, pW, pH);
            GLFW.glfwGetFramebufferSize(this.handle, pFW, pFH);

            this.width = pW.get(0);
            this.height = pH.get(0);
            this.framebufferWidth = pFW.get(0);
            this.framebufferHeight = pFH.get(0);
        }

        GLFW.glfwSetWindowPosCallback(this.handle, (window, x, y) -> {
            this.x = x;
            this.y = y;
        });

        GLFW.glfwSetWindowSizeCallback(this.handle, (window, wwidth, wheight) -> {
            this.width = wwidth;
            this.height = wheight;
        });

        GLFW.glfwSetFramebufferSizeCallback(this.handle, (_w, fwidth, fheight) -> {
            this.framebufferWidth = fwidth;
            this.framebufferHeight = fheight;

            if (this.windowEventHandler != null) {
                this.windowEventHandler.onResize();
            }
        });

		GLFW.glfwSetCharCallback(this.handle, (_w, codepoint) -> {
			if (this.keyboardEventHandler != null) {
				this.keyboardEventHandler.onCharTyped(codepoint);
			}
		});

        GLFW.glfwSetKeyCallback(this.handle, (_w, key, scancode, action, mods) -> {
            if (this.keyboardEventHandler != null) {
                this.keyboardEventHandler.onKey(key, action);
            }
        });

        GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (videoMode != null) {
            GLFW.glfwSetWindowPos(this.handle, (videoMode.width() - this.width) / 2, (videoMode.height() - this.height) / 2);
        }

        GL.createCapabilities();

        this.debugCallback = GLUtil.setupDebugMessageCallback(System.out);
    }

    public void setWindowEventListener(WindowEventHandler handler) {
        this.windowEventHandler = handler;
    }

    public void setKeyboardEventHandler(KeyboardEventHandler keyboardEventHandler) {
        this.keyboardEventHandler = keyboardEventHandler;
    }

    private void setIcon() {
        String[] icons = {"/textures/icons/16x16.png", "/textures/icons/32x32.png", "/textures/icons/48x48.png", "/textures/icons/64x64.png", "/textures/icons/128x128.png", "/textures/icons/256x256.png"};

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer wP = stack.mallocInt(1);
            IntBuffer hP = stack.mallocInt(1);
            IntBuffer cP = stack.mallocInt(1);

            List<ByteBuffer> imageBuffers = new ArrayList<>();
            GLFWImage.Buffer iconsBuffer = GLFWImage.malloc(icons.length, stack);
            for (int i = 0; i < icons.length; ++i) {
                ByteBuffer iconData = IOUtils.readInputStreamToByteBuffer(GameWindow.class.getResourceAsStream(icons[i]));
                ByteBuffer iconPixels = STBImage.stbi_load_from_memory(iconData, wP, hP, cP, 4);

                if (iconPixels != null) {
                    iconsBuffer.position(i);
                    iconsBuffer.width(wP.get(0));
                    iconsBuffer.height(hP.get(0));
                    iconsBuffer.pixels(iconPixels);

                    imageBuffers.add(iconPixels);
                }

                MemoryUtil.memFree(iconData);
            }
            iconsBuffer.position(0);
            GLFW.glfwSetWindowIcon(this.handle, iconsBuffer);
            imageBuffers.forEach(STBImage::stbi_image_free);
        }
    }

    public boolean isFocused() {
        return GLFW.glfwGetWindowAttrib(this.handle, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
    }

    public int getWidth() {
        return this.framebufferWidth;
    }

    public int getHeight() {
        return this.framebufferHeight;
    }

    public boolean isFullscreen() {
        return this.fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public void toggleFullscreen() {
        this.setFullscreen(!this.isFullscreen());
    }

    public void show() {
        GLFW.glfwShowWindow(this.handle);
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(this.handle);
    }

    public void update() {
        GLFW.glfwSwapBuffers(this.handle);
        GLFW.glfwPollEvents();

        if (this.fullscreen != this.currentFullscreen) {
            if (this.fullscreen) {
                this.windowX = this.x;
                this.windowY = this.y;
                this.windowWidth = this.width;
                this.windowHeight = this.height;

                long monitor = GLFW.glfwGetPrimaryMonitor();
                GLFWVidMode videoMode = GLFW.glfwGetVideoMode(monitor);
                GLFW.glfwSetWindowMonitor(this.handle, GLFW.glfwGetPrimaryMonitor(), 0, 0, videoMode.width(), videoMode.height(), GLFW.GLFW_DONT_CARE);
            } else {
                this.x = this.windowX;
                this.y = this.windowY;
                this.width = this.windowWidth;
                this.height = this.windowHeight;
                GLFW.glfwSetWindowMonitor(this.handle, 0L, this.x, this.y, this.width, this.height, GLFW.GLFW_DONT_CARE);
            }

            this.currentFullscreen = this.fullscreen;
        }
    }

    public void close() {
        Callbacks.glfwFreeCallbacks(this.handle);
        this.debugCallback.close();
        GLFW.glfwDestroyWindow(this.handle);
        GLFW.glfwTerminate();
    }

    public long getHandle() {
        return this.handle;
    }

	public void setVsync(boolean vsync) {
		GLFW.glfwSwapInterval(vsync ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
	}

	public interface WindowEventHandler {
        void onResize();
    }

    public interface KeyboardEventHandler {
        void onKey(int key, int action);
        void onCharTyped(int codepoint);
    }
}
