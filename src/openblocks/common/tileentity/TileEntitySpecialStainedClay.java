package openblocks.common.tileentity;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

import openblocks.common.api.IPlaceAwareTile;
import openblocks.common.block.BlockSpecialStainedClay;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableByteArray;
import openblocks.sync.SyncableInt;

public class TileEntitySpecialStainedClay extends SyncedTileEntity implements IPlaceAwareTile {

	private SyncableInt color;
	private SyncableByteArray texture;
	
	public int getColor() {
		return color.getValue();
	}
	
	public byte[] getTexture() {
		return texture.getValue();
	}
	
	@Override
	public void initialize() {
		if (!worldObj.isRemote) {
			// turn on for debugging, dont commit with this in. kthx
			//byte[] bytes = new byte[16*8];
			//new Random().nextBytes(bytes);
			//texture.setValue(bytes);
		}
	}
	
	@Override
	public void onSynced(List<ISyncableObject> changes) {
		if (changes.contains(texture)) {
			worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	protected void createSyncedFields() {
		color = new SyncableInt();
		texture = new SyncableByteArray();
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		color.setValue(BlockSpecialStainedClay.getColorFromNBT(stack));
	}

}
