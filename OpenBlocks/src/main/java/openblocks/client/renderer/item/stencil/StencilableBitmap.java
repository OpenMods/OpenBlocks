package openblocks.client.renderer.item.stencil;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import openblocks.common.IStencilPattern;
import org.apache.commons.lang3.tuple.Pair;

public class StencilableBitmap {

	private static class Rectangle {
		private final float left;
		private final float right;
		private final float top;
		private final float bottom;

		private final int bitIndex;

		public Rectangle(float left, float right, float top, float bottom, int bitIndex) {
			Preconditions.checkArgument(right > left);
			Preconditions.checkArgument(bottom > top); // y = 0 on top
			this.left = left;
			this.right = right;
			this.top = top;
			this.bottom = bottom;
			this.bitIndex = bitIndex;
		}

		public Pair<Rectangle, Rectangle> splitHorizontal(int x) {
			Preconditions.checkState(x > this.left && x <= this.right);

			if (x == this.right)
				return Pair.of(this, null);

			final Rectangle left = new Rectangle(this.left, x, top, bottom, bitIndex);
			final Rectangle right = new Rectangle(x, this.right, top, bottom, bitIndex);
			return Pair.of(left, right);
		}

		public Pair<Rectangle, Rectangle> splitVertical(int y) {
			Preconditions.checkState(y > this.top && y <= this.bottom);
			if (y == this.bottom)
				return Pair.of(this, null);

			final Rectangle top = new Rectangle(this.left, this.right, this.top, y, bitIndex);
			final Rectangle bottom = new Rectangle(this.left, this.right, y, this.bottom, bitIndex);
			return Pair.of(top, bottom);
		}

		public MaskWithWeight create() {
			final float width = right - left;
			final float height = bottom - top;

			final float area = width * height;

			return new MaskWithWeight(area, bitIndex);
		}

		public int targetX() {
			final int result = (int)left;
			Preconditions.checkState(right <= result + 1, "Rectangle not contained in unit square");
			return result;
		}

		public int targetY() {
			final int result = (int)top;
			Preconditions.checkState(bottom <= result + 1, "Rectangle not contained in unit square");
			return result;
		}
	}

	private static class MaskWithWeight {
		private final float weight;

		private final int bitIndex;

		public MaskWithWeight(float weight, int bitIndex) {
			this.weight = weight;
			this.bitIndex = bitIndex;
		}

		public int convert(IStencilPattern stencil, int background) {
			return stencil.mix(bitIndex, 0, background);
		}
	}

	public static class PixelCalculator {
		private final List<MaskWithWeight> modifiers;

		private final float totalWeight;

		private final int originalColor;

		public PixelCalculator(int originalColor, List<MaskWithWeight> inputs) {
			Preconditions.checkState(!inputs.isEmpty());
			this.modifiers = ImmutableList.copyOf(inputs);

			float totalWeight = 0;
			for (MaskWithWeight w : inputs)
				totalWeight += w.weight;

			this.totalWeight = totalWeight;
			this.originalColor = originalColor;
		}

		private static float extractChannel(int input, int bits) {
			return (input >> bits) & 0xFF;
		}

		private static int convertChannel(float input, int bits) {
			final int value = Math.min((int)input, 0xFF);
			return value << bits;
		}

		public int apply(IStencilPattern stencil) {
			float c1 = 0;
			float c2 = 0;
			float c3 = 0;
			float c4 = 0;

			for (MaskWithWeight m : modifiers) {
				final int maskInput = m.convert(stencil, originalColor);
				c1 += extractChannel(maskInput, 0) * m.weight;
				c2 += extractChannel(maskInput, 8) * m.weight;
				c3 += extractChannel(maskInput, 16) * m.weight;
				c4 += extractChannel(maskInput, 24) * m.weight;
			}

			c1 /= totalWeight;
			c2 /= totalWeight;
			c3 /= totalWeight;
			c4 /= totalWeight;

			return convertChannel(c1, 0) | convertChannel(c2, 8) | convertChannel(c3, 16) | convertChannel(c4, 24);
		}
	}

	private static final int STENCIL_WIDTH = 16;

	private static final int STENCIL_HEIGHT = 16;

	private final PixelCalculator[] pixels;

	public StencilableBitmap(int[] image, int width) {
		final int totalPixels = image.length;
		final int height = totalPixels / width;
		Preconditions.checkState(width * height == totalPixels, "Invalid pixel count");
		final List<Rectangle> scaledMask = generateScaledMask(width, height);
		final List<Rectangle> horizontalMaskStrips = splitHorizontal(scaledMask, width);
		final List<Rectangle> fullySplitMask = splitVertical(horizontalMaskStrips, height);

		List<List<MaskWithWeight>> collectedMasks = Lists.newArrayList();
		for (int i = 0; i < totalPixels; i++)
			collectedMasks.add(Lists.newArrayList());

		for (Rectangle rect : fullySplitMask) {
			final int index = rect.targetY() * width + rect.targetX();
			collectedMasks.get(index).add(rect.create());
		}

		this.pixels = new PixelCalculator[totalPixels];
		for (int i = 0; i < totalPixels; i++)
			this.pixels[i] = new PixelCalculator(image[i], collectedMasks.get(i));
	}

	private static List<Rectangle> generateScaledMask(int width, int height) {
		final List<Rectangle> result = Lists.newArrayListWithCapacity(16);

		final float deltaX = width / (float)STENCIL_WIDTH;
		final float deltaY = height / (float)STENCIL_HEIGHT;
		int index = 0;
		for (int row = 0; row < STENCIL_HEIGHT; row++) {
			for (int column = 0; column < STENCIL_WIDTH; column++) {
				final float x = deltaX * column;
				final float y = deltaY * row;
				result.add(new Rectangle(x, x + deltaX, y, y + deltaY, index++));
			}
		}

		return result;
	}

	private static List<Rectangle> splitHorizontal(List<Rectangle> input, int width) {
		final List<Rectangle> result = Lists.newArrayList();

		for (Rectangle r : input) {
			Rectangle reminder = r;
			for (int x = (int)r.left; x < (int)r.right; x++) {
				Preconditions.checkState(reminder != null, "Off by one, yell at developer");
				final Pair<Rectangle, Rectangle> split = reminder.splitHorizontal(x + 1);
				result.add(split.getLeft());
				reminder = split.getRight();
			}
			if (reminder != null)
				result.add(reminder);
		}

		return result;
	}

	private static List<Rectangle> splitVertical(List<Rectangle> input, int height) {
		final List<Rectangle> result = Lists.newArrayList();

		for (Rectangle r : input) {
			Rectangle reminder = r;
			for (int y = (int)r.top; y < (int)r.bottom; y++) {
				Preconditions.checkState(reminder != null, "Off by one, yell at developer");
				final Pair<Rectangle, Rectangle> split = reminder.splitVertical(y + 1);
				result.add(split.getLeft());
				reminder = split.getRight();
			}
			if (reminder != null)
				result.add(reminder);
		}

		return result;
	}

	public int[] apply(IStencilPattern stencil) {
		Preconditions.checkArgument(stencil.width() == STENCIL_WIDTH, "Invalid stencil width, expected 16, got %s", stencil.width());
		Preconditions.checkArgument(stencil.height() == STENCIL_HEIGHT, "Invalid stencil height, expected 16, got %s", stencil.height());

		final int[] result = new int[pixels.length];

		for (int i = 0; i < pixels.length; i++)
			result[i] = pixels[i].apply(stencil);

		return result;
	}

}
