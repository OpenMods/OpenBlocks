package openblocks.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.rubbish.BrickManager;
import openblocks.rubbish.BrickManager.BowelContents;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTastyClay extends ItemFood {

	public ItemTastyClay() {
		super(Config.itemTastyClay, 1, 0.1f, false);
		setAlwaysEdible();
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setUnlocalizedName("openblocks.tasty_clay");
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer entity) {
		BowelContents contents = BrickManager.getProperty(entity);
		if (contents != null) {
			contents.brickCount++;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:yum_yum");
	}
}
