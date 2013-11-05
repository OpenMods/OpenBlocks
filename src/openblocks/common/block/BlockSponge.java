package openblocks.common.block;

import net.minecraft.block.material.Material;
import openblocks.Config;
import openblocks.common.tileentity.TileEntitySponge;

public class BlockSponge extends OpenBlock {

	public BlockSponge() {
		super(Config.blockSpongeId, Material.sponge);
		setupBlock(this, "sponge", TileEntitySponge.class);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}
}
