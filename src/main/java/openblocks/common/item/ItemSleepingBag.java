package openblocks.common.item;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelSleepingBag;
import openmods.infobook.BookDocumentation;
import openmods.utils.BlockUtils;
import openmods.utils.ItemUtils;

@BookDocumentation
public class ItemSleepingBag extends ItemArmor {

	private static final String TAG_SLEEPING = "Sleeping";
	private static final String TAG_SLOT = "Slot";

	public static final String TEXTURE_SLEEPINGBAG = OpenBlocks.location("textures/models/sleeping_bag.png").toString();

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
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		final ItemStack heldStack = player.getHeldItem(hand);

		if (hand != EnumHand.MAIN_HAND) return ActionResult.newResult(EnumActionResult.PASS, heldStack);

		if (!world.isRemote) {
			ItemStack currentArmor = getChestpieceSlot(player);
			if (!currentArmor.isEmpty()) currentArmor = currentArmor.copy();
			final ItemStack sleepingBagCopy = heldStack.copy();

			NBTTagCompound tag = ItemUtils.getItemTag(sleepingBagCopy);
			tag.setInteger(TAG_SLOT, player.inventory.currentItem);

			setChestpieceSlot(player, sleepingBagCopy);
			if (!currentArmor.isEmpty()) return ActionResult.newResult(EnumActionResult.SUCCESS, currentArmor);
			heldStack.setCount(0);
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
			tag.removeTag(TAG_SLEEPING);
			getOutOfSleepingBag(player);
		} else {
			// player just put in on
			if (!trySleep(world, player)) {
				getOutOfSleepingBag(player);
			} else {
				tag.setBoolean(TAG_SLEEPING, true);
			}
		}
	}

	private static boolean trySleep(World world, EntityPlayer player) {
		final BlockPos pos = player.getPosition();

		if (!isNotSuffocating(world, pos) || !isSolidEnough(world, pos.down())) {
			player.sendMessage(new TextComponentTranslation("openblocks.misc.oh_no_ground"));
			return false;
		}

		final EntityPlayer.SleepResult sleepResult = player.trySleep(pos);

		if (sleepResult == SleepResult.OK) return true;

		switch (sleepResult) {
			case NOT_POSSIBLE_NOW:
				player.sendMessage(new TextComponentTranslation("tile.bed.noSleep"));
				break;
			case NOT_SAFE:
				player.sendMessage(new TextComponentTranslation("tile.bed.notSafe"));
				break;
			default:
				break;
		}

		return false;
	}

	private static boolean isNotSuffocating(World world, BlockPos pos) {
		return world.getBlockState(pos).getCollisionBoundingBox(world, pos) == null || world.isAirBlock(pos);
	}

	private static boolean isSolidEnough(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		final AxisAlignedBB aabb = state.getCollisionBoundingBox(world, pos);
		if (aabb == null) return false;

		double dx = aabb.maxX - aabb.minX;
		double dy = aabb.maxY - aabb.minY;
		double dz = aabb.maxZ - aabb.minZ;

		return (dx >= 0.5) && (dy >= 0.5) && (dz >= 0.5);
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
			setChestpieceSlot(player, ItemStack.EMPTY);
			return false;
		}

		final ItemStack possiblyArmor = player.inventory.mainInventory.get(returnSlot);
		if (isChestplate(possiblyArmor)) {
			setChestpieceSlot(player, possiblyArmor);
		} else {
			setChestpieceSlot(player, ItemStack.EMPTY);
			if (!possiblyArmor.isEmpty()) return false;
		}

		player.inventory.setInventorySlotContents(returnSlot, sleepingBag);
		return true;
	}

	private static void getOutOfSleepingBag(EntityPlayer player) {
		ItemStack stack = getChestpieceSlot(player);
		if (isSleepingBag(stack)) {
			if (!tryReturnToSlot(player, stack)) {
				if (!player.inventory.addItemStackToInventory(stack)) {
					BlockUtils.dropItemStackInWorld(player.world, player.posX, player.posY, player.posZ, stack);
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
		return !armor.isEmpty() && armor.getItem() instanceof ItemSleepingBag;
	}

	@Nonnull
	private static ItemStack setChestpieceSlot(EntityPlayer player, @Nonnull ItemStack chestpiece) {
		player.setItemStackToSlot(EntityEquipmentSlot.CHEST, chestpiece);
		return chestpiece;
	}

	@Nonnull
	private static ItemStack getChestpieceSlot(EntityPlayer player) {
		return player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
	}

	private static boolean isChestplate(@Nonnull ItemStack stack) {
		if (stack.isEmpty()) return false;
		Item item = stack.getItem();
		if (item instanceof ItemSleepingBag) return false;

		if (item instanceof ItemArmor) {
			ItemArmor armorItem = (ItemArmor)item;
			return armorItem.armorType == EntityEquipmentSlot.CHEST;
		}

		return false;
	}
}
