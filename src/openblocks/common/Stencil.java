package openblocks.common;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;

public enum Stencil {
	
	CREEPER_FACE("creeperface"),
	BORDER("border"),
	STRIPES("stripes"),
	CORNER("corner"),
	CORNER2("corner2"),
	CORNER3("corner3"),
	HOLE("hole"),
	SPIRAL("spiral"),
	THICKSTRIPES("thickstripes"),
	SPLAT("splat"),
	STORAGE("storage");
	
	private Icon blockIcon;
	private Icon coverBlockIcon;
	private String iconName;
	
	Stencil(String iconName) {
		this.iconName = iconName;
	}

	public void registerBlockIcons(IconRegister register) {
		blockIcon = register.registerIcon("openblocks:stencil_"+iconName);
		coverBlockIcon = register.registerIcon("openblocks:stencilcover_"+iconName);
	}
	
	public Icon getCoverBlockIcon() {
		return coverBlockIcon;
	}
	
	public Icon getBlockIcon() {
		return blockIcon;
	}
}
