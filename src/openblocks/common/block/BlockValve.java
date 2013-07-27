package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityFlag;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.common.tileentity.TileEntityValve;
import openblocks.utils.BlockUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockValve extends OpenBlock {

	public BlockValve() {
		super(OpenBlocks.Config.blockValveId, Material.glass);
		setupBlock(this, "valve", "Valve", TileEntityValve.class);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityValve) {
			((TileEntityValve) te).destroyTank();
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}
	

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving entity, ItemStack itemstack) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityValve) {
			TileEntityValve valve = (TileEntityValve) te;
			valve.setDirection(BlockUtils.get3dOrientation(entity));
			valve.markForRecheck();
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {

		super.onNeighborBlockChange(world, x, y, z, blockId);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityValve) {
			((TileEntityValve) te).markForRecheck();
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
