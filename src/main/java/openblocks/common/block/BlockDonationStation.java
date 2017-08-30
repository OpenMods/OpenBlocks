package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import openmods.block.OpenBlock;

public class BlockDonationStation extends OpenBlock.FourDirections {

	public BlockDonationStation() {
		super(Material.ROCK);
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
}
