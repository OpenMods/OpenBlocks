package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import openblocks.Config;

public class BlockGrave extends OpenBlock {

	public BlockGrave() {
		super(Config.blockGraveId, Material.anvil);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setBlockBounds(0, 0, 0, 1f, 0.2f, 1f);
		setCreativeTab(null);
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
}
