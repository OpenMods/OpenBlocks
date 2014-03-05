package openblocks.enchantments.flimflams;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import openblocks.api.IAttackFlimFlam;

public class BaneFlimFlam implements IAttackFlimFlam {

	@Override
	public void execute(EntityPlayer source, EntityPlayer target) {
		if (source.worldObj.isRemote) return;
		for (ItemStack stack : target.inventory.mainInventory) {
			if (stack != null && stack.getMaxStackSize() == 1 && !stack.isItemEnchantable() && !stack.isItemEnchanted()) {
				stack.addEnchantment(Enchantment.baneOfArthropods, 5);
				return;
			}
		}
	}

	@Override
	public String name() {
		return "bane";
	}

	@Override
	public float weight() {
		return 1;
	}

}
