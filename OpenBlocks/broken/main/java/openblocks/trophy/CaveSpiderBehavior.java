package openblocks.trophy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.potion.EffectInstance;
import openblocks.common.tileentity.TileEntityTrophy;

public class CaveSpiderBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player) {
		player.addPotionEffect(new EffectInstance(Effects.POISON, 200, 3));
		return 0;
	}
}
