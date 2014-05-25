package openblocks.enchantments.flimflams;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.api.IFlimFlamAction;

public class SkyblockFlimFlam implements IFlimFlamAction {

	private static final ForgeDirection BUILD[] = new ForgeDirection[] {
			ForgeDirection.DOWN,
			ForgeDirection.EAST,
			ForgeDirection.NORTH,
			ForgeDirection.SOUTH,
			ForgeDirection.WEST,
	};

	@Override
	public boolean execute(EntityPlayerMP target) {
		final World world = target.worldObj;
		if (world.provider.hasNoSky || world.provider.isHellWorld) return false;

		int coordX = MathHelper.floor_double(target.posX);
		int currentY = MathHelper.floor_double(target.posY);
		int coordY = Math.min(currentY + 150, 250);
		int coordZ = MathHelper.floor_double(target.posZ);

		for (ForgeDirection d : BUILD)
			if (!world.isAirBlock(coordX + d.offsetX, coordY + d.offsetY, coordZ + d.offsetZ)) return false;

		for (ForgeDirection d : BUILD)
			world.setBlock(coordX + d.offsetX, coordY + d.offsetY, coordZ + d.offsetZ, Blocks.ice);

		target.setPositionAndUpdate(coordX + 0.5, coordY + 1, coordZ + 0.5);
		return true;
	}
}
