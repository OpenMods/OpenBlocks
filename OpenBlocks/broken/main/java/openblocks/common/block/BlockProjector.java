package openblocks.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
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
		return new ExtendedBlockState(this, new IProperty[] { getPropertyOrientation(), ACTIVE },
				new IUnlistedProperty[] { Properties.AnimationProperty });
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(ACTIVE, (meta & MASK_ACTIVE) != 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(ACTIVE)? MASK_ACTIVE : 0;
	}

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, SLAB_HEIGHT, 1.0);

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public int getLightValue(BlockState state) {
		if (state.getValue(ACTIVE))
			return Math.min(Math.max(MIN_LIGHT_LEVEL, Config.projectorLightLevelValue), MAX_LIGHT_LEVEL);

		return 0;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(BlockState state) {
		return false;
	}

	@Override
	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.SOLID || (state.getValue(ACTIVE) && layer == BlockRenderLayer.TRANSLUCENT);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return face == Direction.DOWN? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}
}
