package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTank;
import openblocks.common.tileentity.TileEntityValve;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockTank extends OpenBlock {

	public BlockTank() {
		super(OpenBlocks.Config.blockTankId, Material.glass);
		setupBlock(this, "Tank", "Tank", TileEntityTank.class);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		super.onNeighborBlockChange(world, x, y, z, blockId);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityTank) {
			((TileEntityTank) te).notifyTank();
		}
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
