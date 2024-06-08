package me.kalmemarq.minicraft.resource;

import me.kalmemarq.minicraft.IOUtils;

public class DefaultResourcePack extends DirectoryResourcePack {
	public static final DefaultResourcePack INSTANCE = new DefaultResourcePack();

	public DefaultResourcePack() {
		super(IOUtils.getResourcesPath());
	}
}
