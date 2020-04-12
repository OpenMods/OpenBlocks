package openblocks.advancements;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.stream.Collectors;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;

public class TriggerDevNullStack implements ICriterionTrigger<TriggerDevNullStack.Instance> {

	private static final ResourceLocation ID = OpenBlocks.location("dev_null_stacked");

	public static class Instance extends AbstractCriterionInstance {

		public final int depth;

		public Instance(int depth) {
			super(ID);
			this.depth = depth;
		}

		private boolean test(int depth) {
			return this.depth <= depth;
		}
	}

	private final SetMultimap<PlayerAdvancements, Listener<TriggerDevNullStack.Instance>> listeners = HashMultimap.create();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
		listeners.put(playerAdvancementsIn, listener);
	}

	@Override
	public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener) {
		listeners.remove(playerAdvancementsIn, listener);
	}

	@Override
	public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
		listeners.removeAll(playerAdvancementsIn);
	}

	@Override
	public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		final int depth = JSONUtils.getInt(json, "depth");
		return new Instance(depth);
	}

	public void trigger(ServerPlayerEntity player, int depth) {
		final PlayerAdvancements advancements = player.getAdvancements();
		listeners.get(advancements).stream()
				.filter((listener) -> listener.getCriterionInstance().test(depth))
				.collect(Collectors.toList()) // force evaluation
				.forEach((listener) -> listener.grantCriterion(advancements));
	}
}
