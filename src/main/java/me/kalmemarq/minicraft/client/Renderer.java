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

import me.kalmemarq.minicraft.client.texture.AtlasTexture;
import me.kalmemarq.minicraft.level.entity.Entity;
import me.kalmemarq.minicraft.level.entity.ItemEntity;
import me.kalmemarq.minicraft.level.entity.PlayerEntity;
import me.kalmemarq.minicraft.level.entity.SlimeEntity;
import me.kalmemarq.minicraft.level.entity.ZombieEntity;
import me.kalmemarq.minicraft.level.entity.particle.TextParticle;
import me.kalmemarq.minicraft.level.item.Item;
import me.kalmemarq.minicraft.level.tile.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Renderer {
    public final Random wRandom = new Random();
    private final Client client;
    private GCMemoryAllocationRateCalculator allocationRateCalculator;

    public Renderer(Client client) {
        this.client = client;
        this.allocationRateCalculator = new GCMemoryAllocationRateCalculator();
    }

    public void render() {
        int WIDTH = 160;
        int HEIGHT = 120;

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 160, 120, 0, -1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        if (this.client.player != null && this.client.level != null && this.client.level.loaded) {
            int xScroll = this.client.player.x - WIDTH / 2;
            int yScroll = this.client.player.y - (HEIGHT - 8) / 2;
            if (xScroll < 16) xScroll = 16;
            if (yScroll < 16) yScroll = 16;
            if (xScroll > this.client.level.width * 16 - (WIDTH) - 16) xScroll = this.client.level.width * 16 - (WIDTH) - 16;
            if (yScroll > this.client.level.height * 16 - (HEIGHT) - 16) yScroll = this.client.level.height * 16 - (HEIGHT) - 16;
            GL11.glTranslatef(-xScroll, -yScroll, 0);

            {
                int xo = xScroll >> 4;
                int yo = yScroll >> 4;
                int w = ((this.client.window.getWidth() / 3) + 15) >> 4;
                int h = ((this.client.window.getHeight() / 3) + 15) >> 4;

                AtlasTexture texture = this.client.textureManager.getAtlas("tiles");
                texture.bind();

                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glBegin(GL11.GL_QUADS);

                for (int y = yo; y <= h + yo; y++) {
                    for (int x = xo; x <= w + xo; x++) {
                        if (x < 0 || y < 0 || x >= 64 || y >= 64) continue;
                        this.renderTile(texture, this.client.level.getTile(x, y), x, y);
                    }
                }

                GL11.glEnd();
            }
        }

        for (Entity entity : this.client.entities) {
            if (entity instanceof ZombieEntity zombie) {
                this.renderZombie(zombie);
            } else if (entity instanceof SlimeEntity slime) {
                this.renderSlime(slime);
            } else if (entity instanceof ItemEntity item) {
                this.renderItemEntity(item);
            }
        }

        if (this.client.player != null) {
            this.renderPlayer(this.client.player);
        }

        for (Entity entity : this.client.entities) {
            if (entity instanceof TextParticle textParticle) {
                this.renderTextParticle(textParticle);
            }
        }

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        if (this.client.player != null) {
            this.client.textureManager.bind("White");

            GL11.glColor4f(0, 0, 0, 1);
            GL11.glBegin(GL11.GL_QUADS);
            {
                GL11.glVertex3f(0, (this.client.window.getHeight() / 3f) - 16, 0);
                GL11.glVertex3f(0, (this.client.window.getHeight() / 3f), 0);
                GL11.glVertex3f(0 + this.client.window.getWidth() / 3f, (this.client.window.getHeight() / 3f), 0);
                GL11.glVertex3f(0 + this.client.window.getWidth() / 3f, (this.client.window.getHeight() / 3f) - 16, 0);
            }
            GL11.glEnd();
            GL11.glColor4f(1, 1, 1, 1);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            for (int i = 0; i < this.client.player.maxHealth; ++i) {
                this.renderSprite("ui", i >= this.client.player.health ? "heart_empty.png" : "heart.png", i * 8, (int) ((this.client.window.getHeight() / 3f) - 16), 8, 8, false, false);
            }
            for (int i = 0; i < this.client.player.maxStamina; ++i) {
                String sprite = i >= this.client.player.stamina ? "stamina_empty.png" : "stamina.png";
                if (this.client.player.staminaRechargeDelay > 0 && this.client.player.staminaRechargeDelay / 4 % 2 == 0) {
                    sprite = "stamina_empty_blinking.png";
                }
                this.renderSprite("ui", sprite, i * 8, (int) ((this.client.window.getHeight() / 3f) - 8), 8, 8, false, false);
            }
        }

        if (this.client.menu != null) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.client.menu.render();
        }

        if (this.client.showDebug) {
            String debug = this.client.debugString + "\n";

            long maxMem = Runtime.getRuntime().maxMemory();
            long totalMem = Runtime.getRuntime().totalMemory();
            long freeMem = Runtime.getRuntime().freeMemory();
            long usedMem = totalMem - freeMem;

            debug += "Mem: " + (usedMem / 1024 / 1024) + "MB / " + (maxMem / 1024 / 1024) + "MB";
            debug += "\nAlloc: " + (totalMem * 100L / totalMem) + "% " + (totalMem / 1024 / 1024) + "MB";
            debug += "\nAlloc rate: " + (this.allocationRateCalculator.calculateAllocationRate() / 1024 / 1024) + "MB/s";

            if (this.client.player != null) {
                debug += "\nX: " + this.client.player.x + " Y: " + this.client.player.y + " D: " + this.client.player.dir;
            }

            this.client.font.drawOutlined(debug, 1, 1, 0xFFFFFF, 2);
        }

        boolean focused = GLFW.glfwGetWindowAttrib(this.client.window.getHandle(), GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE;
        if (!focused) {
            this.renderFocusNagger();
        }
    }

    public void renderFocusNagger() {
        String msg = "Click to focus!";
        int xx = (this.client.window.getWidth() / 3 - msg.length() * 8) / 2;
        int yy = (this.client.window.getHeight() / 3 - 8) / 2;
        int w = msg.length();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        this.renderSpriteNineslice("ui", "frame.png", xx - 8, yy - 8, w * 8 + 16, 24, 8, 8, 8, 8);

        this.client.font.draw(msg, xx, yy, (this.client.tickCount / 20) % 2 == 0 ? 0x777777 : 0xFFFFFF);
    }

    public void renderSprite(String atlasPath, String spritePath, int x, int y, int width, int height, int bits) {
        this.renderSprite(atlasPath, spritePath, x, y, width, height, (bits & 0x01) > 0, (bits & 0x02) > 0);
    }

    public void renderSprite(String atlasPath, String spritePath, int x, int y, int width, int height, boolean flipH, boolean flipV) {
        AtlasTexture atlas = this.client.textureManager.getAtlas(atlasPath);
        AtlasTexture.SpriteInfo spriteInfo = atlas.getSpriteInfo(spritePath);

        atlas.bind();
        GL11.glBegin(GL11.GL_QUADS);

        float u0 = spriteInfo.minU();
        float v0 = spriteInfo.minV();
        float u1 = spriteInfo.maxU();
        float v1 = spriteInfo.maxV();

        if (flipH) {
            float temp = u0;
            u0 = u1;
            u1 = temp;
        }

        if (flipV) {
            float temp = v0;
            v0 = v1;
            v1 = temp;
        }

        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex3f(x, y, 0);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex3f(x, y + height, 0);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex3f(x + width, y + height, 0);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex3f(x + width, y, 0);

        GL11.glEnd();
    }

    public void renderSprite(String atlasPath, String spritePath, int x, int y, int width, int height, int u, int v, int bits) {
        this.renderSprite(atlasPath, spritePath, x, y, width, height, u, v, (bits & 0x01) > 0, (bits & 0x02) > 0);
    }

    public void renderSprite(String atlasPath, String spritePath, int x, int y, int width, int height, int u, int v, int us, int vs, int bits) {
        this.renderSprite(atlasPath, spritePath, x, y, width, height, u, v, us, vs, (bits & 0x01) > 0, (bits & 0x02) > 0);
    }

    public void renderSprite(String atlasPath, String spritePath, int x, int y, int width, int height, int u, int v, boolean flipH, boolean flipV) {
        this.renderSprite(atlasPath, spritePath, x, y, width, height, u, v, width, height, flipH, flipV);
    }

    public void renderSprite(String atlasPath, String spritePath, int x, int y, int width, int height, int u, int v, int us, int vs, boolean flipH, boolean flipV) {
        AtlasTexture atlas = this.client.textureManager.getAtlas(atlasPath);
        AtlasTexture.SpriteInfo spriteInfo = atlas.getSpriteInfo(spritePath);

        atlas.bind();
        GL11.glBegin(GL11.GL_QUADS);

        float atlasWidth = atlas.getWidth();
        float atlasHeight = atlas.getHeight();

        float u0 = (spriteInfo.u() + u) / atlasWidth;
        float v0 = (spriteInfo.v() + v) / atlasHeight;
        float u1 = (spriteInfo.u() + u + us) / atlasWidth;
        float v1 = (spriteInfo.v() + v + vs) / atlasHeight;

        if (flipH) {
            float temp = u0;
            u0 = u1;
            u1 = temp;
        }

        if (flipV) {
            float temp = v0;
            v0 = v1;
            v1 = temp;
        }

        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex3f(x, y, 0);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex3f(x, y + height, 0);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex3f(x + width, y + height, 0);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex3f(x + width, y, 0);

        GL11.glEnd();
    }

    public void renderSpriteNineslice(String atlasPath, String spritePath, int x, int y, int width, int height, int nx0, int ny0, int nx1, int ny1) {
        AtlasTexture atlas = this.client.textureManager.getAtlas(atlasPath);
        AtlasTexture.SpriteInfo spriteInfo = atlas.getSpriteInfo(spritePath);

        atlas.bind();
        GL11.glBegin(GL11.GL_QUADS);

        float atlasWidth = atlas.getWidth();
        float atlasHeight = atlas.getHeight();
        int u, v, us, vs = 0;
        float u0, v0, u1, v1 = 0;

        // Top Left
        u = spriteInfo.u();
        v = spriteInfo.v();
        us = nx0;
        vs = ny0;

        u0 = u / atlasWidth;
        v0 = v / atlasHeight;
        u1 = (u + us) / atlasWidth;
        v1 = (v + vs) / atlasHeight;

        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex3f(x, y, 0);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex3f(x, y + ny0, 0);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex3f(x + nx0, y + ny0, 0);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex3f(x + nx0, y, 0);

        // Top Middle
        u = spriteInfo.u() + nx0;
        v = spriteInfo.v();
        us = spriteInfo.width() - nx0 - nx1;
        vs = ny0;

        u0 = u / atlasWidth;
        v0 = v / atlasHeight;
        u1 = (u + us) / atlasWidth;
        v1 = (v + vs) / atlasHeight;

        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex3f(x + nx0, y, 0);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex3f(x + nx0, y + ny0, 0);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex3f(x + width - nx1, y + ny0, 0);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex3f(x + width - nx1, y, 0);

        // Top Right
        u = spriteInfo.u() + spriteInfo.width() - nx1;
        v = spriteInfo.v();
        us = nx1;
        vs = ny0;

        u0 = u / atlasWidth;
        v0 = v / atlasHeight;
        u1 = (u + us) / atlasWidth;
        v1 = (v + vs) / atlasHeight;

        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex3f(x + width - nx1, y, 0);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex3f(x + width - nx1, y + ny0, 0);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex3f(x + width, y + ny0, 0);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex3f(x + width, y, 0);

        // Left Middle
        u = spriteInfo.u();
        v = spriteInfo.v() + ny0;
        us = nx0;
        vs = spriteInfo.height() - ny0 - ny1;

        u0 = u / atlasWidth;
        v0 = v / atlasHeight;
        u1 = (u + us) / atlasWidth;
        v1 = (v + vs) / atlasHeight;

        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex3f(x, y + ny0, 0);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex3f(x, y + height - ny1, 0);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex3f(x + nx0, y + height - ny1, 0);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex3f(x + nx0, y + ny0, 0);

        // Center
        u = spriteInfo.u() + nx0;
        v = spriteInfo.v() + ny0;
        us = spriteInfo.width() - nx0 - nx1;
        vs = spriteInfo.height() - ny0 - ny1;

        u0 = u / atlasWidth;
        v0 = v / atlasHeight;
        u1 = (u + us) / atlasWidth;
        v1 = (v + vs) / atlasHeight;

        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex3f(x + nx0, y + ny0, 0);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex3f(x + nx0, y + height - ny1, 0);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex3f(x + width - nx1, y + height - ny1, 0);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex3f(x + width - nx1, y + ny0, 0);

        // Right Middle
        u = spriteInfo.u() + spriteInfo.width() - nx1;
        v = spriteInfo.v() + ny0;
        us = nx1;
        vs = spriteInfo.height() - ny0 - ny1;

        u0 = u / atlasWidth;
        v0 = v / atlasHeight;
        u1 = (u + us) / atlasWidth;
        v1 = (v + vs) / atlasHeight;

        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex3f(x + width - nx1, y + ny0, 0);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex3f(x + width - nx1, y + height - ny1, 0);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex3f(x + width, y + height - ny1, 0);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex3f(x + width, y + ny0, 0);

        // Bottom Left
        u = spriteInfo.u();
        v = spriteInfo.v() + spriteInfo.height() - ny1;
        us = nx0;
        vs = ny1;

        u0 = u / atlasWidth;
        v0 = v / atlasHeight;
        u1 = (u + us) / atlasWidth;
        v1 = (v + vs) / atlasHeight;

        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex3f(x, y + height - ny1, 0);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex3f(x, y + height, 0);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex3f(x + nx0, y + height, 0);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex3f(x + nx0, y + height - ny1, 0);

        // Bottom Middle
        u = spriteInfo.u() + nx0;
        v = spriteInfo.v() + spriteInfo.height() - ny1;
        us = spriteInfo.width() - nx0 - nx1;
        vs = ny1;

        u0 = u / atlasWidth;
        v0 = v / atlasHeight;
        u1 = (u + us) / atlasWidth;
        v1 = (v + vs) / atlasHeight;

        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex3f(x + nx0, y + height - ny1, 0);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex3f(x + nx0, y + height, 0);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex3f(x + width - nx1, y + height, 0);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex3f(x + width - nx1, y + height - ny1, 0);

        // Bottom Right
        u = spriteInfo.u() + spriteInfo.width() - nx1;
        v = spriteInfo.v() + spriteInfo.height() - ny1;
        us = nx1;
        vs = ny1;

        u0 = u / atlasWidth;
        v0 = v / atlasHeight;
        u1 = (u + us) / atlasWidth;
        v1 = (v + vs) / atlasHeight;

        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex3f(x + width - nx1, y + height - ny1, 0);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex3f(x + width - nx1, y + height, 0);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex3f(x + width, y + height, 0);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex3f(x + width, y + height - ny1, 0);

        GL11.glEnd();
    }

    public void renderTextParticle(TextParticle textParticle) {
        this.client.font.draw(textParticle.msg, textParticle.x - textParticle.msg.length() * 4 + 1, textParticle.y - (int) (textParticle.zz) + 1, 0x000000);
        this.client.font.draw(textParticle.msg, textParticle.x - textParticle.msg.length() * 4, textParticle.y - (int) (textParticle.zz), textParticle.col);
    }

    public void renderZombie(ZombieEntity entity) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int walkDist = entity.data.get("walkDist");

        int flip1 = (walkDist >> 3) & 1;
        int flip2 = (walkDist >> 3) & 1;

        if (entity.dir > 1) {
            flip1 = 0;
            flip2 = ((walkDist >> 3) & 1);
            if (entity.dir == 3) {
                flip1 = 1;
            }
        }

        int x = entity.x - 8;
        int y = entity.y - 11;

        if (entity.isSwimming()) {
            if (entity.isSwimmingInLava()) {
                this.renderSprite("ui", this.client.tickCount / 8 % 2 == 0 ? "lava_overlay_1.png" : "lava_overlay_0.png", x, y + 3, 8, 8, false, false);
                this.renderSprite("ui", this.client.tickCount / 8 % 2 == 0 ? "lava_overlay_1.png" : "lava_overlay_0.png", x + 8, y + 3, 8, 8, true, false);
            } else {
                this.renderSprite("ui", this.client.tickCount / 8 % 2 == 0 ? "water_overlay_1.png" : "water_overlay_0.png", x, y + 3, 8, 8, false, false);
                this.renderSprite("ui", this.client.tickCount / 8 % 2 == 0 ? "water_overlay_1.png" : "water_overlay_0.png", x + 8, y + 3, 8, 8, true, false);
            }
        }

        String skin = entity.dir == 0 ? "zombie/level0/forward.png" : entity.dir == 1 ? "zombie/level0/backward.png" : flip2 == 1 ? "zombie/level0/side_moving.png" : "zombie/level0/side_still.png";

        this.renderSprite("entities", skin, x, y, 16, 8, 0, 0, flip1);
        if (!entity.isSwimming()) {
            this.renderSprite("entities", skin, x, y + 8, 16, 8, 0, 8, flip2);
        }
    }

    public void renderSpriteQuad(AtlasTexture atlas, String spritePath, int x, int y, int w, int h, int u, int v, int us, int vs) {
        this.renderSpriteQuad(atlas, spritePath, x, y, w, h, u, v, us, vs, 0);
    }

    public void renderSpriteQuad(AtlasTexture atlas, String spritePath, int x, int y, int w, int h, int u, int v, int us, int vs, int bits) {
        AtlasTexture.SpriteInfo spriteInfo = atlas.getSpriteInfo(spritePath);

        boolean mirrorX = (bits & 0x01) > 0;
        boolean mirrorY = (bits & 0x02) > 0;

        float atlasWidth = atlas.getWidth();
        float atlasHeight = atlas.getHeight();

        float u0 = (spriteInfo.u() + u) / atlasWidth;
        float v0 = (spriteInfo.v() + v) / atlasHeight;
        float u1 = (spriteInfo.u() + u + us) / atlasWidth;
        float v1 = (spriteInfo.v() + v + vs) / atlasHeight;

        if (mirrorX) {
            float temp = u0;
            u0 = u1;
            u1 = temp;
        }

        if (mirrorY) {
            float temp = v0;
            v0 = v1;
            v1 = temp;
        }

        GL11.glTexCoord2f(u0, v0);
        GL11.glVertex3f(x, y, 0);
        GL11.glTexCoord2f(u0, v1);
        GL11.glVertex3f(x, y + h, 0);
        GL11.glTexCoord2f(u1, v1);
        GL11.glVertex3f(x + w, y + h, 0);
        GL11.glTexCoord2f(u1, v0);
        GL11.glVertex3f(x + w, y, 0);
    }

    public void renderTile(AtlasTexture atlas, Tile tile, int x, int y) {
        switch (tile) {
            case RockTile rockTile -> {
                boolean u = this.client.level.getTile(x, y - 1) != tile;
                boolean d = this.client.level.getTile(x, y + 1) != tile;
                boolean l = this.client.level.getTile(x - 1, y) != tile;
                boolean r = this.client.level.getTile(x + 1, y) != tile;

                boolean ul = this.client.level.getTile(x - 1, y - 1) != tile;
                boolean dl = this.client.level.getTile(x - 1, y + 1) != tile;
                boolean ur = this.client.level.getTile(x + 1, y - 1) != tile;
                boolean dr = this.client.level.getTile(x + 1, y + 1) != tile;


                if (!u && !l) {
                    if (!ul) {
                        this.renderSpriteQuad(atlas, "rock.png", x * 16, y * 16, 8, 8, 0, 0, 8, 8);
                    } else {
                        this.renderSpriteQuad(atlas, "rock_corner.png", x * 16, y * 16, 8, 8, 0, 0, 8, 8, 3);
                    }
                } else {
                    this.renderSpriteQuad(atlas, "dirt_border_2.png", x * 16, y * 16, 8, 8, l ? 0 : 8, u ? 0 : 8, 8, 8);
                    this.renderSpriteQuad(atlas, "rock_connector.png", x * 16, y * 16, 8, 8, l ? 16 : 8, u ? 16 : 8, 8, 8, 3);
                }

                if (!u && !r) {
                    if (!ur) {
                        this.renderSpriteQuad(atlas, "rock.png", x * 16 + 8, y * 16, 8, 8, 8, 0, 8, 8);
                    } else {
                        this.renderSpriteQuad(atlas, "rock_corner.png", x * 16 + 8, y * 16, 8, 8, 8, 0, 8, 8, 3);
                    }
                } else {
                    this.renderSpriteQuad(atlas, "dirt_border_2.png", x * 16 + 8, y * 16, 8, 8, r ? 16 : 8, u ? 0 : 8, 8, 8);
                    this.renderSpriteQuad(atlas, "rock_connector.png", x * 16 + 8, y * 16, 8, 8, r ? 0 : 8, u ? 16 : 8, 8, 8, 3);
                }

                if (!d && !l) {
                    if (!dl) {
                        this.renderSpriteQuad(atlas, "rock.png", x * 16, y * 16 + 8, 8, 8, 0, 8, 8, 8);
                    } else {
                        this.renderSpriteQuad(atlas, "rock_corner.png", x * 16, y * 16 + 8, 8, 8, 0, 8, 8, 8, 3);
                    }
                } else {
                    this.renderSpriteQuad(atlas, "dirt_border_2.png", x * 16, y * 16 + 8, 8, 8, l ? 0 : 8, d ? 16 : 8, 8, 8);
                    this.renderSpriteQuad(atlas, "rock_connector.png", x * 16, y * 16 + 8, 8, 8, l ? 16 : 8, d ? 0 : 8, 8, 8, 3);
                }
                if (!d && !r) {
                    if (!dr) {
                        this.renderSpriteQuad(atlas, "rock.png", x * 16 + 8, y * 16 + 8, 8, 8, 8, 8, 8, 8);
                    } else {
                        this.renderSpriteQuad(atlas, "rock_corner.png", x * 16 + 8, y * 16 + 8, 8, 8, 8, 8, 8, 8, 3);
                    }
                } else {
                    this.renderSpriteQuad(atlas, "dirt_border_2.png", x * 16 + 8, y * 16 + 8, 8, 8, r ? 16 : 8, d ? 16 : 8, 8, 8);
                    this.renderSpriteQuad(atlas, "rock_connector.png", x * 16 + 8, y * 16 + 8, 8, 8, r ? 0 : 8, d ? 0 : 8, 8, 8, 3);
                }
            }
            case SandTile sandTile -> {
                boolean u = !this.client.level.getTile(x, y - 1).connectsToSand;
                boolean d = !this.client.level.getTile(x, y + 1).connectsToSand;
                boolean l = !this.client.level.getTile(x - 1, y).connectsToSand;
                boolean r = !this.client.level.getTile(x + 1, y).connectsToSand;

                if (!u && !l) {
                    this.renderSpriteQuad(atlas, "sand.png", x * 16, y * 16, 8, 8, 0, 0, 8, 8);
                } else {
                    this.renderSpriteQuad(atlas, "dirt_border.png", x * 16, y * 16, 8, 8, l ? 0 : 8, u ? 0 : 8, 8, 8);
                    this.renderSpriteQuad(atlas, "sand_connector.png", x * 16, y * 16, 8, 8, l ? 0 : 8, u ? 0 : 8, 8, 8);
                }

                if (!u && !r) {
                    this.renderSpriteQuad(atlas, "sand.png", x * 16 + 8, y * 16, 8, 8, 8, 0, 8, 8);
                } else {
                    this.renderSpriteQuad(atlas, "dirt_border.png", x * 16 + 8, y * 16, 8, 8, r ? 16 : 8, u ? 0 : 8, 8, 8);
                    this.renderSpriteQuad(atlas, "sand_connector.png", x * 16 + 8, y * 16, 8, 8, r ? 16 : 8, u ? 0 : 8, 8, 8);
                }

                if (!d && !l) {
                    this.renderSpriteQuad(atlas, "sand.png", x * 16, y * 16 + 8, 8, 8, 0, 8, 8, 8);
                } else {
                    this.renderSpriteQuad(atlas, "dirt_border.png", x * 16, y * 16 + 8, 8, 8, l ? 0 : 8, d ? 16 : 8, 8, 8);
                    this.renderSpriteQuad(atlas, "sand_connector.png", x * 16, y * 16 + 8, 8, 8, l ? 0 : 8, d ? 16 : 8, 8, 8);
                }
                if (!d && !r) {
                    this.renderSpriteQuad(atlas, "sand.png", x * 16 + 8, y * 16 + 8, 8, 8, 8, 8, 8, 8);
                } else {
                    this.renderSpriteQuad(atlas, "dirt_border.png", x * 16 + 8, y * 16 + 8, 8, 8, r ? 16 : 8, d ? 16 : 8, 8, 8);
                    this.renderSpriteQuad(atlas, "sand_connector.png", x * 16 + 8, y * 16 + 8, 8, 8, r ? 16 : 8, d ? 16 : 8, 8, 8);
                }
            }
            case DirtTile dirtTile -> this.renderSpriteQuad(atlas, "dirt.png", x * 16, y * 16, 16, 16, 0, 0, 16, 16);
            case WaterTile waterTile -> {
                this.wRandom.setSeed((this.client.tickCount + (x / 2 - y) * 4311L) / 10 * 54687121L + x * 3271612L + y * 3412987161L);

                boolean u = !this.client.level.getTile(x, y - 1).connectsToWater;
                boolean d = !this.client.level.getTile(x, y + 1).connectsToWater;
                boolean l = !this.client.level.getTile(x - 1, y).connectsToWater;
                boolean r = !this.client.level.getTile(x + 1, y).connectsToWater;

                boolean su = u && this.client.level.getTile(x, y - 1).connectsToSand;
                boolean sd = d && this.client.level.getTile(x, y + 1).connectsToSand;
                boolean sl = l && this.client.level.getTile(x - 1, y).connectsToSand;
                boolean sr = r && this.client.level.getTile(x + 1, y).connectsToSand;

                if (!u && !l) {
                    this.renderSpriteQuad(atlas, "water.png", x * 16, y * 16, 8, 8, 0, 0, 8, 8, this.wRandom.nextInt(4));
                } else {
                    this.renderSpriteQuad(atlas, "water_in.png", x * 16, y * 16, 8, 8, l ? 0 : 8, u ? 0 : 8, 8, 8);

                    if (su || sl) {
                        this.renderSpriteQuad(atlas, "sand_border.png", x * 16, y * 16, 8, 8, l ? 0 : 8, u ? 0 : 8, 8, 8);
                    } else {
                        this.renderSpriteQuad(atlas, "dirt_border.png", x * 16, y * 16, 8, 8, l ? 0 : 8, u ? 0 : 8, 8, 8);
                    }
                }

                if (!u && !r) {
                    this.renderSpriteQuad(atlas, "water.png", x * 16 + 8, y * 16, 8, 8, 8, 0, 8, 8, this.wRandom.nextInt(4));
                } else {
                    this.renderSpriteQuad(atlas, "water_in.png", x * 16 + 8, y * 16, 8, 8, 8 + (r ? 8 : 0), u ? 0 : 8, 8, 8);

                    if (su || sr) {
                        this.renderSpriteQuad(atlas, "sand_border.png", x * 16 + 8, y * 16, 8, 8, r ? 16 : 8, u ? 0 : 8, 8, 8);
                    } else {
                        this.renderSpriteQuad(atlas, "dirt_border.png", x * 16 + 8, y * 16, 8, 8, r ? 16 : 8, u ? 0 : 8, 8, 8);
                    }
                }

                if (!d && !l) {
                    this.renderSpriteQuad(atlas, "water.png", x * 16, y * 16 + 8, 8, 8, 0, 8, 8, 8, this.wRandom.nextInt(4));
                } else {
                    this.renderSpriteQuad(atlas, "water_in.png", x * 16, y * 16 + 8, 8, 8, l ? 0 : 8, d ? 16 : 8, 8, 8);

                    if (sd || sl) {
                        this.renderSpriteQuad(atlas, "sand_border.png", x * 16, y * 16 + 8, 8, 8, l ? 0 : 8, d ? 16 : 8, 8, 8);
                    } else {
                        this.renderSpriteQuad(atlas, "dirt_border.png", x * 16, y * 16 + 8, 8, 8, l ? 0 : 8, d ? 16 : 8, 8, 8);
                    }
                }
                if (!d && !r) {
                    this.renderSpriteQuad(atlas, "water.png", x * 16 + 8, y * 16 + 8, 8, 8, 8, 8, 8, 8, this.wRandom.nextInt(4));
                } else {
                    this.renderSpriteQuad(atlas, "water_in.png", x * 16 + 8, y * 16 + 8, 8, 8, r ? 16 : 8, d ? 16 : 8, 8, 8);

                    if (sd || sr) {
                        this.renderSpriteQuad(atlas, "sand_border.png", x * 16 + 8, y * 16 + 8, 8, 8, r ? 16 : 8, d ? 16 : 8, 8, 8);
                    } else {
                        this.renderSpriteQuad(atlas, "dirt_border.png", x * 16 + 8, y * 16 + 8, 8, 8, r ? 16 : 8, d ? 16 : 8, 8, 8);
                    }
                }
            }
            case CactusTile cactusTile -> {
                this.renderTile(atlas, Tiles.SAND, x, y);
                this.renderSpriteQuad(atlas, "cactus.png", x * 16, y * 16, 16, 16, 0, 0, 16, 16);
            }
            case TreeTile treeTile -> {
                this.renderTile(atlas, Tiles.GRASS, x, y);

                boolean u = this.client.level.getTile(x, y - 1) == tile;
                boolean l = this.client.level.getTile(x - 1, y) == tile;
                boolean r = this.client.level.getTile(x + 1, y) == tile;
                boolean d = this.client.level.getTile(x, y + 1) == tile;
                boolean ul = this.client.level.getTile(x - 1, y - 1) == tile;
                boolean ur = this.client.level.getTile(x + 1, y - 1) == tile;
                boolean dl = this.client.level.getTile(x - 1, y + 1) == tile;
                boolean dr = this.client.level.getTile(x + 1, y + 1) == tile;

                if (u && ul && l) {
                    this.renderSpriteQuad(atlas, "tree_0.png", x * 16, y * 16, 8, 8, 8, 8, 8, 8);
                } else {
                    this.renderSpriteQuad(atlas, "tree_0.png", x * 16, y * 16, 8, 8, 0, 0, 8, 8);
                }
                if (u && ur && r) {
                    this.renderSpriteQuad(atlas, "tree_1.png", x * 16 + 8, y * 16, 8, 8, 8, 0, 8, 8);
                } else {
                    this.renderSpriteQuad(atlas, "tree_0.png", x * 16 + 8, y * 16, 8, 8, 8, 0, 8, 8);
                }
                if (d && dl && l) {
                    this.renderSpriteQuad(atlas, "tree_1.png", x * 16, y * 16 + 8, 8, 8, 8, 0, 8, 8);
                } else {
                    this.renderSpriteQuad(atlas, "tree_0.png", x * 16, y * 16 + 8, 8, 8, 0, 8, 8, 8);
                }
                if (d && dr && r) {
                    this.renderSpriteQuad(atlas, "tree_0.png", x * 16 + 8, y * 16 + 8, 8, 8, 8, 8, 8, 8);
                } else {
                    this.renderSpriteQuad(atlas, "tree_1.png", x * 16 + 8, y * 16 + 8, 8, 8, 8, 8, 8, 8);
                }
            }
            case StairsTile stairsTile -> {
                this.renderSpriteQuad(atlas, "dirt.png", x * 16, y * 16, 16, 16, 0, 0, 16, 16);
                this.renderSpriteQuad(atlas, "stairs_down.png", x * 16, y * 16, 16, 16, 0, 0, 16, 16);
            }
            case GrassTile grassTile -> {
                boolean u = !this.client.level.getTile(x, y - 1).connectsToGrass;
                boolean d = !this.client.level.getTile(x, y + 1).connectsToGrass;
                boolean l = !this.client.level.getTile(x - 1, y).connectsToGrass;
                boolean r = !this.client.level.getTile(x + 1, y).connectsToGrass;

                if (!u && !l) {
                    this.renderSpriteQuad(atlas, "grass.png", x * 16, y * 16, 8, 8, 0, 0, 8, 8);
                } else {
                    this.renderSpriteQuad(atlas, "dirt_border.png", x * 16, y * 16, 8, 8, l ? 0 : 8, u ? 0 : 8, 8, 8);
                    this.renderSpriteQuad(atlas, "grass_connector.png", x * 16, y * 16, 8, 8, l ? 0 : 8, u ? 0 : 8, 8, 8);
                }

                if (!u && !r) {
                    this.renderSpriteQuad(atlas, "grass.png", x * 16 + 8, y * 16, 8, 8, 8, 0, 8, 8);
                } else {
                    this.renderSpriteQuad(atlas, "dirt_border.png", x * 16 + 8, y * 16, 8, 8, r ? 16 : 8, u ? 0 : 8, 8, 8);
                    this.renderSpriteQuad(atlas, "grass_connector.png", x * 16 + 8, y * 16, 8, 8, r ? 16 : 8, u ? 0 : 8, 8, 8);
                }

                if (!d && !l) {
                    this.renderSpriteQuad(atlas, "grass.png", x * 16, y * 16 + 8, 8, 8, 0, 8, 8, 8);
                } else {
                    this.renderSpriteQuad(atlas, "dirt_border.png", x * 16, y * 16 + 8, 8, 8, l ? 0 : 8, d ? 16 : 8, 8, 8);
                    this.renderSpriteQuad(atlas, "grass_connector.png", x * 16, y * 16 + 8, 8, 8, l ? 0 : 8, d ? 16 : 8, 8, 8);
                }
                if (!d && !r) {
                    this.renderSpriteQuad(atlas, "grass.png", x * 16 + 8, y * 16 + 8, 8, 8, 8, 8, 8, 8);
                } else {
                    this.renderSpriteQuad(atlas, "dirt_border.png", x * 16 + 8, y * 16 + 8, 8, 8, r ? 16 : 8, d ? 16 : 8, 8, 8);
                    this.renderSpriteQuad(atlas, "grass_connector.png", x * 16 + 8, y * 16 + 8, 8, 8, r ? 16 : 8, d ? 16 : 8, 8, 8);
                }
            }
            case null, default -> this.renderSpriteQuad(atlas, "grass.png", x * 16, y * 16, 16, 16, 0, 0, 16, 16);
        }
    }

    public void renderSlime(SlimeEntity entity) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int x = entity.x - 8;
        int y = entity.y - 11;

        int jumpTime = entity.data.get("jumpTime");

        if (jumpTime > 0) {
            y -= 4;
            this.renderSprite("entities", "slime/level0/jumping.png", x, y, 16, 16, entity.dir == 3, false);
        } else {
            this.renderSprite("entities", "slime/level0/still.png", x, y, 16, 16, entity.dir == 3, false);
        }
    }

    public void renderItemEntity(ItemEntity entity) {
        int time = entity.data.get("time");
        int lifeTime = entity.data.get("lifeTime");

        if (time >= lifeTime - 6 * 20) {
            if (time / 6 % 2 == 0) return;
        }

        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        this.renderItem(entity.stack.getItem(), entity.x - 4, entity.y - 4);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.renderItem(entity.stack.getItem(), entity.x - 4, entity.y - 4 - (int) (entity.zz));
    }

    public void renderPlayer(PlayerEntity entity) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int flip1 = (entity.walkDist >> 3) & 1;
        int flip2 = (entity.walkDist >> 3) & 1;

        if (entity.dir > 1) {
            flip1 = 0;
            flip2 = ((entity.walkDist >> 3) & 1);
            if (entity.dir == 3) {
                flip1 = 1;
            }
        }

        int x = entity.x - 8;
        int y = entity.y - 11;

        if (entity.isSwimming()) {
            if (entity.isSwimmingInLava()) {
                this.renderSprite("ui", this.client.tickCount / 8 % 2 == 0 ? "lava_overlay_1.png" : "lava_overlay_0.png", x, y + 3, 8, 8, false, false);
                this.renderSprite("ui", this.client.tickCount / 8 % 2 == 0 ? "lava_overlay_1.png" : "lava_overlay_0.png", x + 8, y + 3, 8, 8, true, false);
            } else {
                this.renderSprite("ui", this.client.tickCount / 8 % 2 == 0 ? "water_overlay_1.png" : "water_overlay_0.png", x, y + 3, 8, 8, false, false);
                this.renderSprite("ui", this.client.tickCount / 8 % 2 == 0 ? "water_overlay_1.png" : "water_overlay_0.png", x + 8, y + 3, 8, 8, true, false);
            }
        }

        String skin = entity.dir == 0 ? "player/forward.png" : entity.dir == 1 ? "player/backward.png" : flip2 == 1 ? "player/side_moving.png" : "player/side_still.png";

        this.renderSprite("entities", skin, x, y, 16, 8, 0, 0, flip1);
        if (!entity.isSwimming()) {
            this.renderSprite("entities", skin, x, y + 8, 16, 8, 0, 8, flip2);
        }
    }

    static class GCMemoryAllocationRateCalculator {
        private final List<GarbageCollectorMXBean> collectors;
        private long lastCalculated = 0L;
        private long allocationRate = 0L;
        private long allocatedBytes = -1L;
        private long collectionCount = -1L;

        public GCMemoryAllocationRateCalculator() {
            this.collectors = ManagementFactory.getGarbageCollectorMXBeans();
        }

        public long calculateAllocationRate() {
            long now = System.currentTimeMillis();
            if (now - this.lastCalculated < 500L) {
                return this.allocationRate;
            }

            long allocatedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long collectionCount = this.getTotalCollectionCount();

            if (this.lastCalculated != 0L && collectionCount == this.collectionCount) {
                double d = 1000.0 / (double)(now - this.lastCalculated);
                long o = allocatedBytes - this.allocatedBytes;
                this.allocationRate = Math.round((double)o * d);
            }

            this.lastCalculated = now;
            this.allocatedBytes = allocatedBytes;
            this.collectionCount = collectionCount;
            return this.allocationRate;
        }

        private long getTotalCollectionCount() {
            long count = 0L;
            for (var collector : this.collectors) {
                count += collector.getCollectionCount();
            }
            return count;
        }
    }

    public void renderItem(Item item, int x, int y) {
        switch (item.getNumericId()) {
            case 0 -> this.renderSprite("items", "wood_shovel.png", x, y, 8, 8, 0);
            case 1 -> this.renderSprite("items", "rock_shovel.png", x, y, 8, 8, 0);
            case 2 -> this.renderSprite("items", "iron_shovel.png", x, y, 8, 8, 0);
            case 3 -> this.renderSprite("items", "gold_shovel.png", x, y, 8, 8, 0);
            case 4 -> this.renderSprite("items", "gem_shovel.png", x, y, 8, 8, 0);
            case 5 -> this.renderSprite("items", "wood_hoe.png", x, y, 8, 8, 0);
            case 6 -> this.renderSprite("items", "rock_hoe.png", x, y, 8, 8, 0);
            case 7 -> this.renderSprite("items", "iron_hoe.png", x, y, 8, 8, 0);
            case 8 -> this.renderSprite("items", "gold_hoe.png", x, y, 8, 8, 0);
            case 9 -> this.renderSprite("items", "gem_hoe.png", x, y, 8, 8, 0);
            case 10 -> this.renderSprite("items", "wood_pickaxe.png", x, y, 8, 8, 0);
            case 11 -> this.renderSprite("items", "rock_pickaxe.png", x, y, 8, 8, 0);
            case 12 -> this.renderSprite("items", "iron_pickaxe.png", x, y, 8, 8, 0);
            case 13 -> this.renderSprite("items", "gold_pickaxe.png", x, y, 8, 8, 0);
            case 14 -> this.renderSprite("items", "gem_pickaxe.png", x, y, 8, 8, 0);
            case 15 -> this.renderSprite("items", "wood_axe.png", x, y, 8, 8, 0);
            case 16 -> this.renderSprite("items", "rock_axe.png", x, y, 8, 8, 0);
            case 17 -> this.renderSprite("items", "iron_axe.png", x, y, 8, 8, 0);
            case 18 -> this.renderSprite("items", "gold_axe.png", x, y, 8, 8, 0);
            case 19 -> this.renderSprite("items", "gem_axe.png", x, y, 8, 8, 0);
            case 20 -> this.renderSprite("items", "wood_sword.png", x, y, 8, 8, 0);
            case 21 -> this.renderSprite("items", "rock_sword.png", x, y, 8, 8, 0);
            case 22 -> this.renderSprite("items", "iron_sword.png", x, y, 8, 8, 0);
            case 23 -> this.renderSprite("items", "gold_sword.png", x, y, 8, 8, 0);
            case 24 -> this.renderSprite("items", "gem_sword.png", x, y, 8, 8, 0);
            case 25 -> this.renderSprite("items", "power_glove.png", x, y, 8, 8, 0);
            case 26 -> this.renderSprite("items", "wood.png", x, y, 8, 8, 0);
            case 27 -> this.renderSprite("items", "stone.png", x, y, 8, 8, 0);
            case 28 -> this.renderSprite("items", "flower.png", x, y, 8, 8, 0);
            case 29 -> this.renderSprite("items", "acorn.png", x, y, 8, 8, 0);
            case 30 -> this.renderSprite("items", "dirt.png", x, y, 8, 8, 0);
            case 31 -> this.renderSprite("items", "sand.png", x, y, 8, 8, 0);
            case 32 -> this.renderSprite("items", "cactus_flower.png", x, y, 8, 8, 0);
            case 33 -> this.renderSprite("items", "seeds.png", x, y, 8, 8, 0);
            case 34 -> this.renderSprite("items", "wheat.png", x, y, 8, 8, 0);
            case 35 -> this.renderSprite("items", "bread.png", x, y, 8, 8, 0);
            case 36 -> this.renderSprite("items", "apple.png", x, y, 8, 8, 0);
            case 37 -> this.renderSprite("items", "coal.png", x, y, 8, 8, 0);
            case 38 -> this.renderSprite("items", "iron_ore.png", x, y, 8, 8, 0);
            case 39 -> this.renderSprite("items", "gold_ore.png", x, y, 8, 8, 0);
            case 40 -> this.renderSprite("items", "iron_ingot.png", x, y, 8, 8, 0);
            case 41 -> this.renderSprite("items", "gold_ingot.png", x, y, 8, 8, 0);
            case 42 -> this.renderSprite("items", "slime.png", x, y, 8, 8, 0);
            case 43 -> this.renderSprite("items", "glass.png", x, y, 8, 8, 0);
            case 44 -> this.renderSprite("items", "cloth.png", x, y, 8, 8, 0);
            case 45 -> this.renderSprite("items", "cloud.png", x, y, 8, 8, 0);
            case 46 -> this.renderSprite("items", "gem.png", x, y, 8, 8, 0);
            case 47 -> this.renderSprite("items", "potato.png", x, y, 8, 8, 0);
            case 48 -> this.renderSprite("items", "baked_potato.png", x, y, 8, 8, 0);
        }
    }
}
