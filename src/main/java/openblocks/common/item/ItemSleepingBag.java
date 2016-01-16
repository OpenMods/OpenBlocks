package openblocks.common.item;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.api.SleepingBagUseEvent;
import openblocks.asm.EntityPlayerVisitor;
import openblocks.client.model.ModelSleepingBag;
import openmods.infobook.BookDocumentation;
import openmods.reflection.FieldAccess;
import openmods.utils.BlockUtils;
import openmods.utils.ItemUtils;
import openmods.utils.NbtUtils;

@BookDocumentation(customName = "sleepingbag")
public class ItemSleepingBag extends ItemArmor {

	private static final String TAG_SPAWN = "Spawn";
	private static final String TAG_POSITION = "Position";
	private static final String TAG_SLEEPING = "Sleeping";
	private static final String TAG_SLOT = "Slot";
	private static final int ARMOR_CHESTPIECE_TYPE = 1;
	private static final int ARMOR_CHESTPIECE_SLOT = 2;

	private static final FieldAccess<Boolean> IS_SLEEPING = FieldAccess.create(EntityPlayer.class, "sleeping", "field_71083_bS");

	private static final FieldAccess<Integer> SLEEPING_TIMER = FieldAccess.create(EntityPlayer.class, "sleepTimer", "field_71076_b");

	public static final String TEXTURE_SLEEPINGBAG = "openblocks:textures/models/sleepingbag.png";

	public ItemSleepingBag() {
		super(ArmorMaterial.IRON, 2, ARMOR_CHESTPIECE_TYPE);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return TEXTURE_SLEEPINGBAG;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
		return armorSlot == ARMOR_CHESTPIECE_TYPE? ModelSleepingBag.instance : null;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack sleepingBagStack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			ItemStack currentArmor = getChestpieceSlot(player);
			if (currentArmor != null) currentArmor = currentArmor.copy();
			final ItemStack sleepingBagCopy = sleepingBagStack.copy();

			NBTTagCompound tag = ItemUtils.getItemTag(sleepingBagCopy);
			tag.setInteger(TAG_SLOT, player.inventory.currentItem);

			setChestpieceSlot(player, sleepingBagCopy);
			if (currentArmor != null) return currentArmor;
			sleepingBagStack.stackSize = 0;
		}
		return sleepingBagStack;
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
		return armorType == ARMOR_CHESTPIECE_TYPE;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if (!(player instanceof EntityPlayerMP)) return;
		if (player.isPlayerSleeping()) return;

		NBTTagCompound tag = ItemUtils.getItemTag(itemStack);
		if (!EntityPlayerVisitor.IsInBedHookSuccess) {
			player.addChatComponentMessage(new ChatComponentTranslation("openblocks.misc.sleeping_bag_broken"));
			getOutOfSleepingBag(player);
		} else if (tag.getBoolean(TAG_SLEEPING)) {
			// player just woke up
			restoreOriginalSpawn(player, tag);
			restoreOriginalPosition(player, tag);
			tag.removeTag(TAG_SLEEPING);
			getOutOfSleepingBag(player);
		} else {
			// player just put in on
			final BlockPos pos = player.getPosition();

			if (canPlayerSleep(player, world, pos)) {
				storeOriginalSpawn(player, tag);
				storeOriginalPosition(player, tag);
				tag.setBoolean(TAG_SLEEPING, true);
				sleepSafe((EntityPlayerMP)player, world, pos);
			} else getOutOfSleepingBag(player);
		}
	}

	private static void sleepSafe(EntityPlayerMP player, World world, BlockPos pos) {
		// TODO 1.8.9 replace if forge PR is accepted
		if (player.isRiding()) player.mountEntity(null);

		IS_SLEEPING.set(player, true);
		SLEEPING_TIMER.set(player, 0);
		player.playerLocation = pos;

		player.motionX = player.motionZ = player.motionY = 0.0D;
		world.updateAllPlayersSleepingFlag();

		S0APacketUseBed sleepPacket = new S0APacketUseBed(player, pos);
		player.getServerForPlayer().getEntityTracker().sendToAllTrackingEntity(player, sleepPacket);
		player.playerNetServerHandler.sendPacket(sleepPacket);
	}

	private static EnumStatus vanillaCanSleep(EntityPlayer player, World world, BlockPos pos) {
		PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player, pos);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.result != null) return event.result;

		if (!world.provider.isSurfaceWorld()) return EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE;
		if (world.isDaytime()) return EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;

		List<EntityMob> list = world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(pos.getX() - 8, pos.getY() - 5, pos.getZ() - 8, pos.getX() + 8, pos.getY() + 5, pos.getZ() + 8));

		if (!list.isEmpty()) return EntityPlayer.EnumStatus.NOT_SAFE;

		return EntityPlayer.EnumStatus.OK;
	}

	private static boolean canPlayerSleep(EntityPlayer player, World world, BlockPos pos) {
		if (player.isPlayerSleeping() || !player.isEntityAlive()) return false;

		if (!isNotSuffocating(world, pos) || !isSolidEnough(world, pos.down())) {
			player.addChatComponentMessage(new ChatComponentTranslation("openblocks.misc.oh_no_ground"));
			return false;
		}

		final EnumStatus status = vanillaCanSleep(player, world, pos);
		final SleepingBagUseEvent evt = new SleepingBagUseEvent(player, status);
		evt.playerChat = findDefaultChatComponent(status);
		MinecraftForge.EVENT_BUS.post(evt);

		switch (evt.getResult()) {
			case ALLOW:
				return true;
			case DEFAULT:
				if (evt.playerChat != null) player.addChatComponentMessage(evt.playerChat);
				return evt.defaultCanSleep();
			case DENY:
			default:
				if (evt.playerChat != null) player.addChatComponentMessage(evt.playerChat);
				return false;
		}
	}

	private static IChatComponent findDefaultChatComponent(EnumStatus status) {
		switch (status) {
			case NOT_POSSIBLE_NOW:
				return new ChatComponentTranslation("tile.bed.noSleep");
			case NOT_SAFE:
				return new ChatComponentTranslation("tile.bed.notSafe");
			default:
				return null;
		}
	}

	private static AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos) {
		final IBlockState state = world.getBlockState(pos);
		return state.getBlock().getCollisionBoundingBox(world, pos, state);
	}

	private static boolean isNotSuffocating(World world, BlockPos pos) {
		return (world.isAirBlock(pos) || getCollisionBoundingBox(world, pos) == null);
	}

	private static boolean isSolidEnough(World world, BlockPos pos) {
		AxisAlignedBB aabb = getCollisionBoundingBox(world, pos);
		if (aabb == null) return false;

		double dx = aabb.maxX - aabb.minX;
		double dy = aabb.maxY - aabb.minY;
		double dz = aabb.maxZ - aabb.minZ;

		return (dx >= 0.5) && (dy >= 0.5) && (dz >= 0.5);
	}

	private static boolean isChestplate(ItemStack stack) {
		if (stack == null) return false;
		Item item = stack.getItem();
		if (item instanceof ItemSleepingBag) return false;

		if (item instanceof ItemArmor) {
			ItemArmor armorItem = (ItemArmor)item;
			return armorItem.armorType == ARMOR_CHESTPIECE_TYPE;
		}

		return false;
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

	// TODO 1.8.9 may not be needed after Forge PR
	// alternativelly, use event
	private static void storeOriginalSpawn(EntityPlayer player, NBTTagCompound tag) {
		BlockPos spawn = player.getBedLocation(player.worldObj.provider.getDimensionId());
		if (spawn != null) tag.setTag(TAG_SPAWN, NbtUtils.store(spawn));
	}

	private static void restoreOriginalSpawn(EntityPlayer player, NBTTagCompound tag) {
		if (tag.hasKey(TAG_SPAWN)) {
			BlockPos coords = NbtUtils.readBlockPos(tag.getCompoundTag(TAG_SPAWN));
			player.setSpawnChunk(coords, false, player.worldObj.provider.getDimensionId());
			tag.removeTag(TAG_SPAWN);
		}
	}

	// TODO 1.8.9 may not be needed - players seem to be frozen in 1.8.9
	private static void storeOriginalPosition(Entity e, NBTTagCompound tag) {
		tag.setTag(TAG_POSITION, NbtUtils.store(e.posX, e.posY, e.posZ));
	}

	private static void restoreOriginalPosition(Entity e, NBTTagCompound tag) {
		if (tag.hasKey(TAG_POSITION)) {
			Vec3 position = NbtUtils.readVec(tag.getCompoundTag(TAG_POSITION));
			e.setPosition(position.xCoord, position.yCoord, position.zCoord);
			tag.removeTag(TAG_POSITION);
		}
	}

	public static boolean isWearingSleepingBag(EntityPlayer player) {
		ItemStack armor = getChestpieceSlot(player);
		return isSleepingBag(armor);
	}

	private static boolean isSleepingBag(ItemStack armor) {
		return armor != null && armor.getItem() instanceof ItemSleepingBag;
	}

	private static ItemStack setChestpieceSlot(EntityPlayer player, ItemStack chestpiece) {
		return player.inventory.armorInventory[ARMOR_CHESTPIECE_SLOT] = chestpiece;
	}

	private static ItemStack getChestpieceSlot(EntityPlayer player) {
		return player.inventory.armorInventory[ARMOR_CHESTPIECE_SLOT];
	}
}
