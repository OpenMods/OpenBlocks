package openblocks.common;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityTorchArrow extends EntityArrow {

	public EntityTorchArrow(World worldObj, EntityPlayer player, float f) {
		super(worldObj, player, f);
	}

	public void onUpdate() {
		super.onUpdate();
		// TODO: Check that this works in an unmodified, out-of-debug-env client.
		if (this.ticksInGround == 1) {
			int closestX = (int) Math.round(posX);
			int closestY = (int) Math.round(posY);
			int closestZ = (int) Math.round(posZ);
			if (worldObj.isAirBlock(closestX, closestY, closestZ)) {
				if (shootingEntity != null
						&& shootingEntity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) shootingEntity;
					if (player.inventory.hasItem(Block.torchWood.blockID)) {
						player.inventory
								.consumeInventoryItem(Block.torchWood.blockID);
						worldObj.setBlock(closestX, closestY, closestZ,
								Block.torchWood.blockID);
						setDead();
					}
				}
			}
		}
	}

}
