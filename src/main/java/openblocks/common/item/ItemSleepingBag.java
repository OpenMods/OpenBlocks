package openblocks.common.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.client.model.ModelSleepingBag;
import openmods.infobook.BookDocumentation;
import openmods.utils.BlockUtils;
import openmods.utils.ItemUtils;

@BookDocumentation(customName = "sleepingbag")
public class ItemSleepingBag extends ItemArmor {

	private static final String TAG_SLEEPING = "Sleeping";
	private static final String TAG_SLOT = "Slot";

	public static final String TEXTURE_SLEEPINGBAG = "openblocks:textures/models/sleepingbag.png";

	public ItemSleepingBag() {
		super(ArmorMaterial.IRON, 2, EntityEquipmentSlot.CHEST);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		return TEXTURE_SLEEPINGBAG;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
		return armorSlot == EntityEquipmentSlot.CHEST? ModelSleepingBag.instance : null;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack heldStack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			ItemStack currentArmor = getChestpieceSlot(player);
			if (currentArmor != null) currentArmor = currentArmor.copy();
			final ItemStack sleepingBagCopy = heldStack.copy();

			NBTTagCompound tag = ItemUtils.getItemTag(sleepingBagCopy);
			tag.setInteger(TAG_SLOT, player.inventory.currentItem);

			setChestpieceSlot(player, sleepingBagCopy);
			if (currentArmor != null) return ActionResult.newResult(EnumActionResult.SUCCESS, currentArmor);
			heldStack.stackSize = 0;
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, heldStack);
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
		return armorType == EntityEquipmentSlot.CHEST;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if (!(player instanceof EntityPlayerMP)) return;
		if (player.isPlayerSleeping()) return;

		NBTTagCompound tag = ItemUtils.getItemTag(itemStack);
		if (tag.getBoolean(TAG_SLEEPING)) {
			// player just woke up
			// TODO 1.10 reimplement if needed?
			tag.removeTag(TAG_SLEEPING);
			getOutOfSleepingBag(player);
		} else {
			// player just put in on
			// final BlockPos pos = player.getPosition();
			// tag.setBoolean(TAG_SLEEPING, true);
			// TODO 1.10 use PR to reimplement. Remember to add custom checks

			getOutOfSleepingBag(player);
		}
	}

	private static Integer getReturnSlot(NBTTagCompound tag) {
		if (tag.hasKey(TAG_SLOT, Constants.NBT.TAG_ANY_NUMERIC)) {
			int slot = tag.getInteger(TAG_SLOT);
			if (slot < 9 && slot >= 0) return slot;
		}

		return null;
	}

	private static boolean tryReturnToSlot(EntityPlayer player, ItemStack sleepingBag) {
		NBTTagCompound tag = ItemUtils.getItemTag(sleepingBag);
		final Integer returnSlot = getReturnSlot(tag);
		tag.removeTag(TAG_SLOT);
		if (returnSlot == null) {
			setChestpieceSlot(player, null);
			return false;
		}

		final ItemStack possiblyArmor = player.inventory.mainInventory[returnSlot];
		if (isChestplate(possiblyArmor)) {
			setChestpieceSlot(player, possiblyArmor);
		} else {
			setChestpieceSlot(player, null);
			if (possiblyArmor != null) return false;
		}

		player.inventory.setInventorySlotContents(returnSlot, sleepingBag);
		return true;
	}

	private static void getOutOfSleepingBag(EntityPlayer player) {
		ItemStack stack = getChestpieceSlot(player);
		if (isSleepingBag(stack)) {
			if (!tryReturnToSlot(player, stack)) {
				if (!player.inventory.addItemStackToInventory(stack)) {
					BlockUtils.dropItemStackInWorld(player.worldObj, player.posX, player.posY, player.posZ, stack);
				}
			}
		}
	}

	public static boolean isWearingSleepingBag(EntityPlayer player) {
		ItemStack armor = getChestpieceSlot(player);
		return isSleepingBag(armor);
	}

	public static class IsSleepingHandler {
		@SubscribeEvent
		public void onBedCheck(SleepingLocationCheckEvent evt) {
			if (isWearingSleepingBag(evt.getEntityPlayer()))
				evt.setResult(Result.ALLOW);
		}
	}

	private static boolean isSleepingBag(ItemStack armor) {
		return armor != null && armor.getItem() instanceof ItemSleepingBag;
	}

	private static ItemStack setChestpieceSlot(EntityPlayer player, ItemStack chestpiece) {
		player.setItemStackToSlot(EntityEquipmentSlot.CHEST, chestpiece);
		return chestpiece;
	}

	private static ItemStack getChestpieceSlot(EntityPlayer player) {
		return player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
	}

	private static boolean isChestplate(ItemStack stack) {
		if (stack == null) return false;
		Item item = stack.getItem();
		if (item instanceof ItemSleepingBag) return false;

		if (item instanceof ItemArmor) {
			ItemArmor armorItem = (ItemArmor)item;
			return armorItem.armorType == EntityEquipmentSlot.CHEST;
		}

		return false;
	}
}
