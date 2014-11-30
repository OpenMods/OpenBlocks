package openblocks.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelSleepingBag;
import openmods.reflection.FieldAccess;
import openmods.utils.BlockUtils;
import openmods.utils.ItemUtils;
import openmods.utils.TagUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSleepingBag extends ItemArmor {

	private static final String TAG_SPAWN = "Spawn";
	private static final String TAG_POSITION = "Position";
	private static final String TAG_SLEEPING = "Sleeping";
	private static final int ARMOR_CHESTPIECE_TYPE = 1;
	private static final int ARMOR_CHESTPIECE_SLOT = 2;

	private static final FieldAccess<Boolean> IS_SLEEPING = FieldAccess.create(EntityPlayer.class, "sleeping", "field_71083_bS");

	private static final FieldAccess<Integer> SLEEPING_TIMER = FieldAccess.create(EntityPlayer.class, "sleepTimer", "field_71076_b");

	public static final String TEXTURE_SLEEPINGBAG = "openblocks:textures/models/sleepingbag.png";

	public ItemSleepingBag() {
		super(ArmorMaterial.IRON, 2, ARMOR_CHESTPIECE_TYPE);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return TEXTURE_SLEEPINGBAG;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:sleepingbag");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
		return armorSlot == ARMOR_CHESTPIECE_TYPE? ModelSleepingBag.instance : null;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack sleepingBagStack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			ItemStack currentArmor = player.getCurrentArmor(ARMOR_CHESTPIECE_SLOT);
			if (currentArmor != null) currentArmor = currentArmor.copy();
			setChestPieceSlot(player, sleepingBagStack.copy());
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
		if (tag.getBoolean(TAG_SLEEPING)) {
			// player just woke up
			restoreOriginalSpawn(player, tag);
			restoreOriginalPosition(player, tag);
			ejectSleepingBagFromPlayer(player);
			tag.removeTag(TAG_SLEEPING);
		} else {
			// player just put in on
			final int posX = MathHelper.floor_double(player.posX);
			final int posY = MathHelper.floor_double(player.posY + 0.99);
			final int posZ = MathHelper.floor_double(player.posZ);

			final int sleepY = posY;
			final int groundY = posY - 1;

			if (!checkGroundCollision(world, posX, groundY, posZ)) {
				player.addChatComponentMessage(new ChatComponentTranslation("openblocks.misc.oh_no_ground"));
				ejectSleepingBagFromPlayer(player);
			} else {
				EnumStatus status = sleepSafe((EntityPlayerMP)player, world, posX, sleepY, posZ);
				if (status == EnumStatus.OK) {
					storeOriginalSpawn(player, tag);
					storeOriginalPosition(player, tag);
					tag.setBoolean(TAG_SLEEPING, true);
				} else {
					if (status == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW) {
						player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.noSleep"));
					} else if (status == EntityPlayer.EnumStatus.NOT_SAFE) {
						player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe"));
					}

					ejectSleepingBagFromPlayer(player);
				}
			}
		}
	}

	private static EntityPlayer.EnumStatus sleepSafe(EntityPlayerMP player, World world, int x, int y, int z) {
		PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player, x, y, z);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.result != null) return event.result;

		if (player.isPlayerSleeping() || !player.isEntityAlive()) return EntityPlayer.EnumStatus.OTHER_PROBLEM;
		if (!world.provider.isSurfaceWorld()) return EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE;
		if (world.isDaytime()) return EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;

		List<?> list = world.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(x - 8, y - 5, z - 8, x + 8, y + 5, z + 8));
		if (!list.isEmpty()) return EntityPlayer.EnumStatus.NOT_SAFE;

		if (player.isRiding()) player.mountEntity(null);

		IS_SLEEPING.set(player, true);
		SLEEPING_TIMER.set(player, 0);
		player.playerLocation = new ChunkCoordinates(x, y, z);

		player.motionX = player.motionZ = player.motionY = 0.0D;
		world.updateAllPlayersSleepingFlag();

		S0APacketUseBed sleepPacket = new S0APacketUseBed(player, x, y, z);
		player.getServerForPlayer().getEntityTracker().func_151247_a(player, sleepPacket);
		player.playerNetServerHandler.sendPacket(sleepPacket);

		return EntityPlayer.EnumStatus.OK;
	}

	private static boolean checkGroundCollision(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		AxisAlignedBB aabb = block.getCollisionBoundingBoxFromPool(world, x, y, z);
		if (aabb == null) return true;

		double dx = aabb.maxX - aabb.minX;
		double dy = aabb.maxY - aabb.minY;
		double dz = aabb.maxZ - aabb.minZ;

		return (dx >= 0.5) && (dy >= 0.5) && (dz >= 0.5);
	}

	private static void ejectSleepingBagFromPlayer(EntityPlayer player) {
		ItemStack stack = getChestpieceSlot(player);
		if (isSleepingBag(stack)) {
			setChestPieceSlot(player, null);
			BlockUtils.dropItemStackInWorld(player.worldObj, player.posX, player.posY, player.posZ, stack);
		}
	}

	private static void storeOriginalSpawn(EntityPlayer player, NBTTagCompound tag) {
		ChunkCoordinates spawn = player.getBedLocation(player.worldObj.provider.dimensionId);
		if (spawn != null) tag.setTag(TAG_SPAWN, TagUtils.store(spawn));
	}

	private static void restoreOriginalSpawn(EntityPlayer player, NBTTagCompound tag) {
		if (tag.hasKey(TAG_SPAWN)) {
			ChunkCoordinates coords = TagUtils.readCoord(tag.getCompoundTag(TAG_SPAWN)).asChunkCoordinate();
			player.setSpawnChunk(coords, false, player.worldObj.provider.dimensionId);
			tag.removeTag(TAG_SPAWN);
		}
	}

	private static void storeOriginalPosition(Entity e, NBTTagCompound tag) {
		tag.setTag(TAG_POSITION, TagUtils.store(e.posX, e.posY, e.posZ));
	}

	private static void restoreOriginalPosition(Entity e, NBTTagCompound tag) {
		if (tag.hasKey(TAG_POSITION)) {
			Vec3 position = TagUtils.readVec(tag.getCompoundTag(TAG_POSITION));
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

	private static ItemStack setChestPieceSlot(EntityPlayer player, ItemStack chestpiece) {
		return player.inventory.armorInventory[ARMOR_CHESTPIECE_SLOT] = chestpiece;
	}

	private static ItemStack getChestpieceSlot(EntityPlayer player) {
		return player.inventory.armorInventory[ARMOR_CHESTPIECE_SLOT];
	}
}
