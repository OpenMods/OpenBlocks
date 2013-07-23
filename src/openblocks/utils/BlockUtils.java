package openblocks.utils;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
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

	public static ForgeDirection get3dOrientation(EntityLiving entity) {
		if (entity.rotationPitch > 66.5F) {
			return ForgeDirection.DOWN;
		} else if (entity.rotationPitch < -66.5F) {
			return ForgeDirection.UP;
		}
		return get2dOrientation(entity);
	}
}
