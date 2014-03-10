package openblocks.enchantments.flimflams;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import openblocks.api.IFlimFlamEffect;
import openmods.utils.CollectionUtils;

import com.google.common.collect.ImmutableList;

public class SoundFlimFlam implements IFlimFlamEffect {

	private static final List<String> sounds = ImmutableList.of(
			"openblocks:mosquito",
			"openblocks:alarmclock",
			"openblocks:fart",
			"random.fuse",
			"random.explode",
			"random.break",
			"mob.blaze.breathe",
			"mob.endermen.stare",
			"mob.ghast.charge",
			"mob.zombiepig.zpigangry"
			);

	@Override
	public boolean execute(EntityPlayer target) {
		String sound = CollectionUtils.getRandom(sounds);
		target.worldObj.playSoundAtEntity(target, sound, 1, 1);
		return true;
	}

	@Override
	public String name() {
		return "sound";
	}

	@Override
	public float weight() {
		return 4;
	}

	@Override
	public float cost() {
		return 3;
	}

	@Override
	public boolean isSilent() {
		return true;
	}

}
