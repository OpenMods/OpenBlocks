package openblocks.client.renderer.tileentity.guide;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import openblocks.common.tileentity.TileEntityGuide;
import openmods.utils.TextureUtils;
import org.lwjgl.opengl.GL11;

public class TileEntityGuideRenderer extends TileEntitySpecialRenderer {

	private final IGuideRenderer renderer;

	public TileEntityGuideRenderer() {
		this.renderer = new GuideRendererSelector().getRenderer();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onTextureChange(TextureStitchEvent evt) {
		if (evt.map.getTextureType() == TextureUtils.TEXTURE_MAP_BLOCKS) renderer.onTextureChange();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		renderer.renderShape((TileEntityGuide)tileentity);
		GL11.glPopMatrix();
	}
}
