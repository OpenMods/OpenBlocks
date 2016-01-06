package openblocks.common.block;

import java.util.List;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityImaginary.Property;
import openmods.block.OpenBlock;

import com.google.common.collect.Lists;

public class BlockImaginary extends OpenBlock {

	private static final Material imaginaryMaterial = new Material(MapColor.airColor);

	public static final SoundType drawingSounds = new SoundType("cloth", 0.5f, 1.0f) {
		@Override
		public String getBreakSound() {
			return "openblocks:crayon.place";
		}

		@Override
		public String getPlaceSound() {
			return "openblocks:crayon.place";
		}
	};

	public BlockImaginary() {
		super(imaginaryMaterial);
		setHardness(0.3f);
		stepSound = drawingSounds;
	}

	@Override
	public int getRenderType() {
		return 2; // TESR only
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
		if (world.isRemote) {
			TileEntityImaginary te = getTileEntity(world, pos, TileEntityImaginary.class);
			if (te != null && te.is(Property.SELECTABLE)) return te.getSelectionBox();
		}

		return AxisAlignedBB.fromBounds(0, 0, 0, 0, 0, 0);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		return null;
	}

	@Override
	public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> result, Entity entity) {
		TileEntityImaginary te = getTileEntity(world, pos, TileEntityImaginary.class);
		if (te != null && te.is(Property.SOLID, entity)) te.addCollisions(mask, result);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, BlockPos pos) {
		TileEntityImaginary te = getTileEntity(access, pos, TileEntityImaginary.class);
		if (te != null && te.is(Property.SELECTABLE)) {
			AxisAlignedBB aabb = te.getBlockBounds();
			minX = aabb.minX;
			minY = aabb.minY;
			minZ = aabb.minZ;

			maxX = aabb.maxX;
			maxY = aabb.maxY;
			maxZ = aabb.maxZ;
		}
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
		if (world.isRemote) {
			TileEntityImaginary te = getTileEntity(world, pos, TileEntityImaginary.class);
			if (te == null || !te.is(Property.SELECTABLE)) return null;
		}

		return super.collisionRayTrace(world, pos, start, end);
	}

	@Override
	public boolean isOpaqueCube() {
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
