package openblocks.common.block;

import net.minecraft.block.material.Material;
import openblocks.OpenBlocks;

public abstract class OpenBlock extends openmods.block.OpenBlock {

	protected OpenBlock(int id, Material material) {
		super(id, material);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}
}
