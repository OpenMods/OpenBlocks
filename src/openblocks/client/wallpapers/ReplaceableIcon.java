package openblocks.client.wallpapers;

import java.io.IOException;
import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.Resource;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;

public class ReplaceableIcon extends TextureAtlasSprite {

	private static final int SIZE = 16;
	
	protected int[] bytes = new int[SIZE * SIZE];
	protected boolean loaded = false;
	
	protected ReplaceableIcon(String id) {
		super(id);
		Arrays.fill(bytes, 0xFFFFFFFF);
	}

	@Override
	public int[] getFrameTextureData(int frame) {
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

	public void setBytes(int[] bytes) {
		this.bytes = bytes;
		pushBitsToTexture();
	}

	protected void pushBitsToTexture() {
		TextureManager manager = Minecraft.getMinecraft().getTextureManager();
		manager.bindTexture(TextureMap.locationBlocksTexture);

		TextureUtil.uploadTextureSub(bytes, width, height, originX, originY, false, false);
	}

	@Override
	public void loadSprite(Resource par1Resource) {}

	@Override
	public boolean load(ResourceManager manager, ResourceLocation location) throws IOException {
		this.width = this.height = SIZE;
		return true;
	}

	@Override
	public void updateAnimation() {}
}
