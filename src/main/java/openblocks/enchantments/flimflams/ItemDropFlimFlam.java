package openblocks.enchantments.flimflams;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import openblocks.api.IFlimFlamAction;

public class ItemDropFlimFlam implements IFlimFlamAction {

	private static final Random random = new Random();

	@Override
	public boolean execute(EntityPlayerMP target) {
		boolean dropped = false;

		for (int i = 0; i < 4; i++)
			dropped |= tryDropStack(target, 36 + i);

		dropped |= tryDropStack(target, target.inventory.currentItem);

		return dropped;
	}

	protected boolean tryDropStack(EntityPlayerMP target, int slot) {
		final InventoryPlayer inv = target.inventory;
		ItemStack stack = inv.getStackInSlot(slot);
		if (stack == null || random.nextFloat() > 0.5f) return false;

		target.dropItem(inv.decrStackSize(slot, 1));
		return true;
	}

}
