package openblocks.enchantments.flimflams;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import openblocks.api.IFlimFlamEffect;

public class EncaseFlimFlam implements IFlimFlamEffect {

	@Override
	public boolean execute(EntityPlayer target) {

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
						target.worldObj.setBlock(x, y, z, Block.dirt.blockID);
					}
				}
			}
		}

		boolean doTorch = target.worldObj.isAirBlock(playerX, playerY + 2, playerZ) &&
				Block.torchWood.canPlaceBlockAt(target.worldObj, playerX, playerY + 2, playerZ);

		if (doTorch) {
			target.worldObj.setBlock(playerX, playerY + 2, playerZ, Block.torchWood.blockID);
		}

		return true;
	}

	@Override
	public String name() {
		return "encase";
	}

	@Override
	public int weight() {
		return 20;
	}

	@Override
	public int cost() {
		return 50;
	}

	@Override
	public boolean isSilent() {
		return false;
	}

}
