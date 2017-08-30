package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import openblocks.common.tileentity.TileEntityFan;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;
import openmods.model.eval.EvalModelState;

@BookDocumentation
public class BlockFan extends OpenBlock {

	public BlockFan() {
		super(Material.CIRCUITS);
		setDefaultState(getDefaultState().withProperty(Properties.StaticProperty, true));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,
				new IProperty[] { getPropertyOrientation(), Properties.StaticProperty },
				new IUnlistedProperty[] { EvalModelState.PROPERTY });
	}

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.2, 0.0, 0.2, 0.8, 1.0, 0.8);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return isOnTopOfSolidBlock(world, pos, side);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		final TileEntityFan te = getTileEntity(world, pos, TileEntityFan.class);

		return (te != null)
				? ((IExtendedBlockState)state).withProperty(EvalModelState.PROPERTY, te.getStaticRenderState())
				: state;
	}

}
