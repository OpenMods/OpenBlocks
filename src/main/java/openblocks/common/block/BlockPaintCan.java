package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;

public class BlockPaintCan extends OpenBlock.FourDirections {

	public BlockPaintCan() {
		super(Material.rock);
		setHardness(0);
		setPlacementMode(BlockPlacementMode.ENTITY_ANGLE);
		setBlockBounds(0.25f, 0f, 0.25f, 0.7f, 0.6875f, 0.75f);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

}
