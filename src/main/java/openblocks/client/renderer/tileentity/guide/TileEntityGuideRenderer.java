package openblocks.client.renderer.tileentity.guide;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockGuide.Icons;
import openblocks.common.tileentity.TileEntityGuide;
import openmods.Log;
import openmods.utils.TextureUtils;

public class TileEntityGuideRenderer extends TileEntitySpecialRenderer {
	
	private final Runnable marker = new Runnable(){
		@Override
		public void run() {
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
	
	private IGuideRenderer renderer;
	
	public TileEntityGuideRenderer() {
		MinecraftForge.EVENT_BUS.register(this);

		if (!ShaderHelper.isSupported() || !BufferHelper.isSupported() || !ArraysHelper.isSupported()) {
			renderer = new GuideLegacyRenderer(marker); // advanced renderer not supported :(
			Log.debug("Advanced guide renderer not supported, falling back to legacy renderer.", (Object[]) null);
		} else if (Config.useAdvancedRenderer == false){
			renderer = new GuideLegacyRenderer(marker);
			Log.debug("Advanced guide renderer disabled, falling back to legacy renderer.", (Object[]) null);
		} else {
			try {
				renderer = new GuideAdvancedRenderer(marker); // try to use the advanced renderer
			} catch (Exception e) {
				Log.debug("Error trying to create advanced renderer, falling back to legacy renderer", (Object[]) null);
				renderer = new GuideLegacyRenderer(marker); // fall back to the old renderer.
			}
		}
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
