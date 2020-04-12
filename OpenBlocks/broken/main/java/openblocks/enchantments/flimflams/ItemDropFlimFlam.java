package openblocks.enchantments.flimflams;

import java.util.Random;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import openblocks.api.IFlimFlamAction;

public class ItemDropFlimFlam implements IFlimFlamAction {

	private static final Random random = new Random();

	@Override
	public boolean execute(ServerPlayerEntity target) {
		boolean dropped = false;

		for (int i = 0; i < 4; i++)
			dropped |= tryDropStack(target, 36 + i);

		dropped |= tryDropStack(target, target.inventory.currentItem);

		return dropped;
	}

	protected boolean tryDropStack(ServerPlayerEntity target, int slot) {
		final PlayerInventory inv = target.inventory;
		ItemStack stack = inv.getStackInSlot(slot);
		if (stack.isEmpty() || random.nextFloat() > 0.5f) return false;

		target.dropItem(inv.decrStackSize(slot, 1), true);
		return true;
	}

}
