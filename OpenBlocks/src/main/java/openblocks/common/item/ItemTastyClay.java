package openblocks.common.item;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openblocks.rubbish.BrickManager;
import openblocks.rubbish.BrickManager.BowelContents;

public class ItemTastyClay extends ItemFood {

	public ItemTastyClay() {
		super(1, 0.1f, false);
		setAlwaysEdible();
	}

	@Override
	protected void onFoodEaten(@Nonnull ItemStack stack, World world, PlayerEntity entity) {
		if (!world.isRemote) {
			BowelContents contents = BrickManager.getProperty(entity);
			if (contents != null) {
				contents.brickCount++;
			}
		}
	}
}
