package openblocks.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ILootGenerator;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.OpenBlocks;

@EventBusSubscriber
public class LootHandler {

	private static ResourceLocation mc(String location) {
		return new ResourceLocation("minecraft", location);
	}

	private static class LootInjection {
		private final ResourceLocation location;

		public LootInjection(ResourceLocation location) {
			this.location = location;
		}

		public boolean canInject() {
			return true;
		}
	}

	private static class TechnicolorGlassesLootInjection extends LootInjection {

		public TechnicolorGlassesLootInjection() {
			super(OpenBlocks.location("inject/technicolor_glasses"));
		}

		@Override
		public boolean canInject() {
			return Config.technicolorGlassesLoot && OpenBlocks.Items.technicolorGlasses != null;
		}
	}

	private static final Map<ResourceLocation, LootInjection> injections = ImmutableMap.of(
			mc("chests/abandoned_mineshaft"), new TechnicolorGlassesLootInjection(),
			mc("chests/simple_dungeon"), new TechnicolorGlassesLootInjection());

	public static void register() {
		final Set<ResourceLocation> alreadyRegistered = Sets.newHashSet();
		for (LootInjection injection : injections.values())
			if (injection.canInject() && alreadyRegistered.add(injection.location))
				LootTables.register(injection.location);
	}

	@SubscribeEvent
	public static void onLootTableLoad(LootTableLoadEvent evt) {
		final LootInjection injection = injections.get(evt.getName());
		if (injection != null && injection.canInject()) {
			evt.getTable().addPool(createPool(injection.location));
		}
	}

	private static LootPool createPool(ResourceLocation injectionEntry) {
		return new LootPool(new ILootGenerator[] { loadEntry(injectionEntry) }, new ILootCondition[0], new RandomValueRange(1), new RandomValueRange(0, 1), "openmods_inject_pool");
	}

	private static ILootGenerator loadEntry(ResourceLocation injectionEntry) {
		return new TableLootEntry(injectionEntry, 1, 0, new ILootCondition[0], "openmods_inject_entry");
	}

}
