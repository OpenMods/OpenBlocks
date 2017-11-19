package openblocks.common.item;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import openblocks.common.TrophyHandler.Trophy;
import openmods.item.ItemOpenBlock;
import openmods.utils.ItemUtils;
import openmods.utils.TranslationUtils;

public class ItemTrophyBlock extends ItemOpenBlock {

	private static final String TAG_ENTITY = "entity";

	private static final String TAG_ENTITY_ID = "entity_id";

	public ItemTrophyBlock(Block block) {
		super(block);
	}

	public static Trophy getTrophy(@Nonnull ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(TAG_ENTITY_ID, Constants.NBT.TAG_STRING)) {
				ResourceLocation id = new ResourceLocation(tag.getString(TAG_ENTITY_ID));
				return Trophy.TYPES_BY_ID.get(id);
			}

			if (tag.hasKey(TAG_ENTITY)) {
				String entityKey = tag.getString(TAG_ENTITY);
				return Trophy.TYPES_BY_NAME.get(entityKey);
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
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
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
		for (Trophy trophy : Trophy.VALUES) {
			result.add(putMetadata(new ItemStack(this), trophy));
		}
	}
}
