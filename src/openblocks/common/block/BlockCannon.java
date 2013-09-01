package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityCannon;
import net.minecraft.block.material.Material;

public class BlockCannon extends OpenBlock {

	public BlockCannon() {
		super(OpenBlocks.Config.blockCannonId, Material.ground);
		setupBlock(this, "cannon", TileEntityCannon.class);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}
	

}
