package openblocks.trophy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.TranslationTextComponent;
import openblocks.common.tileentity.TileEntityTrophy;

public class WitchBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player) {
		player.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 70, 1));
		player.sendMessage(new TranslationTextComponent("openblocks.misc.get_witched"));
		return 0;
	}
}
