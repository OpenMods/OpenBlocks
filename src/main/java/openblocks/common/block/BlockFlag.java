package openblocks.common.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.tileentity.TileEntityFlag;
import openmods.block.OpenBlock;
import openmods.colors.ColorMeta;
import openmods.geometry.Orientation;
import openmods.model.eval.EvalModelState;

public class BlockFlag extends OpenBlock.SixDirections {

	public static final ColorMeta DEFAULT_COLOR = ColorMeta.LIME;

	public static final PropertyEnum<ColorMeta> COLOR = PropertyEnum.create("color", ColorMeta.class);

	@SideOnly(Side.CLIENT)
	public static class BlockColorHandler implements IBlockColor {
		private static final int WHITE = 0xFFFFFFFF;

		@Override
		public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
			return tintIndex == 0? state.getValue(COLOR).rgb : WHITE;
		}
	}

	public BlockFlag() {
		super(Material.CIRCUITS);
		setPlacementMode(BlockPlacementMode.SURFACE);
		setDefaultState(getDefaultState().withProperty(COLOR, DEFAULT_COLOR));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,
				new IProperty[] { getPropertyOrientation(), COLOR },
				new IUnlistedProperty[] { EvalModelState.PROPERTY });
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
		return NULL_AABB;
	}

	private static final AxisAlignedBB MIDDLE_AABB = new AxisAlignedBB(0.5 - (1.0 / 16.0), 0.0, 0.5 - (1.0 / 16.0), 0.5 + (1.0 / 16.0), 0.0 + 1.0, 0.5 + (1.0 / 16.0));
	private static final AxisAlignedBB NS_AABB = new AxisAlignedBB(0.5 - (1.0 / 16.0), 0.0, 0.5 - (5.0 / 16.0), 0.5 + (1.0 / 16.0), 0.0 + 1.0, 0.5 + (5.0 / 16.0));
	private static final AxisAlignedBB WE_AABB = new AxisAlignedBB(0.5 - (5.0 / 16.0), 0.0, 0.5 - (1.0 / 16.0), 0.5 + (5.0 / 16.0), 0.0 + 1.0, 0.5 + (1.0 / 16.0));

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileEntityFlag flag = getTileEntity(source, pos, TileEntityFlag.class);
		if (flag != null) {
			EnumFacing onSurface = flag.getOrientation().down();
			switch (onSurface) {
				case EAST:
				case WEST:
					return WE_AABB;
				case NORTH:
				case SOUTH:
					return NS_AABB;
				default:
					return MIDDLE_AABB;
			}
		}

		return MIDDLE_AABB;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		if (side == EnumFacing.DOWN) return false;
		if (side == EnumFacing.UP) {
			final BlockPos blockBelow = pos.down();
			final Block belowBlock = world.getBlockState(blockBelow).getBlock();
			if (belowBlock instanceof BlockFence) return true;
			if (belowBlock == this) {
				TileEntityFlag flag = getTileEntity(world, blockBelow, TileEntityFlag.class);
				if (flag != null && flag.getOrientation().down() == EnumFacing.DOWN) return true;
			}
		}

		return isNeighborBlockSolid(world, pos, side.getOpposite());
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
		super.neighborChanged(state, world, pos, block);

		final Orientation orientation = getOrientation(state);
		if (!isNeighborBlockSolid(world, pos, orientation.down())) world.destroyBlock(pos, true);
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	private static ColorMeta getColorMeta(IBlockAccess world, BlockPos pos) {
		final TileEntityFlag te = getTileEntity(world, pos, TileEntityFlag.class);
		return te != null? te.getColor() : ColorMeta.WHITE;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		final ColorMeta color = getColorMeta(world, pos);
		return state.withProperty(COLOR, color);
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(itemIn, 1, DEFAULT_COLOR.vanillaBlockId));
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		final TileEntityFlag te = getTileEntity(world, pos, TileEntityFlag.class);

		return (te != null)
				? ((IExtendedBlockState)state).withProperty(EvalModelState.PROPERTY, te.getRenderState())
				: state;
	}
}
