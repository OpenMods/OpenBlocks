package openblocks.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class InventoryEvent extends PlayerEvent {

	protected final Map<String, SubInventory> subInventories;

	protected InventoryEvent(PlayerEntity player, Map<String, SubInventory> subInventories) {
		super(player);
		this.subInventories = subInventories;
	}

	public Map<String, SubInventory> getSubInventories() {
		return ImmutableMap.copyOf(subInventories);
	}

	public static class SubInventory {
		private final Map<Integer, ItemStack> slots = Maps.newHashMap();

		public SubInventory addItemStack(int slot, @Nonnull ItemStack stack) {
			slots.put(slot, stack);
			return this;
		}

		@Nonnull
		public ItemStack getItemStack(int slot) {
			return slots.get(slot);
		}

		public Map<Integer, ItemStack> asMap() {
			return ImmutableMap.copyOf(slots);
		}
	}

	public static class Store extends InventoryEvent {

		public Store(PlayerEntity player) {
			super(player, new HashMap<>());
		}

		public SubInventory createSubInventory(String id) {
			final SubInventory result = new SubInventory();
			SubInventory prev = subInventories.put(id, result);
			Preconditions.checkState(prev == null, "Sub inventory with id %s already exists", id);
			return result;
		}

	}

	public static class Load extends InventoryEvent {

		public Load(PlayerEntity player, Map<String, SubInventory> subInventories) {
			super(player, ImmutableMap.copyOf(subInventories));
		}

		public SubInventory getSubInventory(String id) {
			return subInventories.get(id);
		}
	}

}
