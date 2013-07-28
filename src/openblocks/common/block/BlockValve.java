package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityValve;
import openblocks.utils.BlockUtils;

public class BlockValve extends OpenBlock {

	public BlockValve() {
		super(OpenBlocks.Config.blockValveId, Material.glass);
		setupBlock(this, "valve", TileEntityValve.class);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityValve) {
			((TileEntityValve) te).destroyTank();
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}
	

	public void onBlockPlacedBy(World world, EntityPlayer player,
			ItemStack stack, int x, int y, int z, ForgeDirection side,
			float hitX, float hitY, float hitZ, int meta) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityValve) {
			TileEntityValve valve = (TileEntityValve) te;
			valve.setDirection(BlockUtils.get3dOrientation(player));
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
