package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.tileentity.TileEntityProjector;
import openblocks.utils.BlockUtils;

public class BlockProjector extends OpenBlock {

	public BlockProjector() {
		super(Config.blockProjectorId, Material.iron);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setBlockBounds(0, 0, 0, 1, 0.5f, 1);
		setupBlock(this, "projector", TileEntityProjector.class);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof IActivateAwareTile) return ((IActivateAwareTile)te).onBlockActivated(player, side, hitX, hitY, hitZ);
		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int blockId, int meta) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof IInventory) BlockUtils.dropTileInventory(te);
		super.breakBlock(world, x, y, z, blockId, meta);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}
}
