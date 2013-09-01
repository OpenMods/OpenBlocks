package openblocks.common.tileentity;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.api.IAwareTile;
import openblocks.utils.CompatibilityUtils;

public class TileEntityCannon extends OpenTileEntity implements IAwareTile {

	public double motionX = 0.0;
	public double motionY = 0.0;
	public double motionZ = 0.0;
	
	@Override
	public void updateEntity() {
		super.updateEntity();

		List<EntityPlayer> playersOnTop = (List<EntityPlayer>)worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1));
		if (playersOnTop.size() > 0) {
			EntityPlayer player = playersOnTop.get(0);
			double pitch = Math.toRadians(player.rotationPitch);
			double yaw = Math.toRadians(player.rotationYawHead - 180);
			motionX = Math.sin(yaw) * Math.cos(pitch);
			motionY = Math.cos(pitch);
			motionZ = -Math.cos(yaw) * Math.cos(pitch);
		}

		if (!worldObj.isRemote) {
			if (worldObj.getWorldTime() % 2 == 0) {
					ItemStack stack = new ItemStack(Item.appleGold);
					EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord + 2, zCoord + 0.5, stack);
					item.motionX = motionX;
					item.motionY = motionY;
					item.motionZ = motionZ;
					worldObj.spawnEntityInWorld(item);
				
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
		return false;
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

}
