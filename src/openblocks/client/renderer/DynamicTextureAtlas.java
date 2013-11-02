package openblocks.client.renderer;

import java.util.List;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import openblocks.Log;
import openblocks.utils.ByteUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class DynamicTextureAtlas {

	public class AtlasCell {
		private int[] pixels;

		public void setPixels(int[] pixels) {
			this.pixels = pixels;
		}

		public float minU;
		public float minV;
		public float maxU;
		public float maxV;
	}

	private final List<AtlasCell> cells = Lists.newArrayList();

	public final int cellSize;

	private final TextureManager manager;
	private final DisposableDynamicTexture textureWrapper;
	private final ResourceLocation textureLocation;

	public DynamicTextureAtlas(TextureManager manager, int cellSize) {
		Preconditions.checkArgument(ByteUtils.isPowerOfTwo(cellSize), "NO POWER!");
		this.manager = manager;
		this.cellSize = cellSize;
		this.textureWrapper = new DisposableDynamicTexture();
		this.textureLocation = textureWrapper.register(manager, "dyn-atlas");
	}

	public void bind() {
		manager.bindTexture(textureLocation);
	}

	public AtlasCell allocateCell() {
		AtlasCell cell = new AtlasCell();
		cells.add(cell);
		return cell;
	}

	public void clearCells() {
		cells.clear();
	}

	public void compile() {
		int count = cells.size();
		Preconditions.checkState(count > 0, "No cells added");
		int side = (int)Math.ceil(Math.sqrt(count));
		int width = ByteUtils.nextPowerOf2(side);
		int area = width * width;
		int height = (count <= area / 2)? width / 2 : width;

		Log.info("count: %d, size: (%d,%d), area: %d, free space: %d", count, width, height, width * height, width * height - count);

		textureWrapper.resize(width * cellSize, height * cellSize);
		int[] buffer = textureWrapper.allocate();

		final int pixelsPerLine = width * cellSize;
		final int pixelsPerCellRow = pixelsPerLine * cellSize;

		final float cellU = 1.0f / width;
		final float cellV = 1.0f / height;

		for (int cell = 0; cell < count; cell++) {
			int cellX = cell % width;
			int cellY = cell / width;

			AtlasCell cellData = cells.get(cell);

			int cellStart = cellY * pixelsPerCellRow + cellX * cellSize;
			for (int row = 0; row < cellSize; row++) {
				int dstPos = cellStart + row * pixelsPerLine;
				int srcPos = row * cellSize;
				System.arraycopy(cellData.pixels, srcPos, buffer, dstPos, 64);
			}

			cellData.pixels = null;
			cellData.minU = (float)cellX / width;
			cellData.minV = (float)cellY / height;
			cellData.maxU = cellData.minU + cellU;
			cellData.maxV = cellData.minV + cellV;
		}

		textureWrapper.updateAndDeallocate();
	}
}
