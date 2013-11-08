package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

import openblocks.common.api.IPlaceAwareTile;
import openblocks.common.block.BlockSpecialStainedClay;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;

public class TileEntitySpecialStainedClay extends SyncedTileEntity implements IPlaceAwareTile {

	private SyncableInt color;
	
	public int getColor() {
		return color.getValue();
	}
	
	@Override
	public void onSynced(List<ISyncableObject> changes) {
		
	}

	@Override
	protected void createSyncedFields() {
		color = new SyncableInt();
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		color.setValue(BlockSpecialStainedClay.getColorFromNBT(stack));
	}

}
