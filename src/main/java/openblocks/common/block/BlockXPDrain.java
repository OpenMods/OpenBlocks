package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.BlockRotationMode;
import openmods.infobook.BookDocumentation;

@BookDocumentation
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
}
