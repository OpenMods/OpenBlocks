package openblocks.enchantments.flimflams;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import openblocks.api.IFlimFlamAction;

public class EncaseFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(ServerPlayerEntity target) {

		int playerX = MathHelper.floor(target.posX);
		int playerY = MathHelper.floor(target.getEntityBoundingBox().minY) - 1;
		int playerZ = MathHelper.floor(target.posZ);

		for (int y = playerY; y <= playerY + 3; y++) {
			for (int x = playerX - 1; x <= playerX + 1; x++) {
				for (int z = playerZ - 1; z <= playerZ + 1; z++) {

					boolean isGap = y < playerY + 3 &&
							x == playerX &&
							z == playerZ;

					final BlockPos pos = new BlockPos(x, y, z);
					if (!isGap && target.world.isAirBlock(pos)) {
						target.world.setBlockState(pos, Blocks.DIRT.getDefaultState());
					}
				}
			}
		}

		final BlockPos torchPos = new BlockPos(playerX, playerY + 2, playerZ);
		boolean doTorch = target.world.isAirBlock(torchPos) &&
				Blocks.TORCH.canPlaceBlockAt(target.world, torchPos);

		if (doTorch) {
			target.world.setBlockState(torchPos, Blocks.TORCH.getDefaultState());
		}

		return true;
	}

}
