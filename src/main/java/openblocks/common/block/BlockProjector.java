package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openmods.block.OpenBlock;

public class BlockProjector extends OpenBlock {

	public static final int META_BIT_ACTIVE = 0;

	private static final float SLAB_HEIGHT = 0.5F;

	private static final int MIN_LIGHT_LEVEL = 0;
	private static final int MAX_LIGHT_LEVEL = 15;
	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	private static final int MASK_ACTIVE = 1;

	public BlockProjector() {
		super(Material.IRON);
		setDefaultState(getDefaultState().withProperty(ACTIVE, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { getPropertyOrientation(), ACTIVE });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(ACTIVE, (meta & MASK_ACTIVE) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ACTIVE)? MASK_ACTIVE : 0;
	}

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, SLAB_HEIGHT, 1.0);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public int getLightValue(IBlockState state) {
		if (state.getValue(ACTIVE))
			return Math.min(Math.max(MIN_LIGHT_LEVEL, Config.projectorLightLevelValue), MAX_LIGHT_LEVEL);

		return 0;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	// TODO 1.10 figure rendering of this thing. Duh
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
}
