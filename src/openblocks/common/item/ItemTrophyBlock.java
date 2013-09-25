package openblocks.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openblocks.common.TrophyHandler.Trophy;

public class ItemTrophyBlock extends ItemOpenBlock {

	public ItemTrophyBlock(int par1) {
		super(par1);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer,
			List list, boolean par4) {
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
