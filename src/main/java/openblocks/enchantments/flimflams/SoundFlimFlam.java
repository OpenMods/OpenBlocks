package openblocks.enchantments.flimflams;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import openblocks.api.IFlimFlamAction;
import openmods.utils.CollectionUtils;

public class SoundFlimFlam implements IFlimFlamAction {

	private static final List<String> sounds = ImmutableList.of(
			"openblocks:annoying.mosquito",
			"openblocks:annoying.alarmclock",
			"openblocks:annoying.vibrate",
			"openblocks:best.feature.ever.fart",
			"game.tnt.primed",
			"random.explode",
			"random.break",
			"mob.blaze.breathe",
			"mob.endermen.stare",
			"mob.ghast.charge",
			"mob.zombiepig.zpigangry",
			"mob.creeper.say"
			);

	@Override
	public boolean execute(EntityPlayerMP target) {
		String sound = CollectionUtils.getRandom(sounds);
		target.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(sound, target.posX, target.posY, target.posZ, 1, 1));
		return true;
	}

}
