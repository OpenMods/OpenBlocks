package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.material.Material;

public class BlockGrave extends OpenBlock {

	public BlockGrave() {
		super(Material.ground);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setBlockBounds(0, 0, 0, 1f, 0.2f, 1f);
		setCreativeTab(null);
		setResistance(2000.0F);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}
}
