package openblocks.common;

import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;

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

	public final String iconName;
	public final ResourceLocation blockIcon;
	public final ResourceLocation coverBlockIcon;

	private Stencil(String iconName) {
		this.iconName = iconName;
		this.blockIcon = OpenBlocks.location("stencil_" + iconName);
		this.coverBlockIcon = OpenBlocks.location("openblocks:stencilcover_" + iconName);
	}

	public static final Stencil[] VALUES = values();
}
