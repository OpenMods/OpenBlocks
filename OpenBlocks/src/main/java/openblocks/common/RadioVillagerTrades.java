package openblocks.common;

import net.minecraft.block.Blocks;
import net.minecraft.entity.merchant.villager.VillagerEntity.EmeraldForItems;
import net.minecraft.entity.merchant.villager.VillagerEntity.ListItemForEmeralds;
import net.minecraft.entity.merchant.villager.VillagerEntity.PriceInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.oredict.OreDictionary;

public class RadioVillagerTrades {

	public static void registerUselessVillager() {
		final VillagerProfession prof = new VillagerProfession(
				"openblocks:radio",
				"openblocks:textures/models/king-ish.png",
				"minecraft:textures/entity/zombie_villager/zombie_villager.png"); // TODO: zombie texture?

		GameRegistry.findRegistry(VillagerProfession.class).register(prof);

		final VillagerCareer career = new VillagerCareer(prof, "audiophile")
				.addTrade(1, new EmeraldForItems(Item.getItemFromBlock(Blocks.NOTEBLOCK), new PriceInfo(5, 7)))
				.addTrade(2, new ListItemForEmeralds(Item.getItemFromBlock(Blocks.JUKEBOX), new PriceInfo(10, 15))); // extra for sound quality!

		for (ItemStack record : OreDictionary.getOres("record"))
			career.addTrade(3, new ListItemForEmeralds(record.getItem(), new PriceInfo(3, 6)));
	}

}
