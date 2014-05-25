package openblocks.common.block;

import net.minecraft.block.material.Material;

public class BlockXPDrain extends OpenBlock {

	public BlockXPDrain() {
		super(Material.glass);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

}
