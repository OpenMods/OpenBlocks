package openblocks.common;

import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;

@Deprecated
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

	public final String name;
	public final ResourceLocation blockIcon;
	public final ResourceLocation coverBlockIcon;

	private Stencil(String name) {
		this.name = name;
		this.blockIcon = OpenBlocks.location("blocks/stencil_" + name);
		this.coverBlockIcon = OpenBlocks.location("blocks/stencilcover_" + name);
	}

	public static final Stencil[] VALUES = values();
}
