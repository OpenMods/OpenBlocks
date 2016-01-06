package openblocks.common.block;

import net.minecraft.block.material.Material;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class BlockPaintMixer extends OpenBlock.FourDirections {

	public BlockPaintMixer() {
		super(Material.rock);
		setBlockBounds(0.125f, 0f, 0.125f, 0.875f, 1f, 0.875f);
	}

	// TODO 1.8.9 got you now!
	@Override
	public int getRenderType() {
		return 2; // TESR only
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
