package openblocks.client.renderer.block.canvas;

import openmods.geometry.Permutation2d;

public enum TextureOrientation {
	R0 {
		@Override
		protected Permutation2d setup(Permutation2d input) {
			return input;
		}
	},
	R90 {
		@Override
		protected Permutation2d setup(Permutation2d input) {
			return input.rotateCW();
		}
	},
	R180 {
		@Override
		protected Permutation2d setup(Permutation2d input) {
			return input.reverse();
		}
	},
	R270 {
		@Override
		protected Permutation2d setup(Permutation2d input) {
			return input.rotateCCW();
		}
	};

	protected abstract Permutation2d setup(Permutation2d input);

	TextureOrientation() {
		this.rotator16x16 = setup(Permutation2d.identity(16, 16));
	}

	private final Permutation2d rotator16x16;

	public int rotate16x16(int index) {
		return rotator16x16.apply(index);
	}

	private static final TextureOrientation[] VALUES = values();

	public TextureOrientation subtract(TextureOrientation baseRotation) {
		final int id = ordinal() - baseRotation.ordinal();
		return VALUES[id < 0? id + VALUES.length : id];
	}

	public int shift(int v) {
		final int id = v - ordinal();
		return id < 0? id + VALUES.length : id;
	}

	public TextureOrientation increment() {
		final int id = ordinal() + 1;
		return VALUES[id % VALUES.length];
	}

}