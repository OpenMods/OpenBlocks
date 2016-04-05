package openblocks.client.renderer.tileentity.guide;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockGuide.Icons;
import openblocks.common.tileentity.TileEntityGuide;
import openmods.utils.TextureUtils;

public class TileEntityGuideRenderer extends TileEntitySpecialRenderer {
	
	private final FutureTesselator marker = new FutureTesselator(){
		@Override
		public void render() {
			Tessellator t = Tessellator.instance;
			RenderBlocks renderBlocks = new RenderBlocks();
			renderBlocks.setRenderBounds(0.05D, 0.05D, 0.05D, 0.95D, 0.95D, 0.95D);
			t.startDrawingQuads();
			t.setBrightness(200);
			renderBlocks.renderFaceXNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, Icons.marker);
			renderBlocks.renderFaceXPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, Icons.marker);
			renderBlocks.renderFaceYNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, Icons.marker);
			renderBlocks.renderFaceYPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, Icons.marker);
			renderBlocks.renderFaceZNeg(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, Icons.marker);
			renderBlocks.renderFaceZPos(OpenBlocks.Blocks.guide, -0.5D, 0.0D, -0.5D, Icons.marker);
			// important: don't draw!
		}
	};
	
	private final IGuideRenderer renderer = new GuideLegacyRenderer(marker);
	
	public TileEntityGuideRenderer() {
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
		renderer.renderShape((TileEntityGuide) tileentity);
		GL11.glPopMatrix();
	}
}
