package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntitySprinkler;
import net.minecraft.block.material.Material;

public class BlockSprinkler extends OpenBlock {

	public BlockSprinkler() {
		super(OpenBlocks.Config.blockSprinklerId, Material.water);
		setupBlock(this, "sprinkler", TileEntitySprinkler.class);
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
