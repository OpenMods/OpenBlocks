package openblocks.common.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemPaintCan;
import openmods.api.IActivateAwareTile;
import openmods.sync.SyncableInt;
import openmods.sync.drops.DroppableTileEntity;
import openmods.sync.drops.StoreOnDrop;
import openmods.utils.BlockUtils;

public class TileEntityPaintCan extends DroppableTileEntity implements IActivateAwareTile {

	@StoreOnDrop(name = ItemPaintCan.TAG_COLOR)
	private SyncableInt color;

	@StoreOnDrop(name = ItemPaintCan.TAG_AMOUNT)
	private SyncableInt amount;

	@Override
	protected void createSyncedFields() {
		color = new SyncableInt();
		amount = new SyncableInt();
	}

	@Override
	public boolean onBlockActivated(PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && hand == Hand.MAIN_HAND && amount.get() > 0) {
			final ItemStack heldStack = player.getHeldItemMainhand();
			if (!heldStack.isEmpty() && heldStack.getItem() instanceof ItemPaintBrush) {
				ItemPaintBrush.setColor(heldStack, color.get());
				heldStack.setItemDamage(0);
				amount.modify(-1);

				if (amount.get() <= 0) {
					ItemStack item = new ItemStack(Items.BUCKET);
					BlockUtils.dropItemStackInWorld(world, pos, item);
					world.setBlockToAir(pos);
				} else {
					sync();
				}
				world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.1F, 1.2F);
				return true;
			}
		}
		return false;
	}

	public int getColor() {
		return color.get();
	}

	public int getAmount() {
		return amount.get();
	}

	public void setAmount(int amt) {
		amount.set(amt);
	}

}
