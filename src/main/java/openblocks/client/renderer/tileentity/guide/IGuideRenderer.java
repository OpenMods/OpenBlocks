package openblocks.client.renderer.tileentity.guide;

import openblocks.common.tileentity.TileEntityGuide;

public interface IGuideRenderer {
	void renderShape(TileEntityGuide guide);

	void onTextureChange();
}
