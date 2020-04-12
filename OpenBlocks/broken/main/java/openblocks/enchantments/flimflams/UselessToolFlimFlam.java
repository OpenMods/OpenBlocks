package openblocks.enchantments.flimflams;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
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
				return Items.DIAMOND_PICKAXE;
			case 1:
				return Items.GOLDEN_PICKAXE;
			case 2:
				return Items.IRON_PICKAXE;
			case 3:
				return Items.STONE_PICKAXE;
			case 4:
				return Items.WOODEN_PICKAXE;

			case 5:
				return Items.DIAMOND_SHOVEL;
			case 6:
				return Items.GOLDEN_SHOVEL;
			case 7:
				return Items.IRON_SHOVEL;
			case 8:
				return Items.STONE_SHOVEL;
			case 9:
				return Items.WOODEN_SHOVEL;

			case 10:
				return Items.DIAMOND_AXE;
			case 11:
				return Items.GOLDEN_AXE;
			case 12:
				return Items.IRON_AXE;
			case 13:
				return Items.STONE_AXE;
			case 14:
				return Items.WOODEN_AXE;

			case 15:
				return Items.SHEARS;

			case 16:
				return Items.DIAMOND_LEGGINGS;
			case 17:
				return Items.GOLDEN_LEGGINGS;
			case 18:
				return Items.IRON_LEGGINGS;
			case 19:
				return Items.CHAINMAIL_LEGGINGS;
			case 20:
				return Items.LEATHER_LEGGINGS;

			case 21:
				return Items.DIAMOND_BOOTS;
			case 22:
				return Items.GOLDEN_BOOTS;
			case 23:
				return Items.IRON_BOOTS;
			case 24:
				return Items.CHAINMAIL_BOOTS;
			case 25:
				return Items.LEATHER_BOOTS;

			case 26:
				return Items.DIAMOND_CHESTPLATE;
			case 27:
				return Items.GOLDEN_CHESTPLATE;
			case 28:
				return Items.IRON_CHESTPLATE;
			case 29:
				return Items.CHAINMAIL_CHESTPLATE;
			case 30:
				return Items.LEATHER_CHESTPLATE;

			case 31:
				return Items.DIAMOND_HELMET;
			case 32:
				return Items.GOLDEN_HELMET;
			case 33:
				return Items.IRON_HELMET;
			case 34:
				return Items.CHAINMAIL_HELMET;
			case 35:
				return Items.LEATHER_HELMET;
			default:
				return Items.STICK;
		}
	}

	@Override
	public boolean execute(ServerPlayerEntity target) {
		Item tool = selectTool();
		ItemStack dropped = new ItemStack(tool);
		List<EnchantmentData> enchantments = EnchantmentHelper.getEnchantmentDatas(30, dropped, true);
		EnchantmentData data = CollectionUtils.getRandom(enchantments);
		if (data == null) return false;

		dropped.addEnchantment(data.enchantment, random.nextInt(data.enchantmentLevel) + 1);
		dropped.setItemDamage(dropped.getMaxDamage());
		BlockUtils.dropItemStackInWorld(target.world, target.posX, target.posY, target.posZ, dropped);

		return true;
	}

}
