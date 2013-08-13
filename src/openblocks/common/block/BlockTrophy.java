package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityTrophy;
import net.minecraft.block.material.Material;

public class BlockTrophy extends OpenBlock {

	public BlockTrophy() {
		super(OpenBlocks.Config.blockTrophyId, Material.ground);
		setupBlock(this, "trophy", TileEntityTrophy.class);
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
