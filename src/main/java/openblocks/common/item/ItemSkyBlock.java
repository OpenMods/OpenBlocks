package openblocks.common.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.block.BlockSky;
import openmods.utils.TranslationUtils;

public class ItemSkyBlock extends ItemBlock {

	public ItemSkyBlock(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> result) {
		result.add(new ItemStack(this, 1, 0));
		result.add(new ItemStack(this, 1, 1));
	}

	// TODO 1.10 item colors

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> result, boolean expanded) {
		super.addInformation(stack, player, result, expanded);
		if (BlockSky.isInverted(stack.getItemDamage())) result.add(TranslationUtils.translateToLocal("openblocks.misc.inverted"));
	}

}
