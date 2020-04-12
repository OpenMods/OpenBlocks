package openblocks.client.renderer.block.canvas;

import openblocks.common.IStencilPattern;

public class CanvasLayer {

	public static final int TEXTURE_WIDTH = 16;

	public static final int TEXTURE_HEIGHT = 16;

	public final IStencilPattern pattern;

	public final int color;

	public final TextureOrientation orientation;

	public CanvasLayer(IStencilPattern pattern, int color, TextureOrientation orientation) {
		this.pattern = pattern;
		this.color = color;
		this.orientation = orientation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + color;
		result = prime * result + orientation.hashCode();
		result = prime * result + pattern.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj instanceof CanvasLayer) {
			final CanvasLayer other = (CanvasLayer)obj;
			return this.color == other.color &&
					this.orientation == other.orientation &&
					this.pattern.equals(other.pattern);
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%s[%s:%08X]", pattern, orientation, color);
	}
}
