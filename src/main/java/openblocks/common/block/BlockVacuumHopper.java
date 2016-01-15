package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockVacuumHopper extends OpenBlock {

	public BlockVacuumHopper() {
		super(Material.rock);
	}

	// TODO 1.8.9 hello states
	@Override
	public int getRenderType() {
		return 2; // TESR only
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		return new AxisAlignedBB(pos.getX() + 0.01, pos.getY() + 0.01, pos.getZ() + 0.01,
				pos.getX() + 0.99, pos.getY() + 0.99, pos.getZ() + 0.99);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {
		TileEntityVacuumHopper te = getTileEntity(world, pos, TileEntityVacuumHopper.class);
		if (te != null) {
			te.onEntityCollidedWithBlock(entity);
		}
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
		return new AxisAlignedBB(pos.getX() + 0.3, pos.getY() + 0.3, pos.getZ() + 0.3,
				pos.getX() + 0.7, pos.getY() + 0.7, pos.getZ() + 0.7);
	}

}
