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

package me.kalmemarq.minicraft.client.menu.ui;

import com.fasterxml.jackson.databind.JsonNode;
import me.kalmemarq.minicraft.client.Client;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class UIGradient extends UIElement {
	private int startColor = 0xFF_FFFFFF;
	private int endColor = 0xFF_FFFFFF;
	private int dir;

	@Override
	public void init(Client client, Map<String, Observable<?>> menuBindingsMap, JsonNode node) {
		super.init(client, menuBindingsMap, node);

		if (node.has("start_color") && node.get("start_color").isArray()) {
			JsonNode n = node.get("start_color");
			this.startColor = (n.has(3) ? n.get(3).asInt() << 24 : 255 << 24) | n.get(0).asInt() << 16 | n.get(1).asInt() << 8 | n.get(2).asInt();
		}

		if (node.has("end_color") && node.get("end_color").isArray()) {
			JsonNode n = node.get("end_color");
			this.endColor = (n.has(3) ? n.get(3).asInt() << 24 : 255 << 24) | n.get(0).asInt() << 16 | n.get(1).asInt() << 8 | n.get(2).asInt();
		}

		if (node.has("direction")) {
			this.dir = node.get("direction").textValue().equals("horizontal") ? 1 : 0;
		}
	}

	@Override
	public void render() {
		if (!this.visible) return;

		float sa = (this.startColor >> 24 & 0xFF) / 255.0f;
		float sr = (this.startColor >> 16 & 0xFF) / 255.0f;
		float sg = (this.startColor >> 8 & 0xFF) / 255.0f;
		float sb = (this.startColor & 0xFF) / 255.0f;

		float ea = (this.endColor >> 24 & 0xFF) / 255.0f;
		float er = (this.endColor >> 16 & 0xFF) / 255.0f;
		float eg = (this.endColor >> 8 & 0xFF) / 255.0f;
		float eb = (this.endColor & 0xFF) / 255.0f;

		this.client.textureManager.bind("White");

		GL11.glBegin(GL11.GL_QUADS);
		if (this.dir == 0) {
			GL11.glColor4f(sr, sg, sb, sa);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(this.offsetX, this.offsetY, 0);
			GL11.glColor4f(er, eg, eb, ea);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(this.offsetX, this.offsetY + this.height, 0);
			GL11.glColor4f(er, eg, eb, ea);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(this.offsetX + this.width, this.offsetY + this.height, 0);
			GL11.glColor4f(sr, sg, sb, sa);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(this.offsetX + this.width, this.offsetY, 0);
		} else {
			GL11.glColor4f(sr, sg, sb, sa);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex3f(this.offsetX, this.offsetY, 0);
			GL11.glColor4f(sr, sg, sb, sa);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex3f(this.offsetX, this.offsetY + this.height, 0);
			GL11.glColor4f(er, eg, eb, ea);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex3f(this.offsetX + this.width, this.offsetY + this.height, 0);
			GL11.glColor4f(er, eg, eb, ea);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex3f(this.offsetX + this.width, this.offsetY, 0);
		}
		GL11.glEnd();
		GL11.glColor4f(1f, 1f, 1f, 1f);

		for (UIElement element : this.controls) {
			element.render();
		}
	}
}
