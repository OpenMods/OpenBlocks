package openblocks.common.block;

import net.minecraft.block.material.Material;
import openblocks.Config;

public class BlockGoldenEgg extends OpenBlock {

	public BlockGoldenEgg() {
		super(Config.blockGoldenEggId, Material.ground);
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
