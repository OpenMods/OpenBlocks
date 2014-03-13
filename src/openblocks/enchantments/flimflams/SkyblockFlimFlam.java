package openblocks.enchantments.flimflams;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;

public class SkyblockFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(EntityPlayerMP target) {
		int coordX = MathHelper.floor_double(target.posX);
		int currentY = MathHelper.floor_double(target.posY);
		int coordY = Math.min(currentY + 150, 250);
		int coordZ = MathHelper.floor_double(target.posZ);

		final World world = target.worldObj;

		for (int dy = 0; dy <= 2; dy++)
			if (!world.isAirBlock(coordX, coordY + dy, coordZ)) return false;

		world.setBlock(coordX, coordY, coordZ, Block.ice.blockID);

		target.setPositionAndUpdate(coordX + 0.5, coordY + 2, coordZ + 0.5);
		return true;
	}
}
