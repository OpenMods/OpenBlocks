package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityBearTrap;

public class BlockBearTrap extends OpenBlock {

	public BlockBearTrap() {
		super(OpenBlocks.Config.blockBearTrapId, Material.ground);
		setupBlock(this, "beartrap", TileEntityBearTrap.class);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		TileEntityBearTrap te = this.getTileEntity(world, x, y, z, TileEntityBearTrap.class);
		if (te != null) {
			TileEntityBearTrap teb = (TileEntityBearTrap)te;
			teb.onEntityCollided(entity);
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

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z) {
		return AxisAlignedBB.getAABBPool().getAABB((double)x, (double)y, (double)z, (double)x + 1.0, (double)y + 0.1, (double)z + 1.0);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		setBlockBounds(0.1f, 0, 0.1f, 0.9f, 0.4f, 0.9f);
	}
	
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return super.canPlaceBlockOnSide(world, x, y, z, ForgeDirection.DOWN);
	}
}
