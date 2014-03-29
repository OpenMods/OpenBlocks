package openblocks.integration;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.registry.GameRegistry;

public class CCCommonUtils {

	public static ItemStack getExpandedTurtleItemStack() {
		return GameRegistry.findItemStack("CCTurtle", "CC-TurtleExpanded", 1);
	}

	public static ItemStack getAdvancedTurtleItemStack() {
		return GameRegistry.findItemStack("CCTurtle", "CC-TurtleAdvanced", 1);
	}

	public static void createTurtleItemStack(List<ItemStack> result, boolean isAdvanced, Short left, Short right) {
		ItemStack turtle = isAdvanced? getAdvancedTurtleItemStack() : getExpandedTurtleItemStack();

		if (turtle == null) return;

		NBTTagCompound tag = turtle.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			turtle.setTagCompound(tag);
		}

		if (left != null) tag.setShort("leftUpgrade", left);

		if (right != null) tag.setShort("rightUpgrade", right);

		result.add(turtle);
	}
}
