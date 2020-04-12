package openblocks.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;
import openmods.model.variant.VariantModelState;

@BookDocumentation
public class BlockVacuumHopper extends OpenBlock {

	private static final AxisAlignedBB SELECTION_AABB = new AxisAlignedBB(0.3, 0.3, 0.3, 0.7, 0.7, 0.7);
	private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);

	public BlockVacuumHopper() {
		super(Material.ROCK);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,
				new IProperty[] { getPropertyOrientation() },
				new IUnlistedProperty[] { VariantModelState.PROPERTY });
	}

	private static final AxisAlignedBB STANDARD_AABB = new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
		return STANDARD_AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(BlockState state, IBlockAccess world, BlockPos pos) {
		return COLLISION_AABB;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World world, BlockPos pos) {
		return SELECTION_AABB;
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos) {
		if (state instanceof IExtendedBlockState) {
			final IExtendedBlockState oldState = (IExtendedBlockState)state;
			final TileEntityVacuumHopper te = getTileEntity(world, pos, TileEntityVacuumHopper.class);
			if (te != null) {
				final VariantModelState selectors = VariantModelState.create(te.getOutputState());
				return oldState.withProperty(VariantModelState.PROPERTY, selectors);
			}
		}

		return state;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, BlockState state, Entity entity) {
		TileEntityVacuumHopper te = getTileEntity(world, pos, TileEntityVacuumHopper.class);
		if (te != null) {
			te.onEntityCollidedWithBlock(entity);
		}
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}
}
