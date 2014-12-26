package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemPaintCan;
import openmods.api.IActivateAwareTile;
import openmods.api.ICustomHarvestDrops;
import openmods.api.IPlaceAwareTile;
import openmods.sync.SyncableInt;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

public class TileEntityPaintCan extends SyncedTileEntity implements IPlaceAwareTile, IActivateAwareTile, ICustomHarvestDrops {

	private SyncableInt color;
	private SyncableInt amount;

	@Override
	protected void createSyncedFields() {
		color = new SyncableInt();
		amount = new SyncableInt();
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		color.set(ItemPaintCan.getColorFromStack(stack));
		amount.set(ItemPaintCan.getAmountFromStack(stack));
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote && amount.get() > 0) {
			ItemStack heldStack = player.getHeldItem();
			if (heldStack != null && heldStack.getItem() instanceof ItemPaintBrush) {
				ItemPaintBrush.setColor(heldStack, color.get());
				heldStack.setItemDamage(0);
				amount.modify(-1);
				sync();
				worldObj.playSoundAtEntity(player, "game.neutral.swim.splash", 0.1F, 1.2F);
			}
		}

		if (amount.get() <= 0 && !worldObj.isRemote) {
			ItemStack item = new ItemStack(Items.bucket);
			BlockUtils.dropItemStackInWorld(worldObj, xCoord, yCoord, zCoord, item);
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
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

	@Override
	public boolean suppressNormalHarvestDrops() {
		return true;
	}

	@Override
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops) {
		drops.add(ItemPaintCan.createStack(getColor(), getAmount()));
	}

}
