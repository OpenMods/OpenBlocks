package openblocks.common.tileentity.tank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import openblocks.OpenBlocks;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;
import openblocks.sync.SyncableShort;
import openblocks.utils.BlockUtils;
import openblocks.utils.ItemUtils;

public class TileEntityTank extends TileEntityTankBase implements
		ITankContainer {

	public static int getTankCapacity() {
		return LiquidContainerRegistry.BUCKET_VOLUME
				* OpenBlocks.Config.bucketsPerTank;
	}

	/**
	 * The tank holding the liquid
	 */
	private LiquidTank tank = new LiquidTank(getTankCapacity());

	/**
	 * The Id of the liquid in the tank
	 */
	private SyncableInt liquidId = new SyncableInt();

	/**
	 * The meta of the liquid metadata in the tank
	 */
	private SyncableInt liquidMeta = new SyncableInt();

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
		liquidId, liquidMeta, renderLevel
	}

	public TileEntityTank() {
		syncMap.put(Keys.liquidId, liquidId);
		syncMap.put(Keys.liquidMeta, liquidMeta);
		syncMap.put(Keys.renderLevel, liquidRenderAmount);
	}

	public boolean containsValidLiquid() {
		return liquidId.getValue() != 0 && tank.getLiquidName() != null;
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

	public void updateEntity() {
		super.updateEntity();

		if (!worldObj.isRemote) {

			HashSet<TileEntityTank> except = new HashSet<TileEntityTank>();
			except.add(this);

			// if we have a liquid
			if (tank.getLiquid() != null) {

				// try to fill up the tank below with as much liquid as possible
				TileEntityTank below = getTankInDirection(ForgeDirection.DOWN);
				if (below != null) {
					if (below.getSpace() > 0) {
						LiquidStack myLiquid = tank.getLiquid().copy();
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
						LiquidStack liquid = tank.getLiquid();
						if (horizontal.canReceiveLiquid(liquid)
								&& liquid != null) {
							int difference = getAmount()
									- horizontal.getAmount();
							if (difference <= 0) continue;
							int halfDifference = Math.max(difference / 2, 1);
							LiquidStack liquidCopy = liquid.copy();
							liquidCopy.amount = Math.min(500, halfDifference);
							int filled = horizontal.fill(liquidCopy, true, except);
							tank.drain(filled, true);
						}
					}
				}

				if (tank.getLiquid() != null) {
					// set the sync values for this liquid
					liquidId.setValue(tank.getLiquid().itemID);
					liquidMeta.setValue(tank.getLiquid().itemMeta);
				}
			}

			// calculate render height
			if (containsValidLiquid()) {
				/* ratio the liquid amount in to the entire short, clamp it */
				short newLiquidRender = (short)Math.max(0, Math.min(Short.MAX_VALUE, Short.MAX_VALUE
						* tank.getLiquid().amount / (float)tank.getCapacity()));
				liquidRenderAmount.setValue(newLiquidRender);
			} else {
				liquidRenderAmount.setValue((short)0);
				liquidId.setValue(0);
				liquidMeta.setValue(0);
			}
			/* Update EVERY tick */
			syncMap.sync(worldObj, this, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 1);
		} else {
			interpolateLiquidLevel();
			flowTimer += 0.1f;
		}
	}

	public boolean canReceiveLiquid(LiquidStack liquid) {
		if (!tank.containsValidLiquid()) { return true; }
		if (liquid == null) { return true; }
		LiquidStack otherLiquid = tank.getLiquid();
		if (otherLiquid != null) { return otherLiquid.isLiquidEqual(liquid); }
		return true;
	}

	public LiquidTank getInternalTank() {
		return tank;
	}

	public int getSpace() {
		return getInternalTank().getCapacity() - getAmount();
	}

	public boolean isFull() {
		return getAmount() == getInternalTank().getCapacity();
	}

	public int getAmount() {
		if (getInternalTank() == null || getInternalTank().getLiquid() == null) return 0;
		return getInternalTank().getLiquid().amount;
	}

	public int fill(LiquidStack resource, boolean doFill, HashSet<TileEntityTank> except) {
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
					LiquidStack copy = resource.copy();
					copy.amount = amountPerSide;
					filled = sideTank.fill(copy, doFill, except);
					resource.amount -= filled;
				}
			}
		}
		return startAmount - resource.amount;
	}

	/**
	 * TODO
	 */
	public LiquidStack drain(int amount, boolean doDrain) {
		return tank.drain(amount, doDrain);
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		// Mikee, we don't need to create the liquid client side cause we don't
		// care, right? :D
		if (changes.contains(liquidId) || changes.contains(liquidMeta)) {
			if (liquidId.getValue() == 0) {
				tank.setLiquid(null);
			} else {
				tank.setLiquid(new LiquidStack(liquidId.getValue(), 1, liquidMeta.getValue()));
			}
		}
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
		interpolatedRenderAmount = (short)((double)getAmount()
				/ (double)tank.getCapacity() * Short.MAX_VALUE);
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		int filled = fill(resource, doFill, null);
		if (doFill && filled > 0) {
			if (resource != null) {
				liquidId.setValue(resource.itemID);
				liquidMeta.setValue(resource.itemMeta);
			}
		}
		return filled;
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		return fill(ForgeDirection.UNKNOWN, resource, doFill);
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return drain(maxDrain, doDrain);
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		return drain(maxDrain, doDrain);
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		return new ILiquidTank[] { tank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		return tank;
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
	public Packet getDescriptionPacket() {
		return syncMap.getDescriptionPacket(this);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		syncMap.handleTileDataPacket(this, pkt);
		interpolatedRenderAmount = liquidRenderAmount.getValue();
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {

		/* TODO: Fix some bugs here with client/server side stuff */
		ForgeDirection direction = BlockUtils.sideToDirection(side);

		ItemStack current = player.inventory.getCurrentItem();
		if (current != null) {

			LiquidStack liquid = LiquidContainerRegistry.getLiquidForFilledItem(current);

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
				LiquidStack available = tank.getLiquid();
				if (available != null) {
					ItemStack filled = LiquidContainerRegistry.fillLiquidContainer(available, current);

					liquid = LiquidContainerRegistry.getLiquidForFilledItem(filled);

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
				if (sideTank != null
						&& sideTank.canReceiveLiquid(tank.getLiquid())) {
					fullness += sideTank.getHeightForRender()
							+ sideTank.getFlowOffset();
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

	public void setClientLiquidMeta(int itemMeta) {
		liquidMeta.setValue(itemMeta);
	}

	public NBTTagCompound getItemNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		tank.writeToNBT(nbt);
		return nbt;
	}
}
