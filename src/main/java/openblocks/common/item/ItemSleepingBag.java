package openblocks.common.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelSleepingBag;
import openmods.utils.BlockUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSleepingBag extends ItemArmor {

	private static final int ARMOR_CHESTPIECE = 1;
	public static final String TEXTURE_SLEEPINGBAG = "openblocks:textures/models/sleepingbag.png";

	public ItemSleepingBag() {
		super(ArmorMaterial.IRON, 2, ARMOR_CHESTPIECE);
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
		return armorSlot == ARMOR_CHESTPIECE? ModelSleepingBag.instance : null;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack sleepingBagStack, World world, EntityPlayer player) {
		if (world.isRemote) { return sleepingBagStack; }
		ChunkCoordinates spawn = player.getBedLocation(world.provider.dimensionId);
		EnumStatus status = player.sleepInBedAt((int)player.posX, (int)player.posY, (int)player.posZ);
		if (status == EnumStatus.OK) {
			int i = EntityLiving.getArmorPosition(sleepingBagStack) - 1;
			ItemStack currentArmor = player.getCurrentArmor(i);
			if (currentArmor != null) {
				currentArmor = currentArmor.copy();
			}
			saveOriginalSpawn(spawn, sleepingBagStack);
			player.setCurrentItemOrArmor(i + 1, sleepingBagStack.copy());
			if (currentArmor != null) { return currentArmor; }
			sleepingBagStack.stackSize--;
			return sleepingBagStack;
		}
		return sleepingBagStack;
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
		return armorType == ARMOR_CHESTPIECE;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if (!world.isRemote) {
			NBTTagCompound tag = getOrCreateTag(itemStack);
			if (!player.isPlayerSleeping()) {
				if (tag != null && tag.hasKey("sleeping") && tag.getBoolean("sleeping")) {
					ejectSleepingBagFromPlayer(player, itemStack);
				} else {
					ChunkCoordinates spawn = player.getBedLocation(world.provider.dimensionId);
					EnumStatus status = player.sleepInBedAt((int)player.posX, (int)player.posY, (int)player.posZ);
					if (status == EnumStatus.OK) {
						saveOriginalSpawn(spawn, itemStack);
					} else {
						ejectSleepingBagFromPlayer(player, itemStack);
					}
				}
			} else {
				tag.setBoolean("sleeping", true);
			}
		}
	}

	private static void ejectSleepingBagFromPlayer(EntityPlayer player, ItemStack itemStack) {
		NBTTagCompound tag = getOrCreateTag(itemStack);
		player.setCurrentItemOrArmor(3, null);
		revertSpawnFromItem(player, itemStack);
		tag.setBoolean("sleeping", false);
		BlockUtils.dropItemStackInWorld(player.worldObj, player.posX, player.posY, player.posZ, itemStack);
	}

	private static NBTTagCompound getOrCreateTag(ItemStack itemStack) {
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			itemStack.setTagCompound(tag);
		}
		return tag;
	}

	private static void revertSpawnFromItem(EntityPlayer player, ItemStack itemStack) {
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag != null) {
			if (tag.hasKey("spawnx") && tag.hasKey("spawny")
					&& tag.hasKey("spawnz")) {
				ChunkCoordinates coords = new ChunkCoordinates(tag.getInteger("spawnx"), tag.getInteger("spawny"), tag.getInteger("spawnz"));
				player.setSpawnChunk(coords, false, player.worldObj.provider.dimensionId);
			}
		}
	}

	private static void saveOriginalSpawn(ChunkCoordinates spawn, ItemStack stack) {
		if (spawn != null) {
			NBTTagCompound tag = getOrCreateTag(stack);
			tag.setInteger("spawnx", spawn.posX);
			tag.setInteger("spawny", spawn.posY);
			tag.setInteger("spawnz", spawn.posZ);
		}
	}

	public static boolean isWearingSleepingBag(EntityPlayer player) {
		ItemStack armor = player.getCurrentArmor(2);
		return armor != null && armor.getItem() instanceof ItemSleepingBag;
	}
}
