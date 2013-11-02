package openblocks.common.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumStatus;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelSleepingBag;
import openblocks.utils.BlockUtils;
import openblocks.utils.InventoryUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSleepingBag extends ItemArmor {

	private static final int ARMOR_CHESTPIECE = 1;
	public static final String TEXTURE_SLEEPINGBAG = "openblocks:textures/models/sleepingbag.png";

	public ItemSleepingBag() {
		super(Config.itemSleepingBagId, EnumArmorMaterial.IRON, 2, ARMOR_CHESTPIECE);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setUnlocalizedName("openblocks.sleepingbag");
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		return TEXTURE_SLEEPINGBAG;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
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
			if (spawn != null) {
				NBTTagCompound tag = sleepingBagStack.getTagCompound();
				if (tag == null) {
					tag = new NBTTagCompound();
				}
				tag.setInteger("spawnx", spawn.posX);
				tag.setInteger("spawny", spawn.posY);
				tag.setInteger("spawnz", spawn.posZ);
				sleepingBagStack.setTagCompound(tag);
			}
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
	public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack itemStack) {
		if (!world.isRemote) {
			if (!player.isPlayerSleeping()) {
				player.setCurrentItemOrArmor(3, null);
				NBTTagCompound tag = itemStack.getTagCompound();
				if (tag != null) {
					if (tag.hasKey("spawnx") && tag.hasKey("spawny")
							&& tag.hasKey("spawnz")) {
						ChunkCoordinates coords = new ChunkCoordinates(tag.getInteger("spawnx"), tag.getInteger("spawny"), tag.getInteger("spawnz"));
						player.setSpawnChunk(coords, false, world.provider.dimensionId);
					}
				}
				InventoryUtils.insertItemIntoInventory(player.inventory, itemStack);
				if (itemStack.stackSize > 0) {
					BlockUtils.dropItemStackInWorld(world, player.posX, player.posY, player.posZ, itemStack);
				}
			}
		}
	}

	public static boolean isWearingSleepingBag(EntityPlayer player) {
		ItemStack armor = player.getCurrentArmor(2);
		return armor != null && armor.getItem() instanceof ItemSleepingBag;
	}
}
