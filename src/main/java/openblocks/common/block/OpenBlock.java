package openblocks.common.block;

import net.minecraft.block.material.Material;
import openblocks.OpenBlocks;

public abstract class OpenBlock extends openmods.block.OpenBlock {

	protected OpenBlock(Material material) {
		super(material);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderIdFull;
	}

	@Override
	protected Object getModInstance() {
		return OpenBlocks.instance;
	}
}
