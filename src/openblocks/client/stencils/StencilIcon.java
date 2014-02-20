package openblocks.client.stencils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.Resource;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Preconditions;

public abstract class StencilIcon extends TextureAtlasSprite {

	private static final int STENCIL_SIZE = 16;

	private static final ResourceLocation COVER_BASE = new ResourceLocation("openblocks", "textures/blocks/stencil_cover.png");

	public static class CoverIcon extends StencilIcon {
		private static int[] coverBase;
		private static int coverWidth;
		private static int coverHeight;

		public CoverIcon(String id) {
			super(id);
		}

		@Override
		public boolean load(ResourceManager manager, ResourceLocation location) throws IOException {
			if (coverBase == null) {
				Resource res = manager.getResource(COVER_BASE);
				InputStream inputstream = res.getInputStream();
				BufferedImage bufferedimage = ImageIO.read(inputstream);
				coverHeight = bufferedimage.getHeight();
				coverWidth = bufferedimage.getWidth();
				Preconditions.checkState(coverHeight == coverWidth, "stencil_cover.png must be square");
				coverBase = new int[coverWidth * coverHeight];
				bufferedimage.getRGB(0, 0, coverWidth, coverHeight, coverBase, 0, coverWidth);
			}

			this.bytes = coverBase;
			this.width = coverWidth;
			this.height = coverHeight;
			return true;
		}

		@Override
		public void loadBits(BigInteger bits) {
			bytes = coverBase.clone();

			final int xRatio = coverWidth / STENCIL_SIZE;
			final int yRatio = coverHeight / STENCIL_SIZE;

			for (int x = 0; x < coverWidth; x++)
				for (int y = 0; y < coverHeight; y++) {
					final int texIndex = y * coverWidth + x;
					final int bitsIndex = (y / yRatio) * STENCIL_SIZE + (x / xRatio);
					if (bits.testBit(bitsIndex)) bytes[texIndex] = 0x00000000;
				}

			pushBitsToTexture();
		}

		public static void clearBaseData() {
			coverBase = null;
		}
	}

	public static class InvertedIcon extends StencilIcon {
		private static final int PIXEL_COUNT = STENCIL_SIZE * STENCIL_SIZE;
		private static final int[] blankBytes = new int[PIXEL_COUNT];

		public InvertedIcon(String id) {
			super(id);
		}

		@Override
		public boolean load(ResourceManager manager, ResourceLocation location) throws IOException {
			this.bytes = blankBytes;
			this.width = this.height = STENCIL_SIZE;
			return true;
		}

		@Override
		public void loadBits(BigInteger bits) {
			bytes = new int[PIXEL_COUNT];
			for (int i = 0; i < PIXEL_COUNT; i++)
				if (bits.testBit(i)) bytes[i] = 0xFFFFFFFF;

			pushBitsToTexture();
		}
	}

	protected int[] bytes;

	public StencilIcon(String id) {
		super(id);
	}

	@Override
	public int[] getFrameTextureData(int par1) {
		return bytes;
	}

	@Override
	public int getFrameCount() {
		return 0;
	}

	@Override
	public boolean hasAnimationMetadata() {
		return false;
	}

	public abstract void loadBits(BigInteger bits);

	protected void pushBitsToTexture() {
		TextureManager manager = Minecraft.getMinecraft().getTextureManager();
		manager.bindTexture(TextureMap.locationBlocksTexture);

		TextureUtil.uploadTextureSub(bytes, width, height, originX, originY, false, false);
	}

	@Override
	public void loadSprite(Resource par1Resource) {}

	@Override
	public void updateAnimation() {}
}
