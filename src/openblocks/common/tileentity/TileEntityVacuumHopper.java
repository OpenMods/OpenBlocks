package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.GenericInventory;
import openblocks.common.api.IAwareTile;
import openblocks.integration.ModuleBuildCraft;
import openblocks.utils.InventoryUtils;
import cpw.mods.fml.common.Loader;

public class TileEntityVacuumHopper extends OpenTileEntity implements IInventory, IAwareTile {

	private GenericInventory inventory = new GenericInventory("vacuumhopper", true, 10);

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (worldObj.isRemote) {
			worldObj.spawnParticle("portal", xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, worldObj.rand.nextDouble() - 0.5, worldObj.rand.nextDouble() - 1.0, worldObj.rand.nextDouble() - 0.5);
		}

		@SuppressWarnings("unchecked")
		List<EntityItem> surroundingItems = worldObj.getEntitiesWithinAABB(EntityItem.class, getBB().expand(3, 3, 3));
		
		for(EntityItem item : surroundingItems) {
			
			if (!item.isDead) {
				
				ItemStack stack = item.getEntityItem();
				if (InventoryUtils.testInventoryInsertion(this, stack) > 0) {
					
					double x = (xCoord + 0.5D - item.posX) / 15.0D;
					double y = (yCoord + 0.5D - item.posY) / 15.0D;
					double z = (zCoord + 0.5D - item.posZ) / 15.0D;
	
					double distance = Math.sqrt(x * x + y * y + z * z);
					double var11 = 1.0D - distance;
	
					if (var11 > 0.0D) {
						var11 *= var11;
						item.motionX += x / distance * var11 * 0.05;
						item.motionY += y / distance * var11 * 0.2;
						item.motionZ += z / distance * var11 * 0.05;
					}
				}
			}
		}

		if (!worldObj.isRemote) {
			if (worldObj.getTotalWorldTime() % 10 == 0) {

				TileEntity tileOnSurface = getTileInDirection(getSurface());

				int slotId = InventoryUtils.getSlotIndexOfNextStack(this);
				if (slotId > -1) {
					ItemStack nextStack = getStackInSlot(slotId);
					int previousSize = nextStack.stackSize;
					nextStack = nextStack.copy();
					if (tileOnSurface instanceof IInventory) {
						InventoryUtils.insertItemIntoInventory((IInventory) tileOnSurface, nextStack);
					}else {
						if (Loader.isModLoaded(openblocks.Mods.BUILDCRAFT)) {
							int inserted = ModuleBuildCraft.tryAcceptIntoPipe(tileOnSurface, nextStack, getSurface());
							nextStack.stackSize -= inserted;
						}
					}
					if (nextStack != null) {
						if (nextStack.stackSize > 0) {
							setInventorySlotContents(slotId, nextStack);
						} else {
							setInventorySlotContents(slotId, null);
						}
						if (nextStack.stackSize < previousSize) {
							worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
						}
					}
				}
			}
		}
	}

	public ForgeDirection getSurface() {
		if (getFlag1()) {
			return ForgeDirection.DOWN;
		}else if (getFlag2()) {
			return ForgeDirection.UP;
		}else {
			return getRotation();
		}
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory.getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return inventory.isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return inventory.isItemValidForSlot(i, itemstack);
	}

	@Override
	public void onBlockBroken() {}

	@Override
	public void onBlockAdded() {}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player, OpenBlocks.Gui.VacuumHopper);
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		ForgeDirection surface = side.getOpposite();
		setRotation(side.getOpposite());
		setFlag1(surface == ForgeDirection.DOWN);
		setFlag2(surface == ForgeDirection.UP);
		sync();
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		return false;
	}

	public void onEntityCollidedWithBlock(Entity entity) {
		if (!worldObj.isRemote && entity instanceof EntityItem) {
			EntityItem item = (EntityItem) entity;
			ItemStack stack = item.getEntityItem().copy();
			InventoryUtils.insertItemIntoInventory(inventory, stack);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			if (stack.stackSize == 0) {
				item.setDead();
			}else {
				item.setEntityItemStack(stack);
			}
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		Packet132TileEntityData packet = new Packet132TileEntityData();
		packet.actionType = 0;
		packet.xPosition = xCoord;
		packet.yPosition = yCoord;
		packet.zPosition = zCoord;
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		packet.data = nbt;
		return packet;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

}
