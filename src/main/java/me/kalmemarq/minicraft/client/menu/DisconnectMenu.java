package me.kalmemarq.minicraft.client.menu;

import me.kalmemarq.minicraft.client.util.Translation;
import org.lwjgl.glfw.GLFW;

public class DisconnectMenu extends Menu {
	private final String reason;

	public DisconnectMenu(String reason) {
		this.reason = reason;
	}

	@Override
	public void keyPressed(int key) {
		if (key == GLFW.GLFW_KEY_ESCAPE) {
			this.client.setMenu(new TitleMenu());
		}
	}

	@Override
	public void render() {
		this.client.font.draw(Translation.translate("minicraft.menu.disconnected"), 2 * 8 + 4, 8, 0xFFFFFF);
		this.client.font.drawWithMaxWidth(this.reason, 2 * 8 + 4, 16, 0xFFFFFF, 15);
	}
}
