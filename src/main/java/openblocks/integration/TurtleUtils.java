package openblocks.integration;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openmods.Mods;
import cpw.mods.fml.common.registry.GameRegistry;

public class TurtleUtils {

	private static ItemStack tryGetTurtle(String itemName) {
		ItemStack result = GameRegistry.findItemStack(Mods.COMPUTERCRAFT_TURTLE, itemName, 1);
		if (result != null) return result;
		result = GameRegistry.findItemStack(Mods.COMPUTERCRAFT, itemName, 1);
		return result;
	}

	public static ItemStack getExpandedTurtleItemStack() {
		return tryGetTurtle("CC-TurtleExpanded");
	}

	public static ItemStack getAdvancedTurtleItemStack() {
		return tryGetTurtle("CC-TurtleAdvanced");
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

	private static final int NUMBER_OF_TURTLE_TOOLS = 7;

	private static void addUpgradedTurtles(List<ItemStack> result, short upgradeId, boolean isAdvanced) {
		createTurtleItemStack(result, isAdvanced, upgradeId, null);
		for (int i = 1; i < NUMBER_OF_TURTLE_TOOLS; i++)
			createTurtleItemStack(result, isAdvanced, upgradeId, (short)i);
	}

	public static void addUpgradedTurtles(List<ItemStack> result, short upgradeId) {
		addUpgradedTurtles(result, upgradeId, false);
		addUpgradedTurtles(result, upgradeId, true);
	}

}
