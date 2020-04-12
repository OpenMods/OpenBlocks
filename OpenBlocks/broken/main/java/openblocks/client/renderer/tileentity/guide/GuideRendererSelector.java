package openblocks.client.renderer.tileentity.guide;

import openblocks.Config;
import openmods.Log;
import openmods.renderer.shaders.ArraysHelper;
import openmods.renderer.shaders.BufferHelper;
import openmods.renderer.shaders.ShaderHelper;

public class GuideRendererSelector {

	private static IGuideRenderer createRenderer() {
		if (!ShaderHelper.isSupported() || !BufferHelper.isSupported() || !ArraysHelper.isSupported()) {
			Log.debug("Advanced guide renderer not supported, falling back to legacy renderer.");
			return new GuideLegacyRenderer(); // advanced renderer not supported :(
		} else if (!Config.useAdvancedRenderer) {
			Log.debug("Advanced guide renderer disabled, falling back to legacy renderer.");
			return new GuideLegacyRenderer();
		} else {
			try {
				return new GuideAdvancedRenderer(); // try to use the advanced renderer
			} catch (Throwable e) {
				Log.warn(e, "Error trying to create advanced renderer, falling back to legacy renderer");
				return new GuideLegacyRenderer(); // fall back to the old renderer.
			}
		}
	}

	private static IGuideRenderer renderer;

	public IGuideRenderer getRenderer() {
		if (renderer == null) renderer = createRenderer();
		return renderer;
	}

}
