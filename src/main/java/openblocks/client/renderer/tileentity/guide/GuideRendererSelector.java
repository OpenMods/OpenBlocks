package openblocks.client.renderer.tileentity.guide;

import openblocks.Config;
import openmods.Log;
import openmods.renderer.shaders.ArraysHelper;
import openmods.renderer.shaders.BufferHelper;
import openmods.renderer.shaders.ShaderHelper;

public class GuideRendererSelector {

	private static Runnable createMarkerRenderer() {
		return new Runnable() {
			@Override
			public void run() {
				// TODO 1.8.9 use actual models
				// well, as long as array allows...
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
			} catch (Throwable e) {
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
