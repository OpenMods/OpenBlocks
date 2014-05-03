package openblocks.enchantments.flimflams;

import java.util.Map;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayerMP;
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
				return Item.pickaxeDiamond;
			case 1:
				return Item.pickaxeGold;
			case 2:
				return Item.pickaxeIron;
			case 3:
				return Item.pickaxeStone;
			case 4:
				return Item.pickaxeWood;

			case 5:
				return Item.shovelDiamond;
			case 6:
				return Item.shovelGold;
			case 7:
				return Item.shovelIron;
			case 8:
				return Item.shovelStone;
			case 9:
				return Item.shovelWood;

			case 10:
				return Item.axeDiamond;
			case 11:
				return Item.axeGold;
			case 12:
				return Item.axeIron;
			case 13:
				return Item.axeStone;
			case 14:
				return Item.axeWood;

			case 15:
				return Item.shears;

			case 16:
				return Item.legsDiamond;
			case 17:
				return Item.legsGold;
			case 18:
				return Item.legsChain;
			case 19:
				return Item.legsIron;
			case 20:
				return Item.legsLeather;

			case 21:
				return Item.bootsDiamond;
			case 22:
				return Item.bootsGold;
			case 23:
				return Item.bootsChain;
			case 24:
				return Item.bootsIron;
			case 25:
				return Item.bootsLeather;

			case 26:
				return Item.plateDiamond;
			case 27:
				return Item.plateGold;
			case 28:
				return Item.plateChain;
			case 29:
				return Item.plateIron;
			case 30:
				return Item.plateLeather;

			case 31:
				return Item.helmetDiamond;
			case 32:
				return Item.helmetGold;
			case 33:
				return Item.helmetChain;
			case 34:
				return Item.helmetIron;
			case 35:
				return Item.helmetLeather;
			default:
				return Item.stick;
		}
	}

	@Override
	public boolean execute(EntityPlayerMP target) {
		Item tool = selectTool();
		ItemStack dropped = new ItemStack(tool);
		@SuppressWarnings("unchecked")
		Map<Integer, EnchantmentData> enchantments = EnchantmentHelper.mapEnchantmentData(30, dropped);
		EnchantmentData data = CollectionUtils.getRandom(enchantments.values());
		if (data == null) return false;

		dropped.addEnchantment(data.enchantmentobj, random.nextInt(data.enchantmentLevel) + 1);
		dropped.setItemDamage(dropped.getMaxDamage());
		BlockUtils.dropItemStackInWorld(target.worldObj, target.posX, target.posY, target.posZ, dropped);

		return true;
	}

}
