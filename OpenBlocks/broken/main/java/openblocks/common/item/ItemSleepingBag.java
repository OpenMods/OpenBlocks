package openblocks.common.item;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity.SleepResult;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
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
public class ItemSleepingBag extends ArmorItem {

	private static final String TAG_SLEEPING = "Sleeping";
	private static final String TAG_SLOT = "Slot";

	public static final String TEXTURE_SLEEPINGBAG = OpenBlocks.location("textures/models/sleeping_bag.png").toString();

	public ItemSleepingBag() {
		super(ArmorMaterial.IRON, 2, EquipmentSlotType.CHEST);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return TEXTURE_SLEEPINGBAG;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, ModelBiped _default) {
		return armorSlot == EquipmentSlotType.CHEST? ModelSleepingBag.instance : null;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		final ItemStack heldStack = player.getHeldItem(hand);

		if (hand != Hand.MAIN_HAND) return ActionResult.newResult(ActionResultType.PASS, heldStack);

		if (!world.isRemote) {
			ItemStack currentArmor = getChestpieceSlot(player);
			if (!currentArmor.isEmpty()) currentArmor = currentArmor.copy();
			final ItemStack sleepingBagCopy = heldStack.copy();

			CompoundNBT tag = ItemUtils.getItemTag(sleepingBagCopy);
			tag.setInteger(TAG_SLOT, player.inventory.currentItem);

			setChestpieceSlot(player, sleepingBagCopy);
			if (!currentArmor.isEmpty()) return ActionResult.newResult(ActionResultType.SUCCESS, currentArmor);
			heldStack.setCount(0);
		}

		return ActionResult.newResult(ActionResultType.SUCCESS, heldStack);
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EquipmentSlotType armorType, Entity entity) {
		return armorType == EquipmentSlotType.CHEST;
	}

	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack itemStack) {
		if (!(player instanceof ServerPlayerEntity)) return;
		if (player.isPlayerSleeping()) return;

		CompoundNBT tag = ItemUtils.getItemTag(itemStack);
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

	private static boolean trySleep(World world, PlayerEntity player) {
		final BlockPos pos = player.getPosition();

		if (!isNotSuffocating(world, pos) || !isSolidEnough(world, pos.down())) {
			player.sendMessage(new TranslationTextComponent("openblocks.misc.oh_no_ground"));
			return false;
		}

		final PlayerEntity.SleepResult sleepResult = player.trySleep(pos);

		if (sleepResult == SleepResult.OK) return true;

		switch (sleepResult) {
			case NOT_POSSIBLE_NOW:
				player.sendMessage(new TranslationTextComponent("tile.bed.noSleep"));
				break;
			case NOT_SAFE:
				player.sendMessage(new TranslationTextComponent("tile.bed.notSafe"));
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
		BlockState state = world.getBlockState(pos);
		final AxisAlignedBB aabb = state.getCollisionBoundingBox(world, pos);
		if (aabb == null) return false;

		double dx = aabb.maxX - aabb.minX;
		double dy = aabb.maxY - aabb.minY;
		double dz = aabb.maxZ - aabb.minZ;

		return (dx >= 0.5) && (dy >= 0.5) && (dz >= 0.5);
	}

	private static Integer getReturnSlot(CompoundNBT tag) {
		if (tag.hasKey(TAG_SLOT, Constants.NBT.TAG_ANY_NUMERIC)) {
			int slot = tag.getInteger(TAG_SLOT);
			if (slot < 9 && slot >= 0) return slot;
		}

		return null;
	}

	private static boolean tryReturnToSlot(PlayerEntity player, ItemStack sleepingBag) {
		CompoundNBT tag = ItemUtils.getItemTag(sleepingBag);
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

	private static void getOutOfSleepingBag(PlayerEntity player) {
		ItemStack stack = getChestpieceSlot(player);
		if (isSleepingBag(stack)) {
			if (!tryReturnToSlot(player, stack)) {
				if (!player.inventory.addItemStackToInventory(stack)) {
					BlockUtils.dropItemStackInWorld(player.world, player.posX, player.posY, player.posZ, stack);
				}
			}
		}
	}

	public static boolean isWearingSleepingBag(PlayerEntity player) {
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
	private static ItemStack setChestpieceSlot(PlayerEntity player, @Nonnull ItemStack chestpiece) {
		player.setItemStackToSlot(EquipmentSlotType.CHEST, chestpiece);
		return chestpiece;
	}

	@Nonnull
	private static ItemStack getChestpieceSlot(PlayerEntity player) {
		return player.getItemStackFromSlot(EquipmentSlotType.CHEST);
	}

	private static boolean isChestplate(@Nonnull ItemStack stack) {
		if (stack.isEmpty()) return false;
		Item item = stack.getItem();
		if (item instanceof ItemSleepingBag) return false;

		if (item instanceof ArmorItem) {
			ArmorItem armorItem = (ArmorItem)item;
			return armorItem.armorType == EquipmentSlotType.CHEST;
		}

		return false;
	}
}
