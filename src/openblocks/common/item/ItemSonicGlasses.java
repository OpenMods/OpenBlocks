package openblocks.common.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelSonicGlasses;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSonicGlasses extends ItemArmor {

	private static final int ARMOR_HELMET = 0;

	public ItemSonicGlasses() {
		super(OpenBlocks.Config.itemSonicGlassesId, EnumArmorMaterial.IRON, 2, ARMOR_HELMET);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setMaxStackSize(1);
	}

	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
		return armorType == ARMOR_HELMET;
	}

	@Override
	public void registerIcons(IconRegister register) {
		itemIcon = register.registerIcon("openblocks:sonicglasses");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int par1) {
		return itemIcon;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return "item.openblocks.sonicglasses";
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
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		return "openblocks:textures/models/glasses.png";
	}

}
