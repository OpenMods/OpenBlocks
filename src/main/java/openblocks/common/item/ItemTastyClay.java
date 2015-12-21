package openblocks.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.rubbish.BrickManager;
import openblocks.rubbish.BrickManager.BowelContents;

public class ItemTastyClay extends ItemFood {

	public ItemTastyClay() {
		super(1, 0.1f, false);
		setAlwaysEdible();
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World world, EntityPlayer entity) {
		BowelContents contents = BrickManager.getProperty(entity);
		if (contents != null) {
			contents.brickCount++;
		}
	}
}
