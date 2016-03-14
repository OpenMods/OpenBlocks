package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockVacuumHopper extends OpenBlock {

	public BlockVacuumHopper() {
		super(Material.rock);
		setRenderMode(RenderMode.TESR_ONLY);
		setBlockBounds(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(x + 0.01, y + 0.01, z + 0.01, x + 0.99, y + 0.99, z + 0.99);
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

}
