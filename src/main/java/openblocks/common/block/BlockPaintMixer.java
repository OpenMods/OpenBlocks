package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.BlockRotationMode;

public class BlockPaintMixer extends OpenBlock {

	public BlockPaintMixer() {
		super(Material.rock);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setBlockBounds(0.125f, 0f, 0.125f, 0.875f, 1f, 0.875f);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
