package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;

public class BlockDonationStation extends OpenBlock.FourDirections {

	public BlockDonationStation() {
		super(Material.rock);
		setBlockBounds(0.2f, 0.25f, 0.2f, 0.8f, 0.85f, 0.8f);
	}

	// TODO 1.8.9 almost forgot about you
	@Override
	public int getRenderType() {
		return 2; // TESR only
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
