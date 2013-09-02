package openblocks.common.tileentity;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.api.IAwareTile;
import openblocks.common.entity.EntityCannon;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableDouble;
import openblocks.utils.CompatibilityUtils;
import openblocks.utils.InventoryUtils;

public class TileEntityCannon extends NetworkedTileEntity implements IAwareTile {

	
	private EntityCannon cannon = null;
	
	public SyncableDouble motionX = new SyncableDouble();
	public SyncableDouble motionY = new SyncableDouble();
	public SyncableDouble motionZ = new SyncableDouble();
	
	public enum Keys {
		motionX,
		motionY,
		motionZ
	}
	
	public TileEntityCannon() {
		addSyncedObject(Keys.motionX, motionX);
		addSyncedObject(Keys.motionY, motionY);
		addSyncedObject(Keys.motionZ, motionZ);
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote && cannon != null && cannon.riddenByEntity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) cannon.riddenByEntity;
			double pitch = Math.toRadians(player.rotationPitch);
			double yaw = Math.toRadians(player.rotationYawHead - 180);
			motionX.setValue(Math.sin(yaw) * Math.cos(pitch));
			motionY.setValue(Math.sin(-pitch));
			motionZ.setValue(-Math.cos(yaw) * Math.cos(pitch));

			System.out.println(motionY.getValue());
			sync();
		}

		if (!worldObj.isRemote) {
			if (worldObj.getWorldTime() % 20 == 0) {
				if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
					for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
						IInventory inventory = InventoryUtils.getInventory(worldObj, xCoord, yCoord, zCoord, direction);
						if (inventory != null) {
							ItemStack stack = InventoryUtils.removeItemStack(inventory);
							if (stack != null) {
								EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord + 2, zCoord + 0.5, stack);
								item.delayBeforeCanPickup = 20;
								item.motionX = motionX.getValue();
								item.motionY = motionY.getValue();
								item.motionZ = motionZ.getValue();
								worldObj.spawnEntityInWorld(item);
								break;
							}
						}
					}
					
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB box = super.getRenderBoundingBox();
		return box.expand(32.0, 32.0, 32.0);
	}
	
	@Override
	public void onBlockBroken() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBlockAdded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			cannon = new EntityCannon(worldObj, xCoord, yCoord, zCoord);
			worldObj.spawnEntityInWorld(cannon);
			player.mountEntity(cannon);
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		// TODO Auto-generated method stub
		
	}

}
