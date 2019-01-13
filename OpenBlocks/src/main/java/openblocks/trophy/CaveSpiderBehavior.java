package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import openblocks.common.tileentity.TileEntityTrophy;

public class CaveSpiderBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		player.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 3));
		return 0;
	}
}
