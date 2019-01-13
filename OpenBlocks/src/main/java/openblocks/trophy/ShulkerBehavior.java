package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import openblocks.common.tileentity.TileEntityTrophy;

public class ShulkerBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		player.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 100, 1));
		return 100;
	}
}
