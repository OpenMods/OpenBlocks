package openblocks.enchantments.flimflams;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
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
		int coordX = MathHelper.floor_double(target.posX);
		int currentY = MathHelper.floor_double(target.posY);
		int coordY = Math.min(currentY + 150, 250);
		int coordZ = MathHelper.floor_double(target.posZ);

		final World world = target.worldObj;

		for (ForgeDirection d : BUILD)
			if (!world.isAirBlock(coordX + d.offsetX, coordY + d.offsetY, coordZ + d.offsetZ)) return false;

		for (ForgeDirection d : BUILD)
			world.setBlock(coordX + d.offsetX, coordY + d.offsetY, coordZ + d.offsetZ, Block.ice.blockID);

		target.setPositionAndUpdate(coordX + 0.5, coordY + 1, coordZ + 0.5);
		return true;
	}
}
