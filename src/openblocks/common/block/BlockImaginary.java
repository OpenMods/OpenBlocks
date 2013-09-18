package openblocks.common.block;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemImaginary;
import openblocks.common.tileentity.TileEntityBigButton;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityImaginary.Property;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockImaginary extends OpenBlock {

	public Icon texturePencil;
	public Icon textureCrayon;

	public BlockImaginary() {
		super(Config.blockImaginaryId, Material.glass);
		setupBlock(this, "imaginary", TileEntityImaginary.class);
		setHardness(5);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		if (world.isRemote) {
			TileEntityImaginary te = getTileEntity(world, x, y, z, TileEntityImaginary.class);
			if (te != null && te.is(Property.SELECTABLE)) return AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1, y + 1, z + 1);
		}

		return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB region, List result, Entity entity) {
		TileEntityImaginary te = getTileEntity(world, x, y, z, TileEntityImaginary.class);
		if (te != null && te.is(Property.SOLID, entity)) {
			AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1, y + 1, z + 1);

			if (aabb != null && aabb.intersectsWith(region)) result.add(aabb);
		}
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 par5Vec3, Vec3 par6Vec3) {
		if (world.isRemote) {
			TileEntityImaginary te = getTileEntity(world, x, y, z, TileEntityImaginary.class);
			if (te == null || !te.is(Property.SELECTABLE)) return null;
		}

		return super.collisionRayTrace(world, x, y, z, par5Vec3, par6Vec3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		super.registerIcons(registry);
		blockIcon = texturePencil = registry.registerIcon("openblocks:pencil");
		textureCrayon = registry.registerIcon("openblocks:crayon");
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
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		return Lists.newArrayList();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		TileEntityImaginary te = getTileEntity(world, x, y, z, TileEntityImaginary.class);
		if (te != null) {
			TileEntityImaginary img = (TileEntityImaginary)te;
			return ItemImaginary.setupValues(img.color, new ItemStack(this));
		}
		return null;
	}
}
