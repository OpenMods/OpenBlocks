package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;

public class BlockGoldenEgg extends OpenBlock {

	public BlockGoldenEgg() {
		super(Material.ground);
	}

	@Override
	public int getRenderType() {
		return 2; // TESR only
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
