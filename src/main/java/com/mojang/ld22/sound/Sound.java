package com.mojang.ld22.sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	public static final Sound playerHurt = new Sound("/playerhurt.wav");
	public static final Sound playerDeath = new Sound("/death.wav");
	public static final Sound monsterHurt = new Sound("/monsterhurt.wav");
	public static final Sound test = new Sound("/test.wav");
	public static final Sound pickup = new Sound("/pickup.wav");
	public static final Sound bossdeath = new Sound("/bossdeath.wav");
	public static final Sound craft = new Sound("/craft.wav");

	private Clip clip;
	private final String path;

	private Sound(String name) {
		this.path = name;
	}

	public String getPath() {
		return this.path;
	}

	public void play() {
		if (this.clip == null) {
			try {
				this.clip = AudioSystem.getClip();
				AudioInputStream inputStream = AudioSystem.getAudioInputStream(Sound.class.getResource(this.path));
				this.clip.open(inputStream);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		try {
			new Thread(() -> {
				Sound.this.clip.stop();
				Sound.this.clip.setFramePosition(0);
				Sound.this.clip.start();
			}).start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
