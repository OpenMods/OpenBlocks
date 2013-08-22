package openblocks.common.tileentity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import openblocks.OpenBlocks;
import openblocks.common.api.IAwareTile;
import openblocks.common.api.ISurfaceAttachment;
import openblocks.common.block.BlockSprinkler;
import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;
import openblocks.sync.SyncMapTile;
import openblocks.sync.SyncableDirection;
import openblocks.sync.SyncableFlags;
import openblocks.utils.BlockUtils;

public class TileEntitySprinkler extends OpenTileEntity implements IAwareTile,
		ISurfaceAttachment, ITankContainer, IInventory, ISyncHandler {

	public static final int TICKS_PER_DIRECTION = 100;

	private SyncMapTile syncMap = new SyncMapTile();

	private SyncableFlags flags = new SyncableFlags();

	private LiquidStack water = new LiquidStack(Block.waterStill, 1);
	
	private LiquidTank tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);
	
	/* Apx 60 degrees */
	private static final double angularRotationLimit = (5D * Math.PI) / 3D;

	public enum Flags {
		enabled
	}

	public enum Keys {
		flags
	}

	public TileEntitySprinkler() {
		syncMap.put(Keys.flags, flags);
	}
	
	private void attemptFertilize() {
		if(worldObj == null || worldObj.isRemote) return;
		// there's a 1/100 chance of  attempting to fertilize a crop
		if (worldObj.rand.nextDouble() < 1.0 / 100) {
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
	}
	
	private void sprayParticles() {
		if(worldObj == null || !worldObj.isRemote) return;
		for (int i = 0; i < 6; i++) {
			float offset = (i - 2.5f) / 5f;
			ForgeDirection rotation = getRotation();
			OpenBlocks.proxy.spawnLiquidSpray(worldObj, water, xCoord + 0.5 + (offset * 0.6 * rotation.offsetX), yCoord, zCoord + 0.5 + (offset * 0.6 * rotation.offsetZ), rotation, getSprayPitch(), 2 * offset); 
		}
	}

	public void updateEntity() {
		super.updateEntity();

		if (!worldObj.isRemote) {
			
			if (tank.getLiquid() == null || tank.getLiquid().amount == 0) {
				TileEntity below = worldObj.getBlockTileEntity(xCoord, yCoord - 1,  zCoord);
				if (below instanceof ITankContainer) {
					ITankContainer belowTank = (ITankContainer) below;
					LiquidStack drained = belowTank.drain(ForgeDirection.UP, tank.getCapacity(), false);
					if (drained != null && drained.isLiquidEqual(water)) {
						drained = belowTank.drain(ForgeDirection.UP, tank.getCapacity(), true);
						if (drained != null) {
							tank.fill(drained, true);
						}
					}
				}
			}
			
			// every 60 ticks drain from the tank
			// if there's nothing to drain, disable it
			if (worldObj.getTotalWorldTime() % 60 == 0) {
				flags.set(Flags.enabled, tank.drain(1, true) != null);
			}
			
			
			// if it's enabled..
			
		} 
		// simplified this action because only one of these will execute depending on worldObj.isRemote
		if (flags.get(Flags.enabled)) {
			attemptFertilize();
			sprayParticles();
		}
		syncMap.sync(worldObj, this, xCoord, yCoord, zCoord, 1);
	}

	@Override
	public Packet getDescriptionPacket() {
		return syncMap.getDescriptionPacket(this);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		syncMap.handleTileDataPacket(this, pkt);
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
		if (resource != null && resource.isLiquidEqual(water)) {
			return tank.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		return fill(ForgeDirection.UNKNOWN, resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new ILiquidTank[] { tank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return tank;
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
		// I've no idea if we want to fire a block update here. Will that double the updates for the tick ?
		BlockSprinkler.setMetadataRotation(worldObj, xCoord, yCoord, zCoord, BlockUtils.get2dOrientation(player), true);
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
	}

	public ForgeDirection getRotation() {
		return BlockSprinkler.getMetadataRotation(worldObj, xCoord, yCoord, zCoord, ForgeDirection.NORTH);
	}

	@Override
	public SyncMap getSyncMap() {
		return syncMap;
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {}

	@Override
	public void writeIdentifier(DataOutputStream dos) throws IOException {
		dos.writeInt(xCoord);
		dos.writeInt(yCoord);
		dos.writeInt(zCoord);
	}

	public float getSprayPitch() {
		return (float)(getSprayAngle() * Math.PI);
	}

	public float getSprayAngle() {
		if(flags.get(Flags.enabled)) {
			float angle = (float)(MathHelper.sin(worldObj.getTotalWorldTime() * 0.01f) * Math.PI * 0.1f);
			return (float)(angle);
		}
		return 0;
	}

	@Override
	protected void initialize() {

	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("rotation")) {
			byte ordinal = tag.getByte("rotation");
			BlockSprinkler.setMetadataRotation(worldObj, xCoord, yCoord, zCoord, ForgeDirection.getOrientation(ordinal), true);
		}
	}

}
