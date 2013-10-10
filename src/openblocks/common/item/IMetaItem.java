package openblocks.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public interface IMetaItem {

	public Icon getIcon();

	public String getUnlocalizedName(ItemStack stack);

	public boolean hitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase player);

	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10);

	public ItemStack onItemRightClick(ItemStack itemStack, EntityPlayer player, World world);

	public void registerIcons(IconRegister register);

	public void addRecipe();

	public void addToCreativeList(int itemId, int meta, List<ItemStack> result);

	public boolean hasEffect(int renderPass);
}
