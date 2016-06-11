package openblocks.common;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import java.util.Random;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.oredict.OreDictionary;
import openblocks.Config;

public class RadioVillagerTradeManager implements IVillageTradeHandler {

	private static ItemStack randomItemAmount(Random random, Item item, int min, int max) {
		int amount = random.nextInt(max - min) + min;
		return new ItemStack(item, amount);
	}

	private static ItemStack randomEmeralds(Random random, int min, int max) {
		return randomItemAmount(random, Items.emerald, min, max);
	}

	@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
		if (Config.radioVillagerRecords) {
			for (ItemStack record : OreDictionary.getOres("record"))
				if (random.nextFloat() < 0.01) recipeList.addToListWithCheck(
						new MerchantRecipe(
								randomEmeralds(random, 7, 15),
								record));
		}

		if (random.nextFloat() > 0.5) recipeList.addToListWithCheck(
				new MerchantRecipe(
						randomEmeralds(random, 1, 2),
						new ItemStack(Blocks.noteblock)));

		if (random.nextFloat() > 0.25 || recipeList.isEmpty()) recipeList.addToListWithCheck(
				new MerchantRecipe(
						randomEmeralds(random, 3, 7),
						new ItemStack(Blocks.jukebox)));
	}

}
