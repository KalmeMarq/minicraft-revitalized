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
import me.kalmemarq.minicraft.network.packet.MessagePacket;
import org.lwjgl.glfw.GLFW;

public class ChatMenu extends Menu {
	private static final int MAX_VISIBLE_LINES = 8;
	private static final int MIN_VISIBLE_LINES = 4;
	private String input = "";
	private DumbStatus amIVeryDumbStatus = DumbStatus.NOT;

	@Override
	public void init(Client client) {
		super.init(client);
	}

	@Override
	public void charTyped(int codepoint) {
		if (this.amIVeryDumbStatus != DumbStatus.NOT) this.input += Character.toString(codepoint);
		else this.amIVeryDumbStatus = DumbStatus.VERY_MUCH;
	}

	@Override
	public void keyPressed(int key) {
		if (key == GLFW.GLFW_KEY_ESCAPE) {
			this.client.setMenu(null);
			this.client.soundManager.play("/sounds/craft.wav", 1.0f, 1.0f);
		} else if (key == GLFW.GLFW_KEY_ENTER && !this.input.trim().isEmpty()) {
			this.client.player.getNetworkHandler().send(new MessagePacket(this.input));
			this.input = "";
		} else if (key == GLFW.GLFW_KEY_BACKSPACE && !this.input.isEmpty()) {
			this.input = this.input.substring(0, this.input.length() - 1);
		}
	}

	@Override
	public void render() {
		int size = Math.clamp(this.client.messages.size(), MIN_VISIBLE_LINES, MAX_VISIBLE_LINES);

		this.client.renderer.renderSpriteNineslice("textures/ui", "frame.png", 0, this.getHeight() - 8 - (size * 8) - 8 - 8, this.getWidth(), 8 + (size * 8), 8, 8, 8, 8);
		this.client.renderer.renderSpriteNineslice("textures/ui", "frame.png", 0, this.getHeight() - 16, this.getWidth(), 16, 8, 8, 8, 8);

		for (int i = 0; i < this.client.messages.size(); ++i) {
			if (i >= size) break;

			this.client.font.draw(this.client.messages.get(this.client.messages.size() - 1 - i), 8, this.getHeight() - 12 - 16 - (i * 8), 0xFFFFFF);
		}

		this.client.font.draw(this.input, 8, this.getHeight() - 12, 0xFFFFFF);
	}

	enum DumbStatus {
		NOT,
		VERY_MUCH
	}
}
