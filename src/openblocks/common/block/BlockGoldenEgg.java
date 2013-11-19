package openblocks.common.block;

import net.minecraft.block.material.Material;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityGoldenEgg;

public class BlockGoldenEgg extends OpenBlock {

	public BlockGoldenEgg() {
		super(Config.blockGoldenEggId, Material.ground);
		setupBlock(this, "goldenegg", TileEntityGoldenEgg.class);
	}

	
	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
