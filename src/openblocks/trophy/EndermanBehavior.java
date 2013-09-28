package openblocks.trophy;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import openblocks.common.tileentity.TileEntityTrophy;

public class EndermanBehavior implements ITrophyBehavior {

	@Override
	public void executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		if (tile.worldObj.isRemote) { return; }
		double d0 = player.posX + (tile.worldObj.rand.nextDouble() - 0.5D) * 8.0D;
		double d1 = player.posY + (tile.worldObj.rand.nextInt(16) - 8);
		double d2 = player.posZ + (tile.worldObj.rand.nextDouble() - 0.5D) * 8.0D;
		teleportTo(tile, player, d0, d1, d2);
	}

	protected void teleportTo(TileEntity tile, EntityPlayer player, double par1, double par3, double par5) {
		double d3 = player.posX;
		double d4 = player.posY;
		double d5 = player.posZ;
		player.posX = par1;
		player.posY = par3;
		player.posZ = par5;
		boolean flag = false;
		int i = MathHelper.floor_double(player.posX);
		int j = MathHelper.floor_double(player.posY);
		int k = MathHelper.floor_double(player.posZ);
		int l;

		if (tile.worldObj.blockExists(i, j, k)) {
			boolean flag1 = false;

			while (!flag1 && j > 0) {
				l = tile.worldObj.getBlockId(i, j - 1, k);

				if (l != 0 && Block.blocksList[l].blockMaterial.blocksMovement()) {
					flag1 = true;
				} else {
					--player.posY;
					--j;
				}
			}

			if (flag1) {
				player.setPositionAndUpdate(player.posX, player.posY, player.posZ);

				if (tile.worldObj.getCollidingBoundingBoxes(player, player.boundingBox).isEmpty() && !tile.worldObj.isAnyLiquid(player.boundingBox)) {
					flag = true;
				}
			}
		}

		if (!flag) {
			player.setPositionAndUpdate(d3, d4, d5);
		} else {
			short short1 = 128;

			for (l = 0; l < short1; ++l) {
				double d6 = l / (short1 - 1.0D);
				float f = (tile.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
				float f1 = (tile.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
				float f2 = (tile.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
				double d7 = d3 + (player.posX - d3) * d6 + (tile.worldObj.rand.nextDouble() - 0.5D) * player.width * 2.0D;
				double d8 = d4 + (player.posY - d4) * d6 + tile.worldObj.rand.nextDouble() * player.height;
				double d9 = d5 + (player.posZ - d5) * d6 + (tile.worldObj.rand.nextDouble() - 0.5D) * player.width * 2.0D;
				tile.worldObj.spawnParticle("portal", d7, d8, d9, f, f1, f2);
			}

			tile.worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
			player.playSound("mob.endermen.portal", 1.0F, 1.0F);
		}
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}
}
