package openblocks.common.tileentity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import openblocks.client.fx.FXLiquidSpray;
import openblocks.common.api.IAwareTile;
import openblocks.common.api.ISurfaceAttachment;
import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;
import openblocks.sync.SyncMapTile;
import openblocks.sync.SyncableFlags;
import openblocks.utils.BlockUtils;

public class TileEntitySprinkler extends OpenTileEntity implements IAwareTile,
		ISurfaceAttachment, ITankContainer, IInventory, ISyncHandler {

	public static final int TICKS_PER_DIRECTION = 100;
	
	private ForgeDirection rotation = ForgeDirection.EAST;
	
	private SyncMapTile syncMap = new SyncMapTile();
	
	private SyncableFlags flags = new SyncableFlags();
	
	private LiquidStack water = new LiquidStack(Block.waterStill, 1);
	
	public enum Flags {
		isClockwise
	}
	
	public enum Keys {
		flags
	}
	
	public TileEntitySprinkler() {
		syncMap.put(Keys.flags, flags);
	}
	
	public void updateEntity() {
		super.updateEntity();
		
		if (!worldObj.isRemote) {
			if (flags.ticksSinceChange(Flags.isClockwise) > TICKS_PER_DIRECTION) {
				flags.set(Flags.isClockwise, !flags.get(Flags.isClockwise));
			}
			if (worldObj.rand.nextDouble() < 1.0/100) {
				int x = xCoord + worldObj.rand.nextInt(9) - 5;
				int y = yCoord;
				int z = zCoord + worldObj.rand.nextInt(9) - 5;
				for (int i = -1; i <= 1; i++) {
					y += i;
					int blockId = worldObj.getBlockId(x, y, z);
					if (Block.blocksList[blockId] instanceof BlockCrops) {
						((BlockCrops)Block.blocksList[blockId]).fertilize(worldObj, x, y, z);
					}
				}
			}
		} else {
			for (int i = 0; i < 5; i++) {
				FXLiquidSpray spray = new FXLiquidSpray(worldObj, water, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, worldObj.getWorldVec3Pool().getVecFromPool(getSprayPitch() * rotation.offsetX, 0, getSprayPitch() * rotation.offsetZ), 0.5f);
				Minecraft.getMinecraft().effectRenderer.addEffect(spray);
			}
		}
		syncMap.sync(worldObj, this, xCoord, yCoord, zCoord, 1);
	}
	
	public boolean isTurningClockwise() {
		return flags.get(Flags.isClockwise);
	}
	
	public double getPercentageForDirection() {
		return 1.0 / TICKS_PER_DIRECTION * flags.ticksSinceChange(Flags.isClockwise);
	}
	
	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInvName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInvNameLocalized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openChest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeChest() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		rotation = BlockUtils.get2dOrientation(player);
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub

	}

	public ForgeDirection getRotation() {
		return rotation;
	}

	@Override
	public SyncMap getSyncMap() {
		return syncMap;
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
	}

	@Override
	public void writeIdentifier(DataOutputStream dos) throws IOException {
		dos.writeInt(xCoord);
		dos.writeInt(yCoord);
		dos.writeInt(zCoord);
	}

	public float getSprayPitch() {
		return (float)(getSprayAngle() + (Math.PI / 2));
	}
	
	public float getSprayAngle() {
		double range = Math.PI * 0.4;
		float angle = (float)((float) -(range / 2) + (range * getPercentageForDirection()));
		if (!isTurningClockwise()) {
			angle = -angle;
		}
		return angle;
	}

}
