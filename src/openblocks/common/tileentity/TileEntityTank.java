package openblocks.common.tileentity;

import java.lang.ref.WeakReference;
import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.api.IAwareTile;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;
import openblocks.sync.SyncableShort;
import openblocks.utils.ItemUtils;

public class TileEntityTank extends NetworkedTileEntity implements IFluidHandler, IAwareTile {

	public static int getTankCapacity() {
		return FluidContainerRegistry.BUCKET_VOLUME * Config.bucketsPerTank;
	}

	/**
	 * The tank holding the liquid
	 */
	private FluidTank tank = new FluidTank(getTankCapacity());

	/**
	 * The Id of the liquid in the tank
	 */
	private SyncableInt liquidId = new SyncableInt();

	/**
	 * The level of the liquid that is rendered on the client
	 */
	private SyncableShort liquidRenderAmount = new SyncableShort();

	/**
	 * The amount that will be rendered by the client, interpolated towards
	 * liquidRenderAmount each tick
	 */
	private short interpolatedRenderAmount = 0;

	/**
	 * How quickly the interpolatedRenderAmount approaches liquidRenderAmount
	 */
	private static final short adjustRate = 1000;

	private double flowTimer = Math.random() * 100;

	/**
	 * Keys of things what get synced
	 */
	public enum Keys {
		liquidId,
		renderLevel
	}

	public TileEntityTank() {
		addSyncedObject(Keys.liquidId, liquidId);
		addSyncedObject(Keys.renderLevel, liquidRenderAmount);
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
			surroundingBlocks.put(direction, !worldObj.isAirBlock(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ));
		}
		if (!worldObj.isRemote) {
			sendBlockEvent(0, 0);
		}
	}

	public boolean hasBlockOnSide(ForgeDirection side) {
		return surroundingBlocks.containsKey(side) && surroundingBlocks.get(side);
	}

	public TileEntityTank getTankInDirection(ForgeDirection direction) {
		if (neighbours.containsKey(direction)) {
			WeakReference<TileEntityTank> neighbour = neighbours.get(direction);
			if (neighbour != null) {
				TileEntityTank otherTank = neighbour.get();
				if (otherTank == null) { return null; }
				if (otherTank.isInvalid()) { return null; }
				if (otherTank.canReceiveLiquid(getInternalTank().getFluid())) { return otherTank; }
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
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		if (worldObj.isRemote) {
			findNeighbours();
		}
		return true;
	}

	public boolean containsValidLiquid() {
		return liquidId.getValue() != 0 && tank.getFluid() != null;
	}

	private void interpolateLiquidLevel() {
		/* Client interpolates render amount */
		if (!worldObj.isRemote) return;
		if (interpolatedRenderAmount + adjustRate < liquidRenderAmount.getValue()) {
			interpolatedRenderAmount += adjustRate;
		} else if (interpolatedRenderAmount - adjustRate > liquidRenderAmount.getValue()) {
			interpolatedRenderAmount -= adjustRate;
		} else {
			interpolatedRenderAmount = liquidRenderAmount.getValue();
		}
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

				if (getAmount() > 0 && containsValidLiquid()) {
					// now fill up the horizontal tanks, start with the least
					// full
					ArrayList<TileEntityTank> horizontals = getHorizontalTanksOrdererdBySpace(except);
					for (TileEntityTank horizontal : horizontals) {
						FluidStack liquid = tank.getFluid();
						if (horizontal.canReceiveLiquid(liquid) && liquid != null) {
							int difference = getAmount() - horizontal.getAmount();
							if (difference <= 0) continue;
							int halfDifference = Math.max(difference / 2, 1);
							FluidStack liquidCopy = liquid.copy();
							liquidCopy.amount = Math.min(500, halfDifference);
							int filled = horizontal.fill(liquidCopy, true, except);
							tank.drain(filled, true);
						}
					}
				}

				if (tank.getFluid() != null) {
					// set the sync values for this liquid
					liquidId.setValue(tank.getFluid().fluidID);
					// liquidMeta.setValue(tank.getFluid().areFluidStackTagsEqual(stack1,
					// stack2));
				}
			}

			// calculate render height
			if (containsValidLiquid()) {
				/* ratio the liquid amount in to the entire short, clamp it */
				short newLiquidRender = (short)Math.max(0, Math.min(Short.MAX_VALUE, Short.MAX_VALUE * tank.getFluid().amount / (float)tank.getCapacity()));
				liquidRenderAmount.setValue(newLiquidRender);
			} else {
				liquidRenderAmount.setValue((short)0);
				liquidId.setValue(0);
			}
			sync(false);
		} else {
			interpolateLiquidLevel();
			flowTimer += 0.1f;
			if (OpenBlocks.proxy.getTicks(worldObj) % 20 == 0) {
				worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	public boolean canReceiveLiquid(FluidStack liquid) {
		if (tank.getFluid() == null) { return true; }
		if (liquid == null) { return true; }
		FluidStack otherLiquid = tank.getFluid();
		if (otherLiquid != null) { return otherLiquid.isFluidEqual(liquid); }
		return true;
	}

	public FluidTank getInternalTank() {
		return tank;
	}

	public int getSpace() {
		return getInternalTank().getCapacity() - getAmount();
	}

	public boolean isFull() {
		return getAmount() == getInternalTank().getCapacity();
	}

	public int getAmount() {
		if (getInternalTank() == null || getInternalTank().getFluid() == null) return 0;
		return getInternalTank().getFluid().amount;
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
	public void onSynced(List<ISyncableObject> changes) {
		interpolatedRenderAmount = liquidRenderAmount.getValue();
		if (changes.contains(liquidId)) {
			if (liquidId.getValue() == 0) {
				tank.setFluid(null);
			} else {
				tank.setFluid(new FluidStack(liquidId.getValue(), 1));
			}
		}
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
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

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag);
		interpolatedRenderAmount = (short)((double)getAmount() / (double)tank.getCapacity() * Short.MAX_VALUE);
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
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		super.onDataPacket(net, pkt);
		interpolatedRenderAmount = liquidRenderAmount.getValue();
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
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
			} else {
				// Fix for #15
				if (worldObj.isRemote && liquidRenderAmount.getValue() > 0) return true;
				// End of fix
				FluidStack available = tank.getFluid();
				if (available != null) {
					ItemStack filled = FluidContainerRegistry.fillFluidContainer(available, current);

					liquid = FluidContainerRegistry.getFluidForFilledItem(filled);

					if (liquid != null) {
						if (!player.capabilities.isCreativeMode) {
							if (current.stackSize > 1) {
								if (!player.inventory.addItemStackToInventory(filled)) return false;
								else {
									player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemUtils.consumeItem(current));
								}
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
		}

		return false;
	}

	public double getHeightForRender() {
		double percent = getPercentFull();
		if (worldObj == null || worldObj.isRemote) { return Math.max(percent > 0.001? 0.1 : 0, percent); }
		return percent;
	}

	public double getPercentFull() {
		if (containsValidLiquid()) {
			if (worldObj == null || worldObj.isRemote) {
				return interpolatedRenderAmount / (double)Short.MAX_VALUE;
			} else {
				return liquidRenderAmount.getValue() / (double)Short.MAX_VALUE;
			}
		} else {
			return 0D; /* No D for you ;) */
		}
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
				if (sideTank != null && sideTank.canReceiveLiquid(tank.getFluid())) {
					fullness += sideTank.getHeightForRender() + sideTank.getFlowOffset();
					count++;
				}
			}
			return Math.max(0, Math.min(1, fullness / count));
		} else {
			return 0D; /* No D for you ;) */
		}
	}

	public void setClientLiquidId(int itemID) {
		liquidId.setValue(itemID);
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
		int filled = fill(resource, doFill, null);
		if (doFill && filled > 0) {
			if (resource != null) {
				liquidId.setValue(resource.fluidID);
			}
		}
		return filled;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource == null) return null;
		if (!resource.isFluidEqual(tank.getFluid())) return null;
		return drain(from, resource.amount, doDrain);
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
		FluidTank internalTank = getInternalTank();
		if (internalTank != null && internalTank.getFluid() != null) {
			try {
				return internalTank.getFluid().getFluid().getLuminosity();
			} catch (Exception e) {}
		}
		return 0;
	}
}
