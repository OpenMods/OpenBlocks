package openblocks.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.item.ItemStack;

import openblocks.sync.SyncableFlags;

public class SlotSideHelper {
	
	public HashMap<Integer, SyncableFlags> slotsForSides = new HashMap<Integer, SyncableFlags>();
	
	public SlotSideHelper() {
		
	}
	public void addMapping(Enum<?> slot, SyncableFlags side) {
		slotsForSides.put(slot.ordinal(), side);
	}
	
	public void addMapping(int slot, SyncableFlags side) {
		slotsForSides.put(slot, side);
	}
	
	public int[] getSlotsForSide(int side) {
		Set<Integer> slots = new HashSet<Integer>();
		for (Entry<Integer, SyncableFlags> entry : slotsForSides.entrySet()) {
			if (entry.getValue().get(side)) {
				slots.add(entry.getKey());
			}
		}
		int[] ret = new int[slots.size()];
		int i = 0;
		for (int k : slots)
			ret[i++] = k;
		return ret;
	}
	
	public boolean canInsertItem(int slot, int side) {
		if (!slotsForSides.containsKey(slot)) {
			return false;
		}
		return slotsForSides.get(slot).get(side);
	}

	public boolean canExtractItem(int slot, int side) {
		return canInsertItem(slot, side);
	}
}
