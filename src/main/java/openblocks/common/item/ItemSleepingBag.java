package openblocks.common.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelSleepingBag;
import openmods.utils.BlockUtils;
import openmods.utils.ItemUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSleepingBag extends ItemArmor {

	private static final String TAG_SPAWN_Z = "spawnz";
	private static final String TAG_SPAWN_Y = "spawny";
	private static final String TAG_SPAWN_X = "spawnx";
	private static final String TAG_SLEEPING = "sleeping";
	private static final int ARMOR_CHESTPIECE_TYPE = 1;
	private static final int ARMOR_CHESTPIECE_SLOT = 2;

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
		if (world.isRemote) return;
		if (player.isPlayerSleeping()) return;

		NBTTagCompound tag = ItemUtils.getItemTag(itemStack);
		if (tag.getBoolean(TAG_SLEEPING)) {
			// player just woke up
			revertSpawnFromTag(player, tag);
			tag.removeTag(TAG_SLEEPING);
			ejectSleepingBagFromPlayer(player);
		} else {
			// player just put in on
			final int posX = MathHelper.floor_double(player.posX);
			final int posY = MathHelper.floor_double(player.posY + 1);
			final int posZ = MathHelper.floor_double(player.posZ);

			if (!world.getBlock(posX, posY + 1, posZ).isAir(world, posX, posY, posZ)) {
				player.addChatComponentMessage(new ChatComponentTranslation("openblocks.misc.oh_no_ceiling"));
				ejectSleepingBagFromPlayer(player);
			} else {
				EnumStatus status = player.sleepInBedAt(posX, posY, posZ);
				if (status == EnumStatus.OK) {
					ChunkCoordinates spawn = player.getBedLocation(world.provider.dimensionId);
					player.setPosition(player.posX, player.posY, player.posZ);
					saveOriginalSpawn(spawn, tag);
					tag.setBoolean(TAG_SLEEPING, true);
				} else {
					ejectSleepingBagFromPlayer(player);
				}
			}
		}

	}

	private static void ejectSleepingBagFromPlayer(EntityPlayer player) {
		ItemStack stack = getChestpieceSlot(player);
		if (isSleepingBag(stack)) {
			setChestPieceSlot(player, null);
			BlockUtils.dropItemStackInWorld(player.worldObj, player.posX, player.posY, player.posZ, stack);
		}
	}

	private static void revertSpawnFromTag(EntityPlayer player, NBTTagCompound tag) {
		if (tag.hasKey(TAG_SPAWN_X) && tag.hasKey(TAG_SPAWN_Y) && tag.hasKey(TAG_SPAWN_Z)) {
			ChunkCoordinates coords = new ChunkCoordinates(tag.getInteger(TAG_SPAWN_X), tag.getInteger(TAG_SPAWN_Y), tag.getInteger(TAG_SPAWN_Z));
			player.setSpawnChunk(coords, false, player.worldObj.provider.dimensionId);
			tag.removeTag(TAG_SPAWN_X);
			tag.removeTag(TAG_SPAWN_Y);
			tag.removeTag(TAG_SPAWN_Z);
		}
	}

	private static void saveOriginalSpawn(ChunkCoordinates spawn, NBTTagCompound tag) {
		if (spawn != null) {
			tag.setInteger(TAG_SPAWN_X, spawn.posX);
			tag.setInteger(TAG_SPAWN_Y, spawn.posY);
			tag.setInteger(TAG_SPAWN_Z, spawn.posZ);
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
