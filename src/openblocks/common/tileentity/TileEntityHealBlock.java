package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityHealBlock extends TileEntity {

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote)
			return;
		List<EntityPlayer> playersOnTop = (List<EntityPlayer>) worldObj
				.getEntitiesWithinAABB(
						EntityPlayer.class,
						AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord,
								zCoord, xCoord + 1, yCoord + 2, zCoord + 1));
		if (worldObj.getTotalWorldTime() % 20 == 0) {
			for (EntityPlayer player : playersOnTop) {
				if (!player.capabilities.isCreativeMode) {
					if (player.getHealth() < player.maxHealth)
						player.heal(1);
					if (player.getFoodStats().needFood())
						player.getFoodStats().setFoodLevel(
								player.getFoodStats().getFoodLevel() + 1);
				}
			}
		}
	}
}
