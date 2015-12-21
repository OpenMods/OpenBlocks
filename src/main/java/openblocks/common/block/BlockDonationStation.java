package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.BlockRotationMode;

public class BlockDonationStation extends OpenBlock {

	public BlockDonationStation() {
		super(Material.rock);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setBlockBounds(0.2f, 0.25f, 0.2f, 0.8f, 0.85f, 0.8f);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
