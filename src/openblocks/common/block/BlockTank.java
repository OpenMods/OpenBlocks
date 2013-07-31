package openblocks.common.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTank;

public class BlockTank extends OpenBlock {

	public BlockTank() {
		super(OpenBlocks.Config.blockTankId, Material.air);
		setupBlock(this, "Tank", TileEntityTank.class);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		if (blockId != this.blockID) {
			super.onNeighborBlockChange(world, x, y, z, blockId);
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityTank) {
				((TileEntityTank)te).notifyTank();
			}
		}
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
	}

	/**
	 * We don't want this in creative tab
	 */
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isAirBlock(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}
}
