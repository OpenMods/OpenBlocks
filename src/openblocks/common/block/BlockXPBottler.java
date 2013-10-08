package openblocks.common.block;

import openblocks.Config;
import openblocks.common.tileentity.TileEntityXPBottler;
import net.minecraft.block.material.Material;

public class BlockXPBottler extends OpenBlock {

	public BlockXPBottler() {
		super(Config.blockXPBottlerId, Material.ground);
		setupBlock(this, "xpbottler", TileEntityXPBottler.class);
	}

}
