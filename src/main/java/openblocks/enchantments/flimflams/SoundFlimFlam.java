package openblocks.enchantments.flimflams;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import openblocks.api.IFlimFlamAction;
import openmods.utils.CollectionUtils;

import com.google.common.collect.ImmutableList;

public class SoundFlimFlam implements IFlimFlamAction {

	private static final List<String> sounds = ImmutableList.of(
			"openblocks:mosquito",
			"openblocks:alarmclock",
			"openblocks:vibrate",
			"openblocks:fart",
			"random.fuse",
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
		target.playerNetServerHandler.sendPacketToPlayer(new Packet62LevelSound(sound, target.posX, target.posY, target.posZ, 1, 1));
		return true;
	}

}
