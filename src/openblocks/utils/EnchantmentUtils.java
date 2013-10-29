package openblocks.utils;

import java.util.List;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class EnchantmentUtils {

	public static final int XP_PER_BOTTLE = 8;
	public static final int RATIO = 20;
	public static final int LIQUID_PER_XP_BOTTLE = XP_PER_BOTTLE * RATIO;

	public static int calcEnchantability(ItemStack itemStack, int power, boolean max) {
		Item item = itemStack.getItem();
		int k = item.getItemEnchantability();
		if (k <= 0) { return 0; }
		if (power > 15) {
			power = 15;
		}

		int l = (max? 7 : 0) + 1 + (power >> 1) + (max? power : 0);
		return max? Math.max(l, power * 2) : Math.max(l / 3, 1);
	}

	public static boolean enchantItem(ItemStack itemstack, int level, Random rand) {

		if (itemstack != null) {

			@SuppressWarnings("unchecked")
			List<EnchantmentData> list = EnchantmentHelper.buildEnchantmentList(rand, itemstack, level);
			boolean flag = itemstack.itemID == Item.book.itemID;
			if (list != null) {

				if (flag) {
					itemstack.itemID = Item.enchantedBook.itemID;
				}

				int j = flag? rand.nextInt(list.size()) : -1;

				for (int k = 0; k < list.size(); ++k) {
					EnchantmentData enchantmentdata = list.get(k);

					if (!flag || k == j) {
						if (flag) {
							Item.enchantedBook.addEnchantment(itemstack, enchantmentdata);
						} else {
							itemstack.addEnchantment(enchantmentdata.enchantmentobj, enchantmentdata.enchantmentLevel);
						}
					}
				}
			}

			return true;
		}
		return false;
	}

	public static int getExperienceForLevel(int level) {
		if (level == 0) { return 0; }
		if (level > 0 && level < 16) {
			return level * 17;
		} else if (level > 15 && level < 31) {
			return (int)(1.5 * Math.pow(level, 2) - 29.5 * level + 360);
		} else {
			return (int)(3.5 * Math.pow(level, 2) - 151.5 * level + 2220);
		}
	}

	public static int getLevelForExperience(int experience) {
		int i = 0;
		while (getExperienceForLevel(i) <= experience) {
			i++;
		}
		return i - 1;
	}

	public static double getPower(World worldObj, int xCoord, int yCoord, int zCoord) {

		int j;
		float power = 0;

		for (j = -1; j <= 1; ++j) {
			for (int k = -1; k <= 1; ++k) {
				if ((j != 0 || k != 0)
						&& worldObj.isAirBlock(xCoord + k, yCoord, zCoord + j)
						&& worldObj.isAirBlock(xCoord + k, yCoord + 1, zCoord
								+ j)) {

					power += ForgeHooks.getEnchantPower(worldObj, xCoord + k
							* 2, yCoord, zCoord + j * 2);
					power += ForgeHooks.getEnchantPower(worldObj, xCoord + k
							* 2, yCoord + 1, zCoord + j * 2);

					if (k != 0 && j != 0) {
						power += ForgeHooks.getEnchantPower(worldObj, xCoord
								+ k * 2, yCoord, zCoord + j);
						power += ForgeHooks.getEnchantPower(worldObj, xCoord
								+ k * 2, yCoord + 1, zCoord + j);
						power += ForgeHooks.getEnchantPower(worldObj, xCoord
								+ k, yCoord, zCoord + j * 2);
						power += ForgeHooks.getEnchantPower(worldObj, xCoord
								+ k, yCoord + 1, zCoord + j * 2);
					}
				}
			}
		}
		return power;
	}

	public static int liquidToXPRatio(int liquid) {
		return liquid / RATIO;
	}

	public static int XPToLiquidRatio(int xp) {
		return xp * RATIO;
	}

	public static int getLiquidForLevel(int level) {
		return XPToLiquidRatio(getExperienceForLevel(level));
	}
}