package openblocks.client.renderer.tileentity.guide;

import java.util.function.Supplier;
import net.minecraft.client.renderer.BufferBuilder;
import openblocks.common.tileentity.TileEntityGuide;

public interface IGuideRenderer {
	void renderShape(TileEntityGuide guide);

	void onModelBake(Supplier<BufferBuilder> modelSupplier);
}
