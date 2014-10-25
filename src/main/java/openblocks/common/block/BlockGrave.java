package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import openmods.block.BlockRotationMode;

public class BlockGrave extends OpenBlock {

	public BlockGrave() {
		super(Material.ground);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setBlockBounds(0, 0, 0, 1f, 0.2f, 1f);
		setCreativeTab(null);
		setResistance(2000.0F);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean isOpaqueCube() {
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
