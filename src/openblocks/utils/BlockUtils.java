package openblocks.utils;

import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockUtils {

	public static ForgeDirection get2dOrientation(EntityLiving entity) {
		int l = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
		switch (l) {
			case 0:
				return ForgeDirection.SOUTH;
			case 1:
				return ForgeDirection.WEST;
			case 2:
				return ForgeDirection.NORTH;
			case 3:
				return ForgeDirection.EAST;
		}
		return ForgeDirection.SOUTH;

	}

	public static float getRotationFromDirection(ForgeDirection direction) {
		switch (direction) {
			case NORTH:
				return 0F;
			case SOUTH:
				return 180F;
			case WEST:
				return 90F;
			case EAST:
				return -90F;
			case DOWN:
				return -90f;
			case UP:
				return 90f;
			default:
				return 0f;
		}
	}

	public static ForgeDirection get3dOrientation(EntityLiving entity) {
		if (entity.rotationPitch > 45.5F) {
			return ForgeDirection.DOWN;
		} else if (entity.rotationPitch < -45.5F) { return ForgeDirection.UP; }
		return get2dOrientation(entity);
	}

	public static void dropItemStackInWorld(World worldObj, int x, int y, int z, ItemStack stack) {
		dropItemStackInWorld(worldObj, (double)x, (double)y, (double)z, stack);
	}

	public static void dropItemStackInWorld(World worldObj, double x, double y, double z, ItemStack stack) {
		float f = 0.7F;
		double d0 = (double)(worldObj.rand.nextFloat() * f)
				+ (double)(1.0F - f) * 0.5D;
		double d1 = (double)(worldObj.rand.nextFloat() * f)
				+ (double)(1.0F - f) * 0.5D;
		double d2 = (double)(worldObj.rand.nextFloat() * f)
				+ (double)(1.0F - f) * 0.5D;
		EntityItem entityitem = new EntityItem(worldObj, (double)x + d0, (double)y
				+ d1, (double)z + d2, stack);
		entityitem.delayBeforeCanPickup = 10;
		if (stack.hasTagCompound()) {
			entityitem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
		}
		worldObj.spawnEntityInWorld(entityitem);
	}

	public static void dropTileInventory(TileEntity tileEntity) {

		if (tileEntity != null && tileEntity instanceof IInventory) {
			IInventory inventory = (IInventory)tileEntity;
			dropInventory(inventory, tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
		}
	}

	public static void dropInventory(IInventory inventory, World world, double x, double y, double z) {
		Random rand = world.rand;
		if (inventory == null) { return; }
		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			ItemStack itemStack = inventory.getStackInSlot(i);
			if (itemStack != null) {
				dropItemStackInWorld(world, x, y, z, itemStack);
			}
		}
	}

	public static void dropInventory(IInventory inventory, World world, int x, int y, int z) {
		dropInventory(inventory, world, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5);
	}

	public static TileEntity getTileInDirection(TileEntity tile, ForgeDirection direction) {
		int targetX = tile.xCoord + direction.offsetX;
		int targetY = tile.yCoord + direction.offsetY;
		int targetZ = tile.zCoord + direction.offsetZ;
		return tile.worldObj.getBlockTileEntity(targetX, targetY, targetZ);
	}
	
	public static ForgeDirection sideToDirection(int side) {
		ForgeDirection direction = ForgeDirection.UNKNOWN;
		// Bottom = 0, Top = 1, East = 2, West = 3, North = 4, South = 5.
		switch (side) {
			case 0:
				direction = ForgeDirection.DOWN;
				break;
			case 1:
				direction = ForgeDirection.UP;
				break;
			case 2:
				direction = ForgeDirection.NORTH;
				break;
			case 3:
				direction = ForgeDirection.SOUTH;
				break;
			case 4:
				direction = ForgeDirection.WEST;
				break;
			default:
			case 5:
				direction = ForgeDirection.EAST;
				break;
		}
		return direction;
	}
}
