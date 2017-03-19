package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import openblocks.common.tileentity.TileEntityTrophy;

public class WitchBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 70, 1));
		player.addChatMessage(new TextComponentTranslation("openblocks.misc.get_witched"));
		return 0;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
