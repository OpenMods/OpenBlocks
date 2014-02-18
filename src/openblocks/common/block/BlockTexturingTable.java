package openblocks.common.block;

import openblocks.Config;
import net.minecraft.block.material.Material;

public class BlockTexturingTable extends OpenBlock {

	public BlockTexturingTable() {
		super(Config.blockTexturingTableId, Material.ground);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

}
