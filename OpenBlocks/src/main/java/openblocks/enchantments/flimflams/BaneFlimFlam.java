package openblocks.enchantments.flimflams;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import openblocks.api.IFlimFlamAction;

public class BaneFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(ServerPlayerEntity target) {
		for (ItemStack stack : target.inventory.mainInventory) {
			if (!stack.isEmpty() && stack.getMaxStackSize() == 1 && !stack.isItemEnchantable() && !stack.isItemEnchanted()) {
				stack.addEnchantment(Enchantments.BANE_OF_ARTHROPODS, 5);
				return true;
			}
		}
		return false;
	}

}
