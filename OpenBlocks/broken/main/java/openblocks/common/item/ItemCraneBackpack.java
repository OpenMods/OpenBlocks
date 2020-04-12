package openblocks.common.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelCraneBackpack;
import openblocks.common.CraneRegistry;
import openmods.infobook.BookDocumentation;

@BookDocumentation(customName = "crane_backpack", hasVideo = true)
public class ItemCraneBackpack extends ArmorItem {

	public static final String TEXTURE_CRANE = OpenBlocks.location("textures/models/crane.png").toString();

	public ItemCraneBackpack() {
		super(ArmorMaterial.IRON, 2, EquipmentSlotType.CHEST);
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EquipmentSlotType armorType, Entity entity) {
		return armorType == EquipmentSlotType.CHEST;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, ModelBiped _default) {
		return armorSlot == EquipmentSlotType.CHEST? ModelCraneBackpack.instance : null;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType armorSlot, String type) {
		return TEXTURE_CRANE;
	}

	private static boolean isPointInBlock(World world, PlayerEntity player, double radius) {
		double posX = player.posX + radius * MathHelper.cos((player.rotationYaw + 90) * (float)Math.PI / 180);
		double posY = player.posY + player.getEyeHeight() + 0.2;
		double posZ = player.posZ + radius * MathHelper.sin((player.rotationYaw + 90) * (float)Math.PI / 180);

		AxisAlignedBB aabb = new AxisAlignedBB(posX - 0.1, posY - 0.1, posZ - 0.1, posX + 0.1, posY + 0.1, posZ + 0.1);
		return !world.getCollisionBoxes(player, aabb).isEmpty();
	}

	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack itemStack) {
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

	public static boolean isWearingCrane(LivingEntity player) {
		ItemStack armor = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
		return !armor.isEmpty() && armor.getItem() instanceof ItemCraneBackpack;
	}
}
