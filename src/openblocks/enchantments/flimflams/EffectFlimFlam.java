package openblocks.enchantments.flimflams;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import openblocks.api.IFlimFlamAction;

import com.google.common.collect.Lists;

public class EffectFlimFlam implements IFlimFlamAction {

	private static final Random RANDOM = new Random();

	private final List<EffectMeta> EFFECTS = Lists.newArrayList();

	{
		EFFECTS.add(new EffectMeta(Potion.blindness, 1, 1, seconds(15), seconds(60)));
		EFFECTS.add(new EffectMeta(Potion.confusion, 1, 1, seconds(15), seconds(60)));
		EFFECTS.add(new EffectMeta(Potion.digSlowdown, 50, 100, seconds(15), seconds(60)));

		EFFECTS.add(new EffectMeta(Potion.jump, 30, 50, seconds(5), seconds(15)));
		EFFECTS.add(new EffectMeta(Potion.moveSpeed, 50, 100, seconds(5), seconds(15)));
		EFFECTS.add(new EffectMeta(Potion.moveSlowdown, 4, 7, seconds(5), seconds(15)));
	}

	private static int seconds(int s) {
		return s * 20;
	}

	private static class EffectMeta {
		public final int potionId;
		public final int levelMin;
		public final int levelRange;
		public final int durationMin;
		public final int durationRange;

		public EffectMeta(Potion potion, int levelMin, int levelMax, int durationMin, int durationMax) {
			this.potionId = potion.id;
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
			target.addPotionEffect(new PotionEffect(selected.potionId, duration, level));
		}
		return true;
	}

}
