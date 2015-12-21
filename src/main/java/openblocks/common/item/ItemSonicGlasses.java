package openblocks.common.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelSonicGlasses;
import openmods.infobook.BookDocumentation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation
public class ItemSonicGlasses extends ItemArmor {

	private static final int ARMOR_HELMET = 0;

	public ItemSonicGlasses() {
		super(ArmorMaterial.IRON, 2, ARMOR_HELMET);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
		return armorType == ARMOR_HELMET;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return itemIcon;
	}

	@SideOnly(Side.CLIENT)
	private ModelSonicGlasses model;

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
		if (armorSlot == ARMOR_HELMET) {
			if (model == null) model = new ModelSonicGlasses();
			return model;
		}

		return null;
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return "openblocks:textures/models/glasses.png";
	}

}
