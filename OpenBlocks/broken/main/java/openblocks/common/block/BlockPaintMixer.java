package openblocks.common.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.tileentity.TileEntityPaintMixer;
import openmods.block.OpenBlock;
import openmods.colors.RGB;
import openmods.infobook.BookDocumentation;
import openmods.utils.SimpleUnlistedProperty;

@BookDocumentation(hasVideo = true)
public class BlockPaintMixer extends OpenBlock.FourDirections {

	@SideOnly(Side.CLIENT)
	public static class BlockColorHandler implements IBlockColor {
		private static final int WHITE = 0xFFFFFFFF;

		@Override
		public int colorMultiplier(BlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
			if (tintIndex == 0 && state instanceof IExtendedBlockState) {
				final IExtendedBlockState extendedState = (IExtendedBlockState)state;
				final Integer canColor = extendedState.getValue(CAN_COLOR);
				if (canColor != null) return canColor;
			}

			return WHITE;
		}
	}

	private static final IUnlistedProperty<Integer> CAN_COLOR = SimpleUnlistedProperty.create(Integer.class, "can_color");

	public BlockPaintMixer() {
		super(Material.ROCK);
		setDefaultState(getDefaultState().withProperty(Properties.StaticProperty, true));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this,
				new IProperty[] { getPropertyOrientation(), Properties.StaticProperty },
				new IUnlistedProperty[] { Properties.AnimationProperty, CAN_COLOR });
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos) {
		if (state instanceof IExtendedBlockState) {
			final TileEntityPaintMixer mixer = getTileEntity(world, pos, TileEntityPaintMixer.class);
			if (mixer != null) {
				final int progress = mixer.getProgress().getValue();

				final int currentColor;

				if (progress != 0) {
					final RGB startColor = new RGB(mixer.getCanColor());
					final RGB endColor = new RGB(mixer.getColor().getValue());
					final float scaledProgress = progress / (float)TileEntityPaintMixer.PROGRESS_TICKS;
					currentColor = startColor.interpolate(endColor, scaledProgress).getColor();
				} else {
					currentColor = mixer.getCanColor();
				}
				return ((IExtendedBlockState)state).withProperty(CAN_COLOR, currentColor);
			}
		}

		return state;
	}

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125, 0.0, 0.125, 0.875, 1.0, 0.875);

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return face == Direction.DOWN? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.UNDEFINED;
	}
}
