package openblocks.common.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelCraneBackpack;
import openblocks.common.CraneRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCraneBackpack extends ItemArmor {

	private static final int ARMOR_CHESTPIECE = 1;
	public static final String TEXTURE_CRANE = "openblocks:textures/models/crane.png";

	public ItemCraneBackpack() {
		super(ArmorMaterial.IRON, 2, ARMOR_CHESTPIECE);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
		return armorType == ARMOR_CHESTPIECE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
		return armorSlot == ARMOR_CHESTPIECE? ModelCraneBackpack.instance : null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIIconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:crane_backpack");
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		return TEXTURE_CRANE;
	}

	private static boolean isPointInBlock(World world, EntityPlayer player, double radius) {
		double posX = player.posX
				+ radius
				* MathHelper.cos((player.rotationYaw + 90) * (float)Math.PI
						/ 180);
		double posY = player.posY + player.getEyeHeight() + 0.2;
		double posZ = player.posZ
				+ radius
				* MathHelper.sin((player.rotationYaw + 90) * (float)Math.PI
						/ 180);

		AxisAlignedBB aabb = AxisAlignedBB.getAABBPool().getAABB(posX - 0.1, posY - 0.1, posZ - 0.1, posX + 0.1, posY + 0.1, posZ + 0.1);
		return !world.getCollidingBlockBounds(aabb).isEmpty();
	}

	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack itemStack) {
		CraneRegistry.Data data = CraneRegistry.instance.getData(player, true);
		if (!world.isRemote) CraneRegistry.instance.ensureMagnetExists(player);

		if (Config.doCraneCollisionCheck) {
			boolean isColliding = isPointInBlock(world, player, CraneRegistry.ARM_RADIUS)
					|| isPointInBlock(world, player, 2 * CraneRegistry.ARM_RADIUS / 3)
					|| isPointInBlock(world, player, CraneRegistry.ARM_RADIUS / 3);

			if (isColliding) {
				player.rotationYaw = data.prevYaw;
				player.posX = data.prevPosX;
				player.posY = data.prevPosY;
				player.posZ = data.prevPosZ;
				player.motionX = player.motionY = player.motionZ = 0;
			} else {
				data.prevYaw = player.rotationYaw;
				data.prevPosX = player.posX;
				data.prevPosY = player.posY;
				data.prevPosZ = player.posZ;
			}
		}
	}

	public static boolean isWearingCrane(EntityPlayer player) {
		ItemStack armor = player.getCurrentArmor(2);
		return armor != null && armor.getItem() instanceof ItemCraneBackpack;
	}
}
