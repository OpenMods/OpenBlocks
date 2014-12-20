package openblocks.common.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.OpenBlocks;
import openmods.block.BlockRotationMode;
import openmods.infobook.BookDocumentation;
import openmods.utils.BlockNotifyFlags;
import openmods.utils.BlockUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation
public class BlockRopeLadder extends OpenBlock {

	public static final float RENDER_THICKNESS = 1.0f / 64.0f;
	private static final float COLLISION_THICKNESS = 1.0f / 16.0f;

	public BlockRopeLadder() {
		super(Material.circuits);
		setHardness(0.4F);
		setStepSound(soundTypeLadder);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderIdFlat;
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		return true;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB bb, List list, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			int meta = world.getBlockMetadata(x, y, z);
			ForgeDirection rotation = getRotation(meta);
			ForgeDirection playerRotation = BlockUtils.get2dOrientation((EntityLivingBase)entity);
			if (rotation == playerRotation) {
				super.addCollisionBoxesToList(world, x, y, z, bb, list, entity);
			}
		} else {
			super.addCollisionBoxesToList(world, x, y, z, bb, list, entity);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		getBlockBounds(world, x, y, z, COLLISION_THICKNESS);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		getBlockBounds(world, x, y, z, RENDER_THICKNESS);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		getBlockBounds(world, x, y, z, RENDER_THICKNESS);
	}

	private void getBlockBounds(IBlockAccess world, int x, int y, int z, float thickness) {
		int meta = world.getBlockMetadata(x, y, z);

		ForgeDirection direction = getRotation(meta);

		switch (direction) {
			case EAST:
				setBlockBounds(1 - thickness, 0, 0, 1, 1, 1);
				break;
			case WEST:
				setBlockBounds(0, 0, 0, thickness, 1, 1);
				break;
			case NORTH:
				setBlockBounds(0, 0, 0, 1, 1, thickness);
				break;
			case SOUTH:
				setBlockBounds(0, 0, 1 - thickness, 1, 1, 1);
				break;
			default:
				setBlockBounds(0, 0, 1 - thickness, 1, 1, 1);
				break;
		}
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) {
		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = getRotation(meta);

		if (world.isAirBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) {
			if (world.getBlock(x, y + 1, z) != this) world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		y--;

		if (y > 0) {
			Block bottomBlock = world.getBlock(x, y, z);
			if (bottomBlock == block) world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public void afterBlockPlaced(World world, EntityPlayer player, ItemStack stack, int x, int y, int z, ForgeDirection side, ForgeDirection blockDir, float hitX, float hitY, float hitZ, int itemMeta) {
		super.afterBlockPlaced(world, player, stack, x, y, z, side, blockDir, hitX, hitY, hitZ, itemMeta);
		final int blockMeta = blockRotationMode.toValue(blockDir);
		while (--y > 0 && stack.stackSize > 1) {
			if (world.isAirBlock(x, y, z) && canPlaceBlockOnSide(world, x, y, z, blockDir)) {
				world.setBlock(x, y, z, this, blockMeta, BlockNotifyFlags.ALL);
				stack.stackSize--;
			}
			else return;
		}
	}

}
