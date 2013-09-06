package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityVacuumHopper;

public class BlockVacuumHopper extends OpenBlock {

	public BlockVacuumHopper() {
		super(OpenBlocks.Config.blockVacuumHopperId, Material.ground);
		setupBlock(this, "vacuumhopper", TileEntityVacuumHopper.class);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getAABBPool().getAABB(x + 0.01, y + 0.01, z + 0.01, x + 0.99, y + 0.99, z + 0.99);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		TileEntityVacuumHopper te = getTileEntity(world, x, y, z, TileEntityVacuumHopper.class);
		if (te != null) {
			te.onEntityCollidedWithBlock(entity);
		}
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return true;
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
