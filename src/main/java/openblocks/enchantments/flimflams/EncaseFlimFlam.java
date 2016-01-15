package openblocks.enchantments.flimflams;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import openblocks.api.IFlimFlamAction;

public class EncaseFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(EntityPlayerMP target) {

		int playerX = MathHelper.floor_double(target.posX);
		int playerY = MathHelper.floor_double(target.getEntityBoundingBox().minY) - 1;
		int playerZ = MathHelper.floor_double(target.posZ);

		for (int y = playerY; y <= playerY + 3; y++) {
			for (int x = playerX - 1; x <= playerX + 1; x++) {
				for (int z = playerZ - 1; z <= playerZ + 1; z++) {

					boolean isGap = y < playerY + 3 &&
							x == playerX &&
							z == playerZ;

					final BlockPos pos = new BlockPos(x, y, z);
					if (!isGap && target.worldObj.isAirBlock(pos)) {
						target.worldObj.setBlockState(pos, Blocks.dirt.getDefaultState());
					}
				}
			}
		}

		final BlockPos torchPos = new BlockPos(playerX, playerY + 2, playerZ);
		boolean doTorch = target.worldObj.isAirBlock(torchPos) &&
				Blocks.torch.canPlaceBlockAt(target.worldObj, torchPos);

		if (doTorch) {
			target.worldObj.setBlockState(torchPos, Blocks.torch.getDefaultState());
		}

		return true;
	}

}
