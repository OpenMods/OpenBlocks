package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import openblocks.common.tileentity.TileEntityTrophy;

public class WitchBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		player.addPotionEffect(new PotionEffect(Potion.blindness.id, 70, 1));
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.get_witched"));
		return 0;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
