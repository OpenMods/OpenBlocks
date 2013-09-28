package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import openblocks.OpenBlocks;

public class TileEntityHealBlock extends OpenTileEntity {

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (worldObj.isRemote) return;

		@SuppressWarnings("unchecked")
		List<EntityPlayer> playersOnTop = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1));
		if (OpenBlocks.proxy.getTicks(worldObj) % 20 == 0) {
			for (EntityPlayer player : playersOnTop) {
				if (!player.capabilities.isCreativeMode) {
					/*
					 * TODO: the potion effects are set to 1 tick only to give
					 * enough time for the player to regenerate, but without
					 * having any overkill However, this does have the
					 * side-effect of not showing particle effects. Personally,
					 * I wish that the player could see effects, but I think
					 * someone else should ultimately decide if it should be
					 * done (you know who you are)
					 */
					player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 1, 10));
					player.addPotionEffect(new PotionEffect(23, 1)); // Saturation
					/*
					 * TODO: the saturation potion does not yet have a legible
					 * name, so I'm using its ID value At the moment, this
					 * potion is under the name Potion.field_76443_y. Some name,
					 * eh?
					 */
				}
			}
		}
	}

}
