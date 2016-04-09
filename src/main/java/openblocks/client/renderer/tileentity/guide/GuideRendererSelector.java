package openblocks.client.renderer.tileentity.guide;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockGuide.Icons;
import openmods.Log;
import openmods.renderer.shaders.ArraysHelper;
import openmods.renderer.shaders.BufferHelper;
import openmods.renderer.shaders.ShaderHelper;

public class GuideRendererSelector {

	private static Runnable createMarkerRenderer() {
		return new Runnable() {
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
	}

	private static IGuideRenderer createRenderer() {
		final Runnable marker = createMarkerRenderer();
		if (!ShaderHelper.isSupported() || !BufferHelper.isSupported() || !ArraysHelper.isSupported()) {
			Log.debug("Advanced guide renderer not supported, falling back to legacy renderer.");
			return new GuideLegacyRenderer(marker); // advanced renderer not supported :(
		} else if (Config.useAdvancedRenderer == false) {
			Log.debug("Advanced guide renderer disabled, falling back to legacy renderer.");
			return new GuideLegacyRenderer(marker);
		} else {
			try {
				return new GuideAdvancedRenderer(marker); // try to use the advanced renderer
			} catch (Exception e) {
				Log.warn(e, "Error trying to create advanced renderer, falling back to legacy renderer");
				return new GuideLegacyRenderer(marker); // fall back to the old renderer.
			}
		}
	}

	private static IGuideRenderer renderer;

	public IGuideRenderer getRenderer() {
		if (renderer == null) renderer = createRenderer();
		return renderer;
	}

}
