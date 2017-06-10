package openblocks.common;

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

	private Stencil(String name) {
		this.name = name;
	}

	public static final Stencil[] VALUES = values();
}
