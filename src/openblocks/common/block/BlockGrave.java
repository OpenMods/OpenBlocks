package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Config;
import openblocks.common.tileentity.TileEntityGrave;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockGrave extends OpenBlock {

	public BlockGrave() {
		super(Config.blockGraveId, Material.anvil); /* Requires tool and immovable */
		setupBlock(this, "grave", "Grave", TileEntityGrave.class);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}


}
