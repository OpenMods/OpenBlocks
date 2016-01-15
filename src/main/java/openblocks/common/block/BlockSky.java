package openblocks.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;
import openmods.utils.render.RenderUtils;

@BookDocumentation(customName = "sky.normal")
public class BlockSky extends OpenBlock {

	private static final int MASK_INVERTED = 1 << 0;
	private static final int MASK_POWERED = 1 << 1;

	private static final AxisAlignedBB EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	public static final PropertyBool INVERTED = PropertyBool.create("inverted");

	public static final PropertyBool POWERED = PropertyBool.create("active");

	public static boolean isInverted(int meta) {
		return (meta & MASK_INVERTED) != 0;
	}

	public BlockSky() {
		super(Material.iron);
		setDefaultState(getDefaultState().withProperty(POWERED, false));
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, getPropertyOrientation(), INVERTED, POWERED);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(POWERED, (meta & MASK_POWERED) != 0)
				.withProperty(INVERTED, (meta & MASK_INVERTED) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		final int isPowered = state.getValue(POWERED)? MASK_POWERED : 0;
		final int isInverted = state.getValue(INVERTED)? MASK_INVERTED : 0;

		return isPowered | isInverted;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor() {
		// randomness more or less intended
		return RenderUtils.getFogColor().getColor();
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
		if (!world.isRemote) {
			final boolean isPowered = world.isBlockIndirectlyGettingPowered(pos) > 0;
			final boolean isActive = state.getValue(POWERED);

			if (isPowered != isActive) world.scheduleUpdate(pos, this, 1);
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		final boolean isPowered = world.isBlockIndirectlyGettingPowered(pos) > 0;

		world.setBlockState(pos, state.withProperty(POWERED, isPowered));
	}

	public static boolean isActive(IBlockState state) {
		boolean isPowered = state.getValue(POWERED);
		boolean isInverted = state.getValue(INVERTED);
		return isPowered ^ isInverted;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
		final IBlockState state = world.getBlockState(pos);
		return isActive(state)? EMPTY : super.getSelectedBoundingBox(world, pos);
	}

}
