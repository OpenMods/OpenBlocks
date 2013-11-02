package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.OpenBlocks;
import openblocks.common.GenericInventory;
import openblocks.common.api.IAwareTile;
import openblocks.integration.ModuleBuildCraft;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableBoolean;
import openblocks.sync.SyncableFlags;
import openblocks.sync.SyncableInt;
import openblocks.utils.CollectionUtils;
import openblocks.utils.EnchantmentUtils;
import openblocks.utils.InventoryUtils;
import cpw.mods.fml.common.Loader;

public class TileEntityVacuumHopper extends NetworkedTileEntity implements
		IInventory, IFluidHandler, IAwareTile {

	private GenericInventory inventory = new GenericInventory("vacuumhopper", true, 10);

	private FluidTank tank = new FluidTank(EnchantmentUtils.XPToLiquidRatio(EnchantmentUtils.getExperienceForLevel(5)));

	private int oneLevel = EnchantmentUtils.XPToLiquidRatio(EnchantmentUtils.getExperienceForLevel(1));

	public enum Keys {
		xpOutputs,
		itemOutputs,
		tankLevel,
		vacuumDisabled
	}

	public SyncableFlags xpOutputs = new SyncableFlags();
	public SyncableFlags itemOutputs = new SyncableFlags();
	public SyncableInt tankLevel = new SyncableInt();
	public SyncableBoolean vacuumDisabled = new SyncableBoolean();
	
	public TileEntityVacuumHopper() {
		addSyncedObject(Keys.xpOutputs, xpOutputs);
		addSyncedObject(Keys.itemOutputs, itemOutputs);
		addSyncedObject(Keys.tankLevel, tankLevel);
		addSyncedObject(Keys.vacuumDisabled, vacuumDisabled);
	}

	public SyncableFlags getXPOutputs() {
		return xpOutputs;
	}

	public SyncableFlags getItemOutputs() {
		return itemOutputs;
	}

	public FluidTank getTank() {
		return tank;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if(!vacuumDisabled.getValue()) {
			if (worldObj.isRemote) {
				worldObj.spawnParticle("portal", xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, worldObj.rand.nextDouble() - 0.5, worldObj.rand.nextDouble() - 1.0, worldObj.rand.nextDouble() - 0.5);
			}
			
			@SuppressWarnings("unchecked")
			List<Entity> surroundingItems = worldObj.getEntitiesWithinAABB(Entity.class, getBB().expand(3, 3, 3));
	
			for (Entity entity : surroundingItems) {
	
				if (!entity.isDead
						&& (entity instanceof EntityItem || entity instanceof EntityXPOrb)) {
	
					boolean shouldPull = true;
	
					if (entity instanceof EntityItem) {
						ItemStack stack = ((EntityItem)entity).getEntityItem();
						shouldPull = InventoryUtils.testInventoryInsertion(this, stack) > 0;
					}
					if (entity instanceof EntityXPOrb) {
						shouldPull = this.getXPOutputs().getActiveSlots().size() > 0;
					}
	
					if (shouldPull) {
	
						double x = (xCoord + 0.5D - entity.posX) / 15.0D;
						double y = (yCoord + 0.5D - entity.posY) / 15.0D;
						double z = (zCoord + 0.5D - entity.posZ) / 15.0D;
	
						double distance = Math.sqrt(x * x + y * y + z * z);
						double var11 = 1.0D - distance;
	
						if (var11 > 0.0D) {
							var11 *= var11;
							entity.motionX += x / distance * var11 * 0.05;
							entity.motionY += y / distance * var11 * 0.2;
							entity.motionZ += z / distance * var11 * 0.05;
						}
					}
				}
			}
		}

		if (!worldObj.isRemote) {

			if (OpenBlocks.proxy.getTicks(worldObj) % 10 == 0) {

				Integer slotDirection = CollectionUtils.getRandom(xpOutputs.getActiveSlots());
				ForgeDirection directionToOutputXP = null;
				if (slotDirection != null) {
					directionToOutputXP = ForgeDirection.getOrientation(slotDirection);
				}
				TileEntity tileOnSurface = null;

				if (directionToOutputXP != null) {
					tileOnSurface = getTileInDirection(directionToOutputXP);

					IFluidHandler fluidHandler = null;
					if (tileOnSurface instanceof IFluidHandler) {
						fluidHandler = (IFluidHandler)tileOnSurface;
					}

					// if we've got liquid in the tank
					if (tank.getFluidAmount() > 0) {
						// drain a bit
						FluidStack drainedFluid = tank.drain(Math.min(tank.getFluidAmount(), oneLevel), true);
						if (drainedFluid != null) {
							// copy what we drained
							FluidStack clonedFluid = drainedFluid.copy();
							// try to insert it into a tank or pipe
							if (fluidHandler != null) {
								clonedFluid.amount -= fluidHandler.fill(directionToOutputXP.getOpposite(), drainedFluid, true);
							} else if (Loader.isModLoaded(openblocks.Mods.BUILDCRAFT)) {
								clonedFluid.amount -= ModuleBuildCraft.tryAcceptIntoPipe(tileOnSurface, drainedFluid, directionToOutputXP);
							}
							// fill any remainder
							if (clonedFluid.amount > 0) {
								tank.fill(clonedFluid, true);
							}
						}
					}
				}

				int firstUsedSlot = -1;
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					if (inventory.getStackInSlot(i) != null) {
						firstUsedSlot = i;
					}
				}
				
				if (firstUsedSlot > -1) {
					for (Integer dir : getShuffledItemSlots()) {
						ForgeDirection directionToOutputItem = ForgeDirection.getOrientation(dir);
						tileOnSurface = getTileInDirection(directionToOutputItem);
						if (InventoryUtils.moveItemInto(this, firstUsedSlot, tileOnSurface, 64, directionToOutputItem, true) > 0) {
							worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
							break;
						}
					}
				}
			}
		}
	}
	
	public List<Integer> getShuffledItemSlots() {
		List<Integer> slots = new ArrayList<Integer>();
		slots.addAll(getItemOutputs().getActiveSlots());
		Collections.shuffle(slots);
		return slots;
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
		if (player.isSneaking()) {
			if(player.inventory.getStackInSlot(player.inventory.currentItem) == null) {
				vacuumDisabled.negate();
				return true;
			}
			return false;
		} else if (!worldObj.isRemote) {
			openGui(player, OpenBlocks.Gui.vacuumHopper);
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {

	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		return false;
	}

	public void onEntityCollidedWithBlock(Entity entity) {
		if (!worldObj.isRemote) {
			if (entity instanceof EntityItem) {
				EntityItem item = (EntityItem)entity;
				ItemStack stack = item.getEntityItem().copy();
				InventoryUtils.insertItemIntoInventory(inventory, stack);
				if (stack.stackSize == 0) {
					item.setDead();
				} else {
					item.setEntityItemStack(stack);
				}
			} else if (entity instanceof EntityXPOrb) {
				if (getXPOutputs().getActiveSlots().size() > 0) {
					FluidStack newFluid = new FluidStack(OpenBlocks.Fluids.XPJuice, EnchantmentUtils.XPToLiquidRatio(((EntityXPOrb)entity).getXpValue()));
					tank.fill(newFluid, true);
					entity.setDead();
				}
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
		tank.writeToNBT(tag);
		xpOutputs.writeToNBT(tag, "xpoutputs");
		itemOutputs.writeToNBT(tag, "itemoutputs");
		vacuumDisabled.writeToNBT(tag, "vacuumDisabled");
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
		tank.readFromNBT(tag);
		xpOutputs.readFromNBT(tag, "xpoutputs");
		itemOutputs.readFromNBT(tag, "itemoutputs");
		vacuumDisabled.readFromNBT(tag, "vacuumDisabled");
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource == null) return null;
		if (!resource.isFluidEqual(tank.getFluid())) return null;
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	public double getXPBufferRatio() {
		return Math.max(0, Math.min(1, (double)tankLevel.getValue() / (double)tank.getCapacity()));
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {}

	public void updateGuiValues() {
		tankLevel.setValue(tank.getFluidAmount());
	}

}
