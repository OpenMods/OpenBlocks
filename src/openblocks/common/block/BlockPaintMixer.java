package openblocks.common.block;

import net.minecraft.block.material.Material;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityPaintMixer;

public class BlockPaintMixer extends OpenBlock {

	public BlockPaintMixer() {
		super(Config.blockPaintMixer, Material.ground);
		setupBlock(this, "paintmixer", TileEntityPaintMixer.class);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setBlockBounds(0.125f, 0f, 0.125f, 0.875f, 1f, 0.875f);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

}
