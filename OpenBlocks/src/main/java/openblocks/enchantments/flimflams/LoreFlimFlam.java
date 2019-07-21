package openblocks.enchantments.flimflams;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.AxeItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
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
				final CompoundNBT itemTag = evt.getItemStack().getTagCompound();
				if (itemTag != null) {
					if (itemTag.hasKey("display", Constants.NBT.TAG_COMPOUND)) {
						final CompoundNBT displayTag = itemTag.getCompoundTag("display");
						if (displayTag.hasKey(TAG_NAME, Constants.NBT.TAG_LIST) &&
								!displayTag.hasKey("Lore", Constants.NBT.TAG_LIST)) {
							final ListNBT sillyLore = displayTag.getTagList(TAG_NAME, Constants.NBT.TAG_STRING);

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
	public boolean execute(ServerPlayerEntity target) {
		List<EquipmentSlotType> slots = Lists.newArrayList(EquipmentSlotType.values());
		Collections.shuffle(slots);

		for (EquipmentSlotType slot : slots)
			if (tryAddLore(target, slot)) return true;

		return false;
	}

	private static boolean tryAddLore(PlayerEntity target, EquipmentSlotType slot) {
		final ItemStack item = target.getItemStackFromSlot(slot);

		if (item.isEmpty()) return false;

		final CompoundNBT tag = ItemUtils.getItemTag(item);

		final CompoundNBT display = tag.getCompoundTag("display");
		if (!tag.hasKey("display", Constants.NBT.TAG_COMPOUND)) tag.setTag("display", display);

		final String lore = LoreGenerator.generateLore(target.getName(), identityType(item));

		final ListNBT loreList = new ListNBT();
		for (String line : splitText(lore, 30))
			loreList.appendTag(new StringNBT(line));

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

	private static String identityType(@Nonnull ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ArmorItem) {
			switch (((ArmorItem)item).armorType) {
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
		} else if (item instanceof PickaxeItem) return "pickaxe";
		else if (item instanceof ShearsItem) return "shears";
		else if (item instanceof AxeItem) return "axe";
		else if (item instanceof ShovelItem) return "shovel";
		else if (item instanceof BlockItem) return "block";
		else if (item instanceof BucketItem) return "bucket";

		return "gizmo";
	}

}
