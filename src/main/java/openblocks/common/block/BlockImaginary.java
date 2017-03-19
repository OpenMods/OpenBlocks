package openblocks.common.block;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.OpenBlocks.Sounds;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityImaginary.Property;
import openmods.block.OpenBlock;

public class BlockImaginary extends OpenBlock {

	private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	private static final Material IMAGINARY = new Material(MapColor.AIR);

	public static final SoundType NOISES_IN_YOUR_HEAD = new SoundType(0.5f, 1.0f,
			Sounds.ITEM_CRAYON_PLACE,
			Sounds.ITEM_CRAYON_PLACE,
			Sounds.ITEM_CRAYON_PLACE,
			Sounds.ITEM_CRAYON_PLACE,
			Sounds.ITEM_CRAYON_PLACE);

	public BlockImaginary() {
		super(IMAGINARY);
		setHardness(0.3f);
		setSoundType(NOISES_IN_YOUR_HEAD);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		if (world.isRemote) {
			TileEntityImaginary te = getTileEntity(world, pos, TileEntityImaginary.class);
			if (te != null && te.is(Property.SELECTABLE)) return te.getSelectionBox();
		}

		return EMPTY_AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity) {
		TileEntityImaginary te = getTileEntity(world, pos, TileEntityImaginary.class);
		if (te != null && te.is(Property.SOLID, entity)) te.addCollisions(entityBox, collidingBoxes);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileEntityImaginary te = getTileEntity(source, pos, TileEntityImaginary.class);
		if (te != null && te.is(Property.SELECTABLE))
			return te.getBlockBounds();

		return EMPTY_AABB;
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
		// TODO verify if this is needed
		if (world.isRemote) {
			TileEntityImaginary te = getTileEntity(world, pos, TileEntityImaginary.class);
			if (te == null || !te.is(Property.SELECTABLE)) return null;
		}

		return null;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		return Lists.newArrayList();
	}

	@Override
	protected boolean suppressPickBlock() {
		return true;
	}
}
