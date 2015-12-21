package openblocks.common.block;

import net.minecraft.block.material.Material;

public class BlockGoldenEgg extends OpenBlock {

	public BlockGoldenEgg() {
		super(Material.ground);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
