package openblocks.common.tileentity;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ITickable;
import openmods.OpenMods;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.BlockUtils;

public class TileEntityHealBlock extends OpenTileEntity implements ITickable {

	@Override
	public void update() {
		if (worldObj.isRemote) return;

		if (OpenMods.proxy.getTicks(worldObj) % 20 == 0) {
			List<EntityPlayer> playersOnTop = worldObj.getEntitiesWithinAABB(EntityPlayer.class, BlockUtils.expandAround(pos, 1, 2, 1));
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
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 1, 10));
					player.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 1));
				}
			}
		}
	}

}
