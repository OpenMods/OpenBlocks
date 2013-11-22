package openblocks.common.tileentity;

import java.lang.ref.WeakReference;
import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.Config;
import openmods.api.IAwareTile;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableTank;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.ItemUtils;

public class TileEntityTank extends SyncedTileEntity implements
		IFluidHandler, IAwareTile {

	public static int getTankCapacity() {
		return FluidContainerRegistry.BUCKET_VOLUME * Config.bucketsPerTank;
	}

	/**
	 * The tank holding the liquid
	 */
	private SyncableTank tank;

	private double flowTimer = Math.random() * 100;

	private int previousFluidId = 0;

	@Override
	protected void createSyncedFields() {
		tank = new SyncableTank(getTankCapacity());
	}

	public HashMap<ForgeDirection, WeakReference<TileEntityTank>> neighbours = new HashMap<ForgeDirection, WeakReference<TileEntityTank>>();
	public HashMap<ForgeDirection, Boolean> surroundingBlocks = new HashMap<ForgeDirection, Boolean>();

	public static final ForgeDirection[] horizontalDirections = new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST };

	protected Comparator<TileEntityTank> sortBySpace = new Comparator<TileEntityTank>() {
		@Override
		public int compare(TileEntityTank c1, TileEntityTank c2) {
			return c2.getSpace() - c1.getSpace();
		}
	};

	/**
	 * Tell neighbour tanks to update themselves
	 */
	protected void updateNeighbours() {
		TileEntityTank up, down, north, south, east, west;
		up = getTankInDirection(ForgeDirection.UP);
		down = getTankInDirection(ForgeDirection.DOWN);
		north = getTankInDirection(ForgeDirection.NORTH);
		south = getTankInDirection(ForgeDirection.SOUTH);
		east = getTankInDirection(ForgeDirection.EAST);
		west = getTankInDirection(ForgeDirection.WEST);
		if (up != null) up.findNeighbours();
		if (down != null) down.findNeighbours();
		if (north != null) north.findNeighbours();
		if (south != null) south.findNeighbours();
		if (east != null) east.findNeighbours();
		if (west != null) west.findNeighbours();
	}

	/**
	 * Find the neighbouring tanks and store them in a hashmap
	 */
	protected void findNeighbours() {
		neighbours.clear();
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity neighbour = getTileInDirection(direction);
			if (neighbour != null && neighbour instanceof TileEntityTank) {
				neighbours.put(direction, new WeakReference<TileEntityTank>((TileEntityTank)neighbour));
			}
			surroundingBlocks.put(direction, !worldObj.isAirBlock(xCoord
					+ direction.offsetX, yCoord + direction.offsetY, zCoord
					+ direction.offsetZ));
		}
		if (!worldObj.isRemote) {
			sendBlockEvent(0, 0);
		}
	}

	public boolean hasBlockOnSide(ForgeDirection side) {
		return surroundingBlocks.containsKey(side)
				&& surroundingBlocks.get(side);
	}

	public TileEntityTank getTankInDirection(ForgeDirection direction) {
		if (neighbours.containsKey(direction)) {
			WeakReference<TileEntityTank> neighbour = neighbours.get(direction);
			if (neighbour != null) {
				TileEntityTank otherTank = neighbour.get();
				if (otherTank == null) { return null; }
				if (otherTank.isInvalid()) { return null; }
				if (otherTank.canReceiveLiquid(getTank().getFluid())) { return otherTank; }
			}
		}
		return null;
	}

	public TileEntityTank[] getSurroundingTanks() {
		ArrayList<TileEntityTank> tanks = new ArrayList<TileEntityTank>();
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			TileEntityTank t = getTankInDirection(direction);
			if (t != null) {
				tanks.add(t);
			}
		}
		return tanks.toArray(new TileEntityTank[tanks.size()]);
	}

	public ArrayList<TileEntityTank> getHorizontalTanksOrdererdBySpace(HashSet<TileEntityTank> except) {
		ArrayList<TileEntityTank> horizontalTanks = new ArrayList<TileEntityTank>();
		for (ForgeDirection direction : horizontalDirections) {
			TileEntityTank tank = getTankInDirection(direction);
			if (tank != null && !except.contains(tank)) {
				horizontalTanks.add(tank);
			}
		}
		Collections.sort(horizontalTanks, sortBySpace);
		return horizontalTanks;
	}

	/**
	 * Refresh the neighbours because something changed
	 */
	@Override
	public void onNeighbourChanged(int blockId) {
		findNeighbours();
	}

	@Override
	public boolean receiveClientEvent(int eventId, int eventParam) {
		if (worldObj.isRemote) {
			findNeighbours();
		}
		return true;
	}

	public boolean containsValidLiquid() {
		return tank.getFluid() != null;
	}

	@Override
	protected void initialize() {
		findNeighbours();
		updateNeighbours();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {

			HashSet<TileEntityTank> except = new HashSet<TileEntityTank>();
			except.add(this);

			// if we have a liquid
			if (tank.getFluid() != null) {

				// try to fill up the tank below with as much liquid as possible
				TileEntityTank below = getTankInDirection(ForgeDirection.DOWN);
				if (below != null) {
					if (below.getSpace() > 0) {
						FluidStack myLiquid = tank.getFluid().copy();
						if (below.canReceiveLiquid(myLiquid)) {
							int toFill = Math.min(below.getSpace(), myLiquid.amount);
							myLiquid.amount = toFill;
							int filled = below.fill(myLiquid, true, except);
							tank.drain(filled, true);
						}
					}
				}

				if (containsValidLiquid()) {
					// now fill up the horizontal tanks, start with the least
					// full
					ArrayList<TileEntityTank> horizontals = getHorizontalTanksOrdererdBySpace(except);
					for (TileEntityTank horizontal : horizontals) {
						FluidStack liquid = tank.getFluid();
						if (horizontal.canReceiveLiquid(liquid)
								&& liquid != null) {
							int difference = tank.getFluidAmount()
									- horizontal.getTank().getFluidAmount();
							if (difference <= 0) continue;
							int halfDifference = Math.max(difference / 2, 1);
							FluidStack liquidCopy = liquid.copy();
							liquidCopy.amount = Math.min(500, halfDifference);
							int filled = horizontal.fill(liquidCopy, true, except);
							tank.drain(filled, true);
						}
					}
				}
			}
			sync();

		} else {
			flowTimer += 0.1f;
		}
	}

	public boolean canReceiveLiquid(FluidStack liquid) {
		if (tank.getFluid() == null) { return true; }
		if (liquid == null) { return true; }
		FluidStack otherLiquid = tank.getFluid();
		if (otherLiquid != null) { return otherLiquid.isFluidEqual(liquid); }
		return true;
	}

	public IFluidTank getTank() {
		return tank;
	}

	public boolean isFull() {
		return tank.getSpace() == 0;
	}

	public int getSpace() {
		return tank.getSpace();
	}

	public int fill(FluidStack resource, boolean doFill, HashSet<TileEntityTank> except) {
		TileEntityTank below = getTankInDirection(ForgeDirection.DOWN);
		int filled = 0;
		if (except == null) {
			except = new HashSet<TileEntityTank>();
		}
		if (resource == null) { return 0; }

		int startAmount = resource.amount;
		if (except.contains(this)) { return 0; }
		except.add(this);

		resource = resource.copy();

		// fill the tank below as much as possible
		if (below != null && below.getSpace() > 0) {
			filled = below.fill(resource, doFill, except);
			resource.amount -= filled;
		}

		// fill myself up
		if (resource.amount > 0) {
			filled = tank.fill(resource, doFill);
			resource.amount -= filled;
		}

		// ok we cant, so lets fill the tank above
		if (resource.amount > 0) {
			TileEntityTank above = getTankInDirection(ForgeDirection.UP);
			if (above != null) {
				filled = above.fill(resource, doFill, except);
				resource.amount -= filled;
			}
		}

		// finally, distribute any remaining to the sides
		if (resource.amount > 0 && canReceiveLiquid(resource)) {
			ArrayList<TileEntityTank> horizontals = getHorizontalTanksOrdererdBySpace(except);
			if (horizontals.size() > 0) {
				int amountPerSide = resource.amount / horizontals.size();
				for (TileEntityTank sideTank : horizontals) {
					FluidStack copy = resource.copy();
					copy.amount = amountPerSide;
					filled = sideTank.fill(copy, doFill, except);
					resource.amount -= filled;
				}
			}
		}
		return startAmount - resource.amount;
	}

	public FluidStack drain(int amount, boolean doDrain) {
		return tank.drain(amount, doDrain);
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		int newFluidId = tank.getFluid() == null? 0 : tank.getFluid().fluidID;
		if (newFluidId != previousFluidId) {
			worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
		}
		previousFluidId = newFluidId;
	}

	@Override
	public void onBlockBroken() {
		// invalidate();
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("tank")) {
			NBTTagCompound tankTag = stack.getTagCompound().getCompoundTag("tank");
			this.tank.readFromNBT(tankTag);
		}
	}

	public int countDownwardsTanks() {
		int count = 1;
		TileEntityTank below = getTankInDirection(ForgeDirection.DOWN);
		if (below != null) {
			count += below.countDownwardsTanks();
		}
		return count;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {

		ForgeDirection direction = ForgeDirection.getOrientation(side);

		ItemStack current = player.inventory.getCurrentItem();
		if (current != null) {

			FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(current);

			// Handle filled containers
			if (liquid != null) {
				int qty = fill(direction, liquid, true);
				if (qty != 0 && !player.capabilities.isCreativeMode) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemUtils.consumeItem(current));
				}
				return true;
			}
			FluidStack available = tank.getFluid();
			if (worldObj.isRemote && getTank().getFluidAmount() > 0) { return true; }
			if (available != null) {
				ItemStack filled = FluidContainerRegistry.fillFluidContainer(available, current);
				liquid = FluidContainerRegistry.getFluidForFilledItem(filled);
				if (liquid != null) {
					if (!player.capabilities.isCreativeMode) {
						if (current.stackSize > 1) {
							if (!player.inventory.addItemStackToInventory(filled)) return false;
							player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemUtils.consumeItem(current));
						} else {
							player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemUtils.consumeItem(current));
							player.inventory.setInventorySlotContents(player.inventory.currentItem, filled);
						}
					}
					drain(ForgeDirection.UNKNOWN, liquid.amount, true);
					return true;
				}
			}
		}
		return false;
	}

	public double getHeightForRender() {
		return (double)tank.getFluidAmount() / (double)tank.getCapacity();
	}

	public double getPercentFull() {
		return tank.getPercentFull();
	}

	public double getFlowOffset() {
		return Math.sin(flowTimer) / 35;
	}

	public double getLiquidHeightForSide(ForgeDirection... sides) {
		if (containsValidLiquid()) {
			double percentFull = getHeightForRender();

			if (percentFull > 0.98) { return 1.0; }
			double fullness = percentFull + getFlowOffset();
			int count = 1;
			for (ForgeDirection side : sides) {
				TileEntityTank sideTank = getTankInDirection(side);
				if (sideTank != null
						&& sideTank.canReceiveLiquid(tank.getFluid())) {
					fullness += sideTank.getHeightForRender()
							+ sideTank.getFlowOffset();
					count++;
				}
			}
			return Math.max(0, Math.min(1, fullness / count));
		}
		return 0D; /* No D for you ;) */
	}

	public NBTTagCompound getItemNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		tank.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void onBlockAdded() {}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return this.fill(resource, doFill, null);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return tank.drain(resource, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return true;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return drain(maxDrain, doDrain);
	}

	public int getFluidLightLevel() {
		FluidStack fluid = tank.getFluid();
		if (fluid != null) {
			try {
				return fluid.getFluid().getLuminosity();
			} catch (Exception e) {}
		}
		return 0;
	}
}
