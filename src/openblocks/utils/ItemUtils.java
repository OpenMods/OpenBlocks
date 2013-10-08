package openblocks.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;

public class ItemUtils {
	public static ItemStack consumeItem(ItemStack stack) {
		if (stack.stackSize == 1) {
			if (stack.getItem().hasContainerItem()) {
				return stack.getItem().getContainerItemStack(stack);
			}
			return null;
		}
		stack.splitStack(1);

		return stack;
	}

	public static NBTTagCompound getItemTag(ItemStack stack) {
		if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound("tag");

		return stack.stackTagCompound;
	}

	public static Integer getInt(ItemStack stack, String tagName) {
		NBTTagCompound tag = getItemTag(stack);
		NBTBase data = tag.getTag(tagName);
		return (data != null)? ((NBTTagInt)data).data : null;
	}
}
