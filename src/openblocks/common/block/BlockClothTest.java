package openblocks.common.block;

import openblocks.Config;
import openblocks.common.tileentity.TileEntityClothTest;
import net.minecraft.block.material.Material;

public class BlockClothTest extends OpenBlock {

	public BlockClothTest() {
		super(Config.blockClothTest, Material.cloth);
		setupBlock(this, "clothtest", TileEntityClothTest.class);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		// TODO Auto-generated method stub
		return false;
	}

}
