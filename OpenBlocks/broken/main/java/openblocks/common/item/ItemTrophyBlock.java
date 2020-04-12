package openblocks.common.item;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import openblocks.common.TrophyHandler.Trophy;
import openmods.item.ItemOpenBlock;
import openmods.utils.ItemUtils;
import openmods.utils.TranslationUtils;

public class ItemTrophyBlock extends ItemOpenBlock {

	private static final String TAG_ENTITY_ID = "Entity";

	public ItemTrophyBlock(Block block) {
		super(block);
	}

	public static Trophy getTrophy(@Nonnull ItemStack stack) {
		if (stack.hasTagCompound()) {
			CompoundNBT tag = stack.getTagCompound();
			if (tag.hasKey(TAG_ENTITY_ID, Constants.NBT.TAG_STRING)) {
				ResourceLocation id = new ResourceLocation(tag.getString(TAG_ENTITY_ID));
				return Trophy.ENTITY_TO_TROPHY.get(id);
			}
		}

		return null;
	}

	@Override
	public int getMetadata(@Nonnull ItemStack stack) {
		// for item rendering purposes
		Trophy trophy = getTrophy(stack);
		return trophy != null? trophy.ordinal() : 0;
	}

	public static ItemStack putMetadata(@Nonnull ItemStack stack, Trophy trophy) {
		CompoundNBT tag = ItemUtils.getItemTag(stack);
		tag.setString(TAG_ENTITY_ID, trophy.id.toString());
		return stack;
	}

	@Override
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		Trophy trophyType = getTrophy(stack);
		if (trophyType != null) {
			final String name = TranslationUtils.translateToLocal(trophyType.translationKey());
			return TranslationUtils.translateToLocalFormatted(super.getUnlocalizedName() + ".entity.name", name);
		}

		return super.getItemStackDisplayName(stack);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> result) {
		if (isInCreativeTab(tab)) {
			for (Trophy trophy : Trophy.VALUES) {
				result.add(putMetadata(new ItemStack(this), trophy));
			}
		}
	}
}
