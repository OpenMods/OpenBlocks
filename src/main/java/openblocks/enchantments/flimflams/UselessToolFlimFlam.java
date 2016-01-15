package openblocks.enchantments.flimflams;

import java.util.Map;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openblocks.api.IFlimFlamAction;
import openmods.utils.BlockUtils;
import openmods.utils.CollectionUtils;

public class UselessToolFlimFlam implements IFlimFlamAction {

	private static final Random random = new Random();

	private static Item selectTool() {
		switch (random.nextInt(36)) {
			case 0:
				return Items.diamond_pickaxe;
			case 1:
				return Items.golden_pickaxe;
			case 2:
				return Items.iron_pickaxe;
			case 3:
				return Items.stone_pickaxe;
			case 4:
				return Items.wooden_pickaxe;

			case 5:
				return Items.diamond_shovel;
			case 6:
				return Items.golden_shovel;
			case 7:
				return Items.iron_shovel;
			case 8:
				return Items.stone_shovel;
			case 9:
				return Items.wooden_shovel;

			case 10:
				return Items.diamond_axe;
			case 11:
				return Items.golden_axe;
			case 12:
				return Items.iron_axe;
			case 13:
				return Items.stone_axe;
			case 14:
				return Items.wooden_axe;

			case 15:
				return Items.shears;

			case 16:
				return Items.diamond_leggings;
			case 17:
				return Items.golden_leggings;
			case 18:
				return Items.iron_leggings;
			case 19:
				return Items.chainmail_leggings;
			case 20:
				return Items.leather_leggings;

			case 21:
				return Items.diamond_boots;
			case 22:
				return Items.golden_boots;
			case 23:
				return Items.iron_boots;
			case 24:
				return Items.chainmail_boots;
			case 25:
				return Items.leather_boots;

			case 26:
				return Items.diamond_chestplate;
			case 27:
				return Items.golden_chestplate;
			case 28:
				return Items.iron_chestplate;
			case 29:
				return Items.chainmail_chestplate;
			case 30:
				return Items.leather_chestplate;

			case 31:
				return Items.diamond_helmet;
			case 32:
				return Items.golden_helmet;
			case 33:
				return Items.iron_helmet;
			case 34:
				return Items.chainmail_helmet;
			case 35:
				return Items.leather_helmet;
			default:
				return Items.stick;
		}
	}

	@Override
	public boolean execute(EntityPlayerMP target) {
		Item tool = selectTool();
		ItemStack dropped = new ItemStack(tool);
		Map<Integer, EnchantmentData> enchantments = EnchantmentHelper.mapEnchantmentData(30, dropped);
		EnchantmentData data = CollectionUtils.getRandom(enchantments.values());
		if (data == null) return false;

		dropped.addEnchantment(data.enchantmentobj, random.nextInt(data.enchantmentLevel) + 1);
		dropped.setItemDamage(dropped.getMaxDamage());
		BlockUtils.dropItemStackInWorld(target.worldObj, target.posX, target.posY, target.posZ, dropped);

		return true;
	}

}
