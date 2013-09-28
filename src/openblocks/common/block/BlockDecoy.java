package openblocks.common.block;

import net.minecraft.block.material.Material;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityDecoy;

public class BlockDecoy extends OpenBlock {

	public BlockDecoy() {
		super(Config.blockDecoyId, Material.ground);
		setupBlock(this, "decoy", TileEntityDecoy.class);
	}

}
