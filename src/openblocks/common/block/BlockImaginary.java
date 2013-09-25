package openblocks.common.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemImaginary;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityImaginary.Property;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockImaginary extends OpenBlock {

	public Icon texturePencilBlock;
	public Icon textureCrayonBlock;

	public Icon texturePencilPanel;
	public Icon textureCrayonPanel;

	public Icon texturePencilHalfPanel;
	public Icon textureCrayonHalfPanel;

	public BlockImaginary() {
		super(Config.blockImaginaryId, Material.glass);
		setupBlock(this, "imaginary", TileEntityImaginary.class,
				ItemImaginary.class);
		setHardness(0.3f);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x,
			int y, int z) {
		if (world.isRemote) {
			TileEntityImaginary te = getTileEntity(world, x, y, z,
					TileEntityImaginary.class);
			if (te != null && te.is(Property.SELECTABLE))
				return te.getSelectionBox();
		}

		return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x,
			int y, int z) {
		return null;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addCollisionBoxesToList(World world, int x, int y, int z,
			AxisAlignedBB region, List result, Entity entity) {
		TileEntityImaginary te = getTileEntity(world, x, y, z,
				TileEntityImaginary.class);
		if (te != null && te.is(Property.SOLID, entity))
			te.addCollisions(region, result);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y,
			int z) {
		TileEntityImaginary te = getTileEntity(access, x, y, z,
				TileEntityImaginary.class);
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
	public MovingObjectPosition collisionRayTrace(World world, int x, int y,
			int z, Vec3 par5Vec3, Vec3 par6Vec3) {
		if (world.isRemote) {
			TileEntityImaginary te = getTileEntity(world, x, y, z,
					TileEntityImaginary.class);
			if (te == null || !te.is(Property.SELECTABLE))
				return null;
		}

		return super.collisionRayTrace(world, x, y, z, par5Vec3, par6Vec3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		blockIcon = texturePencilBlock = registry
				.registerIcon("openblocks:pencilBlock");
		textureCrayonBlock = registry.registerIcon("openblocks:crayonBlock");

		texturePencilPanel = registry.registerIcon("openblocks:pencilPanel");
		textureCrayonPanel = registry.registerIcon("openblocks:crayonPanel");

		texturePencilHalfPanel = registry
				.registerIcon("openblocks:pencilHalfPanel");
		textureCrayonHalfPanel = registry
				.registerIcon("openblocks:crayonHalfPanel");
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y,
			int z, int metadata, int fortune) {
		return Lists.newArrayList();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world,
			int x, int y, int z) {
		TileEntityImaginary te = getTileEntity(world, x, y, z,
				TileEntityImaginary.class);
		if (te != null) {
			return ItemImaginary.setupValues(te.color, new ItemStack(this));
		}
		return null;
	}
}
