package openblocks.enchantments.flimflams;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import openblocks.api.IAttackFlimFlam;
import openblocks.rubbish.LoreGenerator;
import openmods.utils.ItemUtils;

public class LoreFlimFlam implements IAttackFlimFlam {

	private static final Random random = new Random();

	@Override
	public void execute(EntityPlayer source, EntityPlayer target) {
		int slot = random.nextInt(5);
		ItemStack item;
		if (slot == 4) item = target.getHeldItem();
		else item = target.inventory.armorInventory[slot];

		if (item != null) {
			NBTTagCompound tag = ItemUtils.getItemTag(item);

			NBTTagCompound display = tag.getCompoundTag("display");
			if (!tag.hasKey("display")) tag.setTag("display", display);

			// TODO: needs proper line breaks
			NBTTagList lore = new NBTTagList();
			lore.appendTag(new NBTTagString("lies", LoreGenerator.generateLore()));
			display.setTag("Lore", lore);
		}
	}

	@Override
	public String name() {
		return "epic lore add";
	}

	@Override
	public float weight() {
		// TODO Auto-generated method stub
		return 0;
	}

}
