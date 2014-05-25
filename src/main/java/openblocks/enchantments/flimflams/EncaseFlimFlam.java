package openblocks.enchantments.flimflams;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import openblocks.api.IFlimFlamAction;

public class EncaseFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(EntityPlayerMP target) {

		int playerX = MathHelper.floor_double(target.posX);
		int playerY = MathHelper.floor_double(target.boundingBox.minY) - 1;
		int playerZ = MathHelper.floor_double(target.posZ);

		for (int y = playerY; y <= playerY + 3; y++) {
			for (int x = playerX - 1; x <= playerX + 1; x++) {
				for (int z = playerZ - 1; z <= playerZ + 1; z++) {

					boolean isGap = y < playerY + 3 &&
							x == playerX &&
							z == playerZ;

					if (!isGap && target.worldObj.isAirBlock(x, y, z)) {
						target.worldObj.setBlock(x, y, z, Blocks.dirt);
					}
				}
			}
		}

		boolean doTorch = target.worldObj.isAirBlock(playerX, playerY + 2, playerZ) &&
				Blocks.torch.canPlaceBlockAt(target.worldObj, playerX, playerY + 2, playerZ);

		if (doTorch) {
			target.worldObj.setBlock(playerX, playerY + 2, playerZ, Blocks.torch);
		}

		return true;
	}

}
