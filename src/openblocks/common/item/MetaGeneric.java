package openblocks.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class MetaGeneric implements IMetaItem {

	public static class SmeltingRecipe {
		public final int itemId;
		public final int itemMeta;
		public final ItemStack result;
		public final float experience;

		private SmeltingRecipe(int itemId, int itemMeta, ItemStack result, float experience) {
			this.itemId = itemId;
			this.itemMeta = itemMeta;
			this.result = result.copy();
			this.experience = experience;
		}
	}

	private String name;
	private Icon icon;
	private Object[] recipes;
	private boolean visibleInCreative = true;

	public MetaGeneric(String name, Object... recipes) {
		this.name = name;
		this.recipes = recipes;
	}

	public MetaGeneric hideFromCreative() {
		visibleInCreative = false;
		return this;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return String.format("openblocks.%s", name);
	}

	@Override
	public boolean hitEntity(ItemStack itemStack, EntityLivingBase target, EntityLivingBase player) {
		return false;
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, EntityPlayer player, World world) {
		return itemStack;
	}

	@Override
	public void registerIcons(IconRegister register) {
		registerIcon(register, name);
	}

	protected void registerIcon(IconRegister register, String name) {
		icon = register.registerIcon(String.format("openblocks:%s", name));
	}

	@Override
	public void addRecipe() {
		if (recipes == null) return;

		final FurnaceRecipes furnaceRecipes = FurnaceRecipes.smelting();
		@SuppressWarnings("unchecked")
		final List<IRecipe> craftingRecipes = CraftingManager.getInstance().getRecipeList();
		for (Object tmp : recipes) {
			if (tmp instanceof SmeltingRecipe) {
				SmeltingRecipe recipe = (SmeltingRecipe)tmp;
				furnaceRecipes.addSmelting(recipe.itemId, recipe.itemMeta, recipe.result, recipe.experience);
			} else if (tmp instanceof IRecipe) {
				craftingRecipes.add((IRecipe)tmp);
			} else throw new IllegalArgumentException("Invalid recipe object: "
					+ tmp);
		}
	}

	@Override
	public void addToCreativeList(int itemId, int meta, List<ItemStack> result) {
		if (visibleInCreative) {
			result.add(new ItemStack(itemId, 1, meta));
		}
	}

	@Override
	public boolean hasEffect(int renderPass) {
		return false;
	}

}