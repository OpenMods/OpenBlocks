package openblocks.common.block;

import openblocks.Config;
import openblocks.common.tileentity.TileEntityClayStainer;
import net.minecraft.block.material.Material;

public class BlockClayStainer extends OpenBlock {

	public BlockClayStainer() {
		super(Config.blockClayStainerId, Material.ground);
		setupBlock(this, "claystainer", TileEntityClayStainer.class);
		setRotationMode(BlockRotationMode.NONE);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

}
