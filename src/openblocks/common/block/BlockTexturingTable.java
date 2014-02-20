package openblocks.common.block;

import net.minecraft.block.material.Material;
import openblocks.Config;

public class BlockTexturingTable extends OpenBlock {

	public BlockTexturingTable() {
		super(Config.blockTexturingTableId, Material.ground);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

}
