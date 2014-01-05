package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemPaintCan;
import openmods.api.IActivateAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableInt;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

public class TileEntityPaintCan extends SyncedTileEntity implements IPlaceAwareTile, IActivateAwareTile {

	private SyncableInt color;
	private SyncableInt amount;

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

	@Override
	protected void createSyncedFields() {
		color = new SyncableInt();
		amount = new SyncableInt();
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		color.setValue(ItemPaintCan.getColorFromStack(stack));
		amount.setValue(ItemPaintCan.getAmountFromStack(stack));
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote && amount.getValue() > 0) {
			ItemStack heldStack = player.getHeldItem();
			if (heldStack != null && heldStack.getItem() instanceof ItemPaintBrush) {
				ItemPaintBrush.setColor(heldStack, color.getValue());
				heldStack.setItemDamage(0);
				amount.modify(-1);
				sync();
				worldObj.playSoundAtEntity(player, "liquid.swim", 0.1F, 1.2F);
			}
		}

		if (amount.getValue() <= 0 && !worldObj.isRemote) {
			ItemStack item = new ItemStack(Item.bucketEmpty);
			BlockUtils.dropItemStackInWorld(worldObj, xCoord, yCoord, zCoord, item);
			worldObj.setBlock(xCoord, yCoord, zCoord, 0);
		}
		return false;
	}

	public int getColor() {
		return color.getValue();
	}

	public int getAmount() {
		return amount.getValue();
	}

	public void setAmount(int amt) {
		amount.setValue(amt);
	}

}
