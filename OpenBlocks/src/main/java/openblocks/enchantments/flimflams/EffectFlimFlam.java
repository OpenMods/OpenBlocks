package openblocks.enchantments.flimflams;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import openblocks.api.IFlimFlamAction;

public class EffectFlimFlam implements IFlimFlamAction {

	private static final Random RANDOM = new Random();

	private final List<EffectMeta> EFFECTS = Lists.newArrayList();

	{
		EFFECTS.add(new EffectMeta(MobEffects.BLINDNESS, 1, 1, seconds(15), seconds(60)));
		EFFECTS.add(new EffectMeta(MobEffects.NAUSEA, 1, 1, seconds(15), seconds(60)));
		EFFECTS.add(new EffectMeta(MobEffects.MINING_FATIGUE, 50, 100, seconds(15), seconds(60)));

		EFFECTS.add(new EffectMeta(MobEffects.JUMP_BOOST, 30, 50, seconds(5), seconds(15)));
		EFFECTS.add(new EffectMeta(MobEffects.SPEED, 50, 100, seconds(5), seconds(15)));
		EFFECTS.add(new EffectMeta(MobEffects.SLOWNESS, 4, 7, seconds(5), seconds(15)));

		EFFECTS.add(new EffectMeta(MobEffects.LEVITATION, 1, 1, seconds(5), seconds(15)));
	}

	private static int seconds(int s) {
		return s * 20;
	}

	private static class EffectMeta {
		public final Potion potion;
		public final int levelMin;
		public final int levelRange;
		public final int durationMin;
		public final int durationRange;

		public EffectMeta(Potion potion, int levelMin, int levelMax, int durationMin, int durationMax) {
			this.potion = potion;
			this.levelMin = levelMin;
			this.levelRange = levelMax - levelMin + 1;
			this.durationMin = durationMin;
			this.durationRange = durationMax - durationMin + 1;
		}
	}

	@Override
	public boolean execute(EntityPlayerMP target) {
		Collections.shuffle(EFFECTS);

		for (int i = 0; i < 2; i++) {
			EffectMeta selected = EFFECTS.get(i);
			int duration = selected.durationMin + RANDOM.nextInt(selected.durationRange);
			int level = selected.levelMin + RANDOM.nextInt(selected.levelRange);
			target.addPotionEffect(new PotionEffect(selected.potion, duration, level));
		}
		return true;
	}

}
