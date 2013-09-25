package openblocks.common.block;

import openblocks.Config;
import openblocks.common.tileentity.TileEntityDecoy;
import net.minecraft.block.material.Material;

public class BlockDecoy extends OpenBlock {

	public BlockDecoy() {
		super(Config.blockDecoyId, Material.ground);
		setupBlock(this, "decoy", TileEntityDecoy.class);
	}

}
