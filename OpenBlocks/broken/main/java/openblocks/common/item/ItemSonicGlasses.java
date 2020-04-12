package openblocks.common.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelSonicGlasses;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class ItemSonicGlasses extends ArmorItem {

	private static final String TEXTURE = OpenBlocks.location("textures/models/glasses.png").toString();

	public ItemSonicGlasses() {
		super(ArmorMaterial.IRON, 2, EquipmentSlotType.HEAD);
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EquipmentSlotType armorType, Entity entity) {
		return armorType == EquipmentSlotType.HEAD;
	}

	@SideOnly(Side.CLIENT)
	private ModelSonicGlasses model;

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, ModelBiped _default) {
		if (armorSlot == EquipmentSlotType.HEAD) {
			if (model == null) model = new ModelSonicGlasses();
			return model;
		}

		return null;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		return TEXTURE;
	}

}
