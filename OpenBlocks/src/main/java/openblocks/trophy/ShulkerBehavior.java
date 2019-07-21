package openblocks.trophy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import openblocks.common.tileentity.TileEntityTrophy;

public class ShulkerBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player) {
		player.addPotionEffect(new EffectInstance(Effects.LEVITATION, 100, 1));
		return 100;
	}
}
