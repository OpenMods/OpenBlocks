package openblocks.enchantments.flimflams;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import openblocks.OpenBlocks;
import openblocks.api.IFlimFlamAction;
import openmods.utils.CollectionUtils;

public class SoundFlimFlam implements IFlimFlamAction {

	private static final List<SoundEvent> sounds = ImmutableList.of(
			OpenBlocks.Sounds.MISC_MOSQUITO,
			OpenBlocks.Sounds.MISC_ALARM_CLOCK,
			OpenBlocks.Sounds.MISC_VIBRATE,
			OpenBlocks.Sounds.PLAYER_WHOOPS,
			SoundEvents.ENTITY_TNT_PRIMED,
			SoundEvents.ENTITY_GENERIC_EXPLODE,
			SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE,
			SoundEvents.ENTITY_BLAZE_SHOOT,
			SoundEvents.ENTITY_ENDERMEN_STARE,
			SoundEvents.ENTITY_GHAST_SCREAM,
			SoundEvents.ENTITY_GHAST_SHOOT,
			SoundEvents.ENTITY_ZOMBIE_PIG_ANGRY,
			SoundEvents.ENTITY_CREEPER_PRIMED);

	@Override
	public boolean execute(ServerPlayerEntity target) {
		SoundEvent sound = CollectionUtils.getRandom(sounds);
		target.connection.sendPacket(new SPlaySoundEffectPacket(sound, SoundCategory.MASTER, target.posX, target.posY, target.posZ, 1, 1));
		return true;
	}

}
