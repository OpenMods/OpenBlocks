package openblocks.enchantments.flimflams;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import openblocks.api.IFlimFlamEffect;

public class BaneFlimFlam implements IFlimFlamEffect {

	@Override
	public boolean execute(EntityPlayer target) {
		for (ItemStack stack : target.inventory.mainInventory) {
			if (stack != null && stack.getMaxStackSize() == 1 && !stack.isItemEnchantable() && !stack.isItemEnchanted()) {
				stack.addEnchantment(Enchantment.baneOfArthropods, 5);
				return true;
			}
		}
		return false;
	}

	@Override
	public String name() {
		return "bane";
	}

	@Override
	public int weight() {
		return 20;
	}

	@Override
	public int cost() {
		return 50;
	}

	@Override
	public boolean isSilent() {
		return false;
	}

}
