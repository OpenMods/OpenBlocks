package openblocks.enchantments.flimflams;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import openblocks.api.IFlimFlamAction;

public class BaneFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(EntityPlayerMP target) {
		for (ItemStack stack : target.inventory.mainInventory) {
			if (stack != null && stack.getMaxStackSize() == 1 && !stack.isItemEnchantable() && !stack.isItemEnchanted()) {
				stack.addEnchantment(Enchantment.baneOfArthropods, 5);
				return true;
			}
		}
		return false;
	}

}
