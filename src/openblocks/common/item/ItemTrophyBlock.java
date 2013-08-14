package openblocks.common.item;

import java.util.List;

import openblocks.common.TrophyHandler.Trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemTrophyBlock extends ItemOpenBlock {

	public ItemTrophyBlock(int par1) {
		super(par1);
	}
	
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey("entity")) {
				String entityKey = tag.getString("entity");
				Trophy trophyType = Trophy.valueOf(entityKey);
				list.add(trophyType.getEntity().getTranslatedEntityName());
			}
		}
	}

}
