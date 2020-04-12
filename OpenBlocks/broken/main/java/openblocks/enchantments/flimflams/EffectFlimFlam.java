package openblocks.enchantments.flimflams;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Effect;
import openblocks.api.IFlimFlamAction;

public class EffectFlimFlam implements IFlimFlamAction {

	private static final Random RANDOM = new Random();

	private final List<EffectMeta> EFFECTS = Lists.newArrayList();

	{
		EFFECTS.add(new EffectMeta(Effects.BLINDNESS, 1, 1, seconds(15), seconds(60)));
		EFFECTS.add(new EffectMeta(Effects.NAUSEA, 1, 1, seconds(15), seconds(60)));
		EFFECTS.add(new EffectMeta(Effects.MINING_FATIGUE, 50, 100, seconds(15), seconds(60)));

		EFFECTS.add(new EffectMeta(Effects.JUMP_BOOST, 30, 50, seconds(5), seconds(15)));
		EFFECTS.add(new EffectMeta(Effects.SPEED, 50, 100, seconds(5), seconds(15)));
		EFFECTS.add(new EffectMeta(Effects.SLOWNESS, 4, 7, seconds(5), seconds(15)));

		EFFECTS.add(new EffectMeta(Effects.LEVITATION, 1, 1, seconds(5), seconds(15)));
	}

	private static int seconds(int s) {
		return s * 20;
	}

	private static class EffectMeta {
		public final Effect potion;
		public final int levelMin;
		public final int levelRange;
		public final int durationMin;
		public final int durationRange;

		public EffectMeta(Effect potion, int levelMin, int levelMax, int durationMin, int durationMax) {
			this.potion = potion;
			this.levelMin = levelMin;
			this.levelRange = levelMax - levelMin + 1;
			this.durationMin = durationMin;
			this.durationRange = durationMax - durationMin + 1;
		}
	}

	@Override
	public boolean execute(ServerPlayerEntity target) {
		Collections.shuffle(EFFECTS);

		for (int i = 0; i < 2; i++) {
			EffectMeta selected = EFFECTS.get(i);
			int duration = selected.durationMin + RANDOM.nextInt(selected.durationRange);
			int level = selected.levelMin + RANDOM.nextInt(selected.levelRange);
			target.addPotionEffect(new EffectInstance(selected.potion, duration, level));
		}
		return true;
	}

}
