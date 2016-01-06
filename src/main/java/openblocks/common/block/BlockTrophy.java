package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;

public class BlockTrophy extends OpenBlock.FourDirections {

	public BlockTrophy() {
		super(Material.rock);
		setBlockBounds(0.2f, 0, 0.2f, 0.8f, 0.2f, 0.8f);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

}
