package openblocks.enchantments.flimflams;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.api.IFlimFlamAction;
import openblocks.rubbish.LoreGenerator;
import openmods.utils.ItemUtils;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class LoreFlimFlam implements IFlimFlamAction {

	public static final String TAG_NAME = "SillyLore";

	public static final String LORE_FORMAT = EnumChatFormatting.GREEN + "" + EnumChatFormatting.ITALIC;

	public static class DisplayHandler {
		@SubscribeEvent
		public void onItemTooltip(ItemTooltipEvent evt) {
			if (Config.loreDisplay > 0) {
				final NBTTagCompound itemTag = evt.itemStack.getTagCompound();
				if (itemTag != null) {
					if (itemTag.hasKey("display", Constants.NBT.TAG_COMPOUND)) {
						final NBTTagCompound displayTag = itemTag.getCompoundTag("display");
						if (displayTag.hasKey(TAG_NAME, Constants.NBT.TAG_LIST) &&
								!displayTag.hasKey("Lore", Constants.NBT.TAG_LIST)) {
							final NBTTagList sillyLore = displayTag.getTagList(TAG_NAME, Constants.NBT.TAG_STRING);

							if ((Config.loreDisplay > 1) ||
									Keyboard.isKeyDown(Keyboard.KEY_LMENU) ||
									Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
								for (int i = 0; i < sillyLore.tagCount(); i++)
									evt.toolTip.add(LORE_FORMAT + sillyLore.getStringTagAt(i));
							} else {
								evt.toolTip.add(StatCollector.translateToLocal("openblocks.misc.hidden_lore"));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean execute(EntityPlayerMP target) {
		List<Integer> slots = Lists.newArrayList(0, 1, 2, 3, 4);
		Collections.shuffle(slots);

		for (int slot : slots)
			if (tryAddLore(target, slot)) return true;

		return false;
	}

	private static boolean tryAddLore(EntityPlayer target, int slot) {
		final ItemStack item;

		if (slot == 4) item = target.getHeldItem();
		else item = target.inventory.armorInventory[slot];

		if (item == null) return false;

		final NBTTagCompound tag = ItemUtils.getItemTag(item);

		final NBTTagCompound display = tag.getCompoundTag("display");
		if (!tag.hasKey("display", Constants.NBT.TAG_COMPOUND)) tag.setTag("display", display);

		final String lore = LoreGenerator.generateLore(target.getName(), identityType(item));

		final NBTTagList loreList = new NBTTagList();
		for (String line : splitText(lore, 30))
			loreList.appendTag(new NBTTagString(line));

		display.setTag(TAG_NAME, loreList);
		return true;
	}

	private static List<String> splitText(String lore, int maxSize) {
		List<String> result = Lists.newArrayList();

		Joiner joiner = Joiner.on(" ");
		Iterable<String> words = Splitter.on(" ").omitEmptyStrings().split(lore);
		List<String> buffer = Lists.newArrayList();
		int length = 0;
		for (String word : words) {
			int newLength = length + word.length();
			if (newLength > maxSize) {
				result.add(joiner.join(buffer));
				length = 0;
				buffer.clear();
			}
			buffer.add(word);
			length += word.length() + 1;
		}
		if (!buffer.isEmpty()) result.add(joiner.join(buffer));

		return result;
	}

	private static String identityType(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemArmor) {
			switch (((ItemArmor)item).armorType) {
				case 0:
					return "helmet";
				case 1:
					return "chestplate";
				case 2:
					return "leggings";
				case 3:
					return "boots";
			}
		} else if (item instanceof ItemPickaxe) return "pickaxe";
		else if (item instanceof ItemShears) return "shears";
		else if (item instanceof ItemAxe) return "axe";
		else if (item instanceof ItemSpade) return "shovel";
		else if (item instanceof ItemBlock) return "block";
		else if (item instanceof ItemBucket) return "bucket";

		return "gizmo";
	}

}
