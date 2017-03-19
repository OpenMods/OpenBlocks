package openblocks.enchantments.flimflams;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.api.IFlimFlamAction;
import openblocks.rubbish.LoreGenerator;
import openmods.utils.ItemUtils;
import openmods.utils.TranslationUtils;
import org.lwjgl.input.Keyboard;

public class LoreFlimFlam implements IFlimFlamAction {

	public static final String TAG_NAME = "SillyLore";

	public static final String LORE_FORMAT = TextFormatting.GREEN + "" + TextFormatting.ITALIC;

	public static class DisplayHandler {
		@SubscribeEvent
		public void onItemTooltip(ItemTooltipEvent evt) {
			if (Config.loreDisplay > 0) {
				final NBTTagCompound itemTag = evt.getItemStack().getTagCompound();
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
									evt.getToolTip().add(LORE_FORMAT + sillyLore.getStringTagAt(i));
							} else {
								evt.getToolTip().add(TranslationUtils.translateToLocal("openblocks.misc.hidden_lore"));
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean execute(EntityPlayerMP target) {
		List<EntityEquipmentSlot> slots = Lists.newArrayList(EntityEquipmentSlot.values());
		Collections.shuffle(slots);

		for (EntityEquipmentSlot slot : slots)
			if (tryAddLore(target, slot)) return true;

		return false;
	}

	private static boolean tryAddLore(EntityPlayer target, EntityEquipmentSlot slot) {
		final ItemStack item = target.getItemStackFromSlot(slot);

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
				case HEAD:
					return "helmet";
				case CHEST:
					return "chestplate";
				case LEGS:
					return "leggings";
				case FEET:
					return "boots";
				default:
					break;
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
