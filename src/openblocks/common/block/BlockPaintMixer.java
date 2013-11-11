package openblocks.common.block;

import openblocks.Config;
import openblocks.common.tileentity.TileEntityPaintMixer;
import net.minecraft.block.material.Material;

public class BlockPaintMixer extends OpenBlock {

	public BlockPaintMixer() {
		super(Config.blockPaintMixer, Material.ground);
		setupBlock(this, "paintmixer", TileEntityPaintMixer.class);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
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
