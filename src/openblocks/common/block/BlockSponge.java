package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntitySponge;
import net.minecraft.block.material.Material;

public class BlockSponge extends OpenBlock {

	public BlockSponge() {
		super(OpenBlocks.Config.blockSpongeId, Material.sponge);
		setupBlock(this, "sponge", TileEntitySponge.class);
	}

}
