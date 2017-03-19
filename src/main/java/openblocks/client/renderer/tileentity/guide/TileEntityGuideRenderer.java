package openblocks.client.renderer.tileentity.guide;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.common.tileentity.TileEntityGuide;
import org.lwjgl.opengl.GL11;

public class TileEntityGuideRenderer<T extends TileEntityGuide> extends TileEntitySpecialRenderer<T> {

	private final IGuideRenderer renderer;

	public TileEntityGuideRenderer() {
		this.renderer = new GuideRendererSelector().getRenderer();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onTextureChange(TextureStitchEvent evt) {
		renderer.onTextureChange();
	}

	@Override
	public void renderTileEntityAt(T tileentity, double x, double y, double z, float partialTicks, int destroyStage) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		renderer.renderShape(tileentity);
		GL11.glPopMatrix();
	}
}
