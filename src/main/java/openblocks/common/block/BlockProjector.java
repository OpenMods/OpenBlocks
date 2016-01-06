package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;

public class BlockProjector extends OpenBlock {

	public BlockProjector() {
		super(Material.iron);
		setBlockBounds(0, 0, 0, 1, 0.5f, 1);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockNormalCube() {
		return false;
	}

	@Override
	public boolean isFullBlock() {
		return false;
	}
}
