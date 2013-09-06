package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import openblocks.utils.CompatibilityUtils;

public class TileEntityHealBlock extends OpenTileEntity {

	int value = 0;

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (worldObj.isRemote) return;

		@SuppressWarnings("unchecked")
		List<EntityPlayer> playersOnTop = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1));
		if (worldObj.getTotalWorldTime() % 20 == 0) {
			for (EntityPlayer player : playersOnTop) {
				if (!player.capabilities.isCreativeMode) {
					if (CompatibilityUtils.getEntityHealth(player) < CompatibilityUtils.getEntityMaxHealth(player)) player.heal(1);
					if (player.getFoodStats().needFood()) player.getFoodStats().setFoodLevel(player.getFoodStats().getFoodLevel() + 1);
				}
			}
		}
	}

}
