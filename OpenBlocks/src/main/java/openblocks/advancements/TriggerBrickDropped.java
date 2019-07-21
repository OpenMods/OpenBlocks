package openblocks.advancements;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;

public class TriggerBrickDropped implements ICriterionTrigger<TriggerBrickDropped.Instance> {

	private static final ResourceLocation ID = OpenBlocks.location("brick_dropped");

	public static class Instance extends AbstractCriterionInstance {
		public Instance() {
			super(ID);
		}
	}

	private static final Instance INSTANCE = new Instance();

	private final SetMultimap<PlayerAdvancements, Listener<TriggerBrickDropped.Instance>> listeners = HashMultimap.create();

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
		return INSTANCE;
	}

	public void trigger(ServerPlayerEntity player) {
		final PlayerAdvancements advancements = player.getAdvancements();
		listeners.get(advancements).forEach((listener) -> listener.grantCriterion(advancements));
	}
}
