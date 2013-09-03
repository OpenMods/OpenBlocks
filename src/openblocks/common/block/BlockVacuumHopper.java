package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.item.ItemTrophyBlock;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockVacuumHopper extends OpenBlock {

	public BlockVacuumHopper() {
		super(OpenBlocks.Config.blockVacuumHopperId, Material.ground);
		setupBlock(this, "vacuumhopper", TileEntityVacuumHopper.class);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getAABBPool().getAABB((double)x + 0.01, (double)y + 0.01, (double)z + 0.01, (double)x + 0.99, (double)y + 0.99, (double)z + 0.99);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		TileEntityVacuumHopper te = getTileEntity(world, x, y, z, TileEntityVacuumHopper.class);
		if (te != null) {
			te.onEntityCollidedWithBlock(entity);
		}
	}
	
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
