package openblocks.client.renderer;

import java.io.IOException;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.ResourceManager;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Preconditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DisposableDynamicTexture extends AbstractTexture {
	private int[] dynamicTextureData;
	private int width;
	private int height;

	private static int textureCounter;

	public DisposableDynamicTexture() {}

	public void resize(int width, int height) {
		if (width != this.width || height != this.height) {
			this.width = width;
			this.height = height;
			TextureUtil.allocateTexture(getGlTextureId(), width, height);
		}
	}

	@Override
	public void loadTexture(ResourceManager par1ResourceManager) throws IOException {}

	public void update() {
		Preconditions.checkNotNull(dynamicTextureData, "Texture not allocated");
		TextureUtil.uploadTexture(getGlTextureId(), dynamicTextureData, width, height);
	}

	public void updateAndDeallocate() {
		update();
		dynamicTextureData = null;
	}

	public int[] allocate() {
		if (dynamicTextureData == null) dynamicTextureData = new int[width * height];

		return this.dynamicTextureData;
	}

	public ResourceLocation register(TextureManager manager, String prefix) {
		ResourceLocation location = new ResourceLocation(String.format("dynamic_o/%s_%d", prefix, textureCounter));
		textureCounter++;

		manager.loadTexture(location, this);
		return location;
	}
}
