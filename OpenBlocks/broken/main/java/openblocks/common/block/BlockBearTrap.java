package openblocks.common.block;

import net.minecraft.block.BlockRenderType;
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
import net.minecraftforge.common.property.IUnlistedProperty;
import openblocks.common.tileentity.TileEntityBearTrap;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;
import openmods.model.eval.EvalModelState;

@BookDocumentation
public class BlockBearTrap extends OpenBlock.TwoDirections {

	public BlockBearTrap() {
		super(Material.ROCK);
	}

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.1, 0.0, 0.1, 0.9, 0.4, 0.9);

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,
				new IProperty[] { getPropertyOrientation() },
				new IUnlistedProperty[] { EvalModelState.PROPERTY });
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isSideSolid(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, BlockState state, Entity entity) {
		TileEntityBearTrap te = getTileEntity(world, pos, TileEntityBearTrap.class);
		if (te != null) te.onEntityCollided(entity);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, Direction side) {
		return isOnTopOfSolidBlock(world, pos, side);
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		TileEntityBearTrap tile = getTileEntity(world, pos, TileEntityBearTrap.class);
		return tile != null? tile.getComparatorLevel() : 0;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}
}
