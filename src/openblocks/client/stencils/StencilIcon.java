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

public abstract class StencilIcon extends TextureAtlasSprite {

	private static final int STENCIL_SIZE = 16;

	private static final ResourceLocation COVER_BASE = new ResourceLocation("openblocks", "textures/blocks/stencil_cover.png");

	public static class CoverIcon extends StencilIcon {
		private static int[] coverBase;

		public CoverIcon(String id) {
			super(id);
		}

		@Override
		public boolean load(ResourceManager manager, ResourceLocation location) throws IOException {
			if (coverBase == null) {
				Resource res = manager.getResource(COVER_BASE);
				InputStream inputstream = res.getInputStream();
				BufferedImage bufferedimage = ImageIO.read(inputstream);
				int height = bufferedimage.getHeight();
				int width = bufferedimage.getWidth();
				coverBase = new int[height * width];
				bufferedimage.getRGB(0, 0, width, height, coverBase, 0, width);
			}

			if (bytes == null) bytes = coverBase;
			return super.load(manager, location);
		}

		@Override
		public void loadBits(BigInteger bits) {
			bytes = coverBase.clone();
			for (int i = 0; i < STENCIL_SIZE * STENCIL_SIZE; i++)
				if (bits.testBit(i)) bytes[i] = 0x00000000;

			pushBitsToTexture();
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
			if (bytes == null) bytes = blankBytes;
			return super.load(manager, location);
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
	public boolean load(ResourceManager manager, ResourceLocation location) throws IOException {
		this.width = this.height = STENCIL_SIZE;
		return true;
	}

	@Override
	public void updateAnimation() {}
}
