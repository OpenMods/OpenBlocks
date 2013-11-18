package openblocks.common;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	STORAGE("storage"),
	HEART("heart"),
	HEART2("heart2"),
	MUSIC("music"),
	BALLOON("balloon");

	private Icon blockIcon;
	private Icon coverBlockIcon;
	private String iconName;

	Stencil(String iconName) {
		this.iconName = iconName;
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IconRegister register) {
		blockIcon = register.registerIcon("openblocks:stencil_" + iconName);
		coverBlockIcon = register.registerIcon("openblocks:stencilcover_" + iconName);
	}

	@SideOnly(Side.CLIENT)
	public Icon getCoverBlockIcon() {
		return coverBlockIcon;
	}

	@SideOnly(Side.CLIENT)
	public Icon getBlockIcon() {
		return blockIcon;
	}
}
