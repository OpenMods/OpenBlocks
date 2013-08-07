package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import openblocks.utils.ByteUtils;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidEvent;
import net.minecraftforge.liquids.LiquidStack;

public class SyncableTank implements ISyncableObject, ILiquidTank {

	private LiquidStack liquid;
	private LiquidStack previousLiquid;
	private int previousCapacity;
	private int capacity;
	private int tankPressure;
	private boolean hasChanged;
	private int ticksSinceChanged = 0;
	private short changeMask = 0;
	
	public enum Flags {
		capacity,
		amount,
		liquid
	}

	public SyncableTank(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public LiquidStack getLiquid() {
		return liquid;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}
	
	public int getAmount() {
		return liquid != null ? liquid.amount : 0;
	}
	
	public double getPercentFull() {
		return (double) getAmount() / (double) getCapacity();
	}

	@Override
	public int fill(LiquidStack resource, boolean doFill) {
		if (resource == null || resource.itemID <= 0) return 0;

		if (liquid == null || liquid.itemID <= 0) {
			if (resource.amount <= capacity) {
				if (doFill) {
					this.liquid = resource.copy();
					changeMask = ByteUtils.set(changeMask, Flags.liquid, true);
					changeMask = ByteUtils.set(changeMask, Flags.amount, true);
				}
				return resource.amount;
			} else {
				if (doFill) {
					this.liquid = resource.copy();
					this.liquid.amount = capacity;
					changeMask = ByteUtils.set(changeMask, Flags.liquid, true);
					changeMask = ByteUtils.set(changeMask, Flags.amount, true);
				}
				return capacity;
			}
		}
		if (!liquid.isLiquidEqual(resource)) return 0;

		int space = capacity - liquid.amount;
		if (resource.amount <= space) {
			if (doFill) {
				this.liquid.amount += resource.amount;
				changeMask = ByteUtils.set(changeMask, Flags.amount, true);
			}
			return resource.amount;
		} else {
			if (doFill) {
				this.liquid.amount = capacity;
				changeMask = ByteUtils.set(changeMask, Flags.amount, true);
			}
			return space;
		}
	}

	@Override
	public LiquidStack drain(int maxDrain, boolean doDrain) {
		if (liquid == null || liquid.itemID <= 0) return null;
		if (liquid.amount <= 0) return null;

		int used = maxDrain;
		if (liquid.amount < used) used = liquid.amount;

		if (doDrain) {
			liquid.amount -= used;
			setHasChanged();
			changeMask = ByteUtils.set(changeMask, Flags.amount, true);
		}

		LiquidStack drained = new LiquidStack(liquid.itemID, used, liquid.itemMeta);

		// Reset liquid if emptied
		if (liquid.amount <= 0) {
			liquid = null;
			changeMask = ByteUtils.set(changeMask, Flags.liquid, true);
		}

		return drained;
	}

	@Override
	public int getTankPressure() {
		return tankPressure;
	}

	public void setTankPressure(int pressure) {
		this.tankPressure = pressure;
	}

	public boolean hasChanged() {
		return previousLiquid != liquid || previousCapacity != capacity;
	}

	public void resetChangeStatus() {
		if (liquid != null) {
			previousLiquid = liquid.copy();
		}else {
			liquid = null;
		}
		previousCapacity = capacity;
		ticksSinceChanged++;
		changeMask = 0;
	}

	@Override
	public void setHasChanged() {
		hasChanged = true;
		ticksSinceChanged = 0;
	}

	public boolean containsValidLiquid() {
		return LiquidDictionary.findLiquidName(liquid) != null;
	}

	public void setLiquid(LiquidStack liquid) {
		this.liquid = liquid;
		changeMask = ByteUtils.set(changeMask, Flags.liquid, true);
		changeMask = ByteUtils.set(changeMask, Flags.amount, true);
	}

	public void setCapacity(int capacity) {
		if (capacity != this.capacity) {
			this.capacity = capacity;
			changeMask = ByteUtils.set(changeMask, Flags.capacity, true);
		}
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		short changeMask = stream.readShort();
		if (ByteUtils.get(changeMask, Flags.liquid)) {
			short liquidId = stream.readShort();
			short liquidMeta = stream.readShort();
			if (liquidId == 0 && liquidMeta == 0) {
				liquid = null;
			}else {
				if (liquid == null || (liquid.itemID != liquidId || liquid.itemMeta != liquidMeta)) {
					liquid = new LiquidStack(liquidId, 0, liquidMeta);
				}
			}
		}
		if (ByteUtils.get(changeMask, Flags.amount)) {
			int amount = stream.readInt();
			if (liquid != null) {
				liquid.amount = amount;
			}
			if (amount == 0) {
				liquid = null;
			}
		}
		if (ByteUtils.get(changeMask, Flags.capacity)) {
			capacity = stream.readInt();
		}
	}

	@Override
	public void writeToStream(DataOutputStream stream, boolean fullData) throws IOException {
		short sendMask = fullData ? Short.MAX_VALUE : changeMask;
		stream.writeShort(sendMask);
		if (ByteUtils.get(sendMask, Flags.liquid)) {
			stream.writeShort(liquid == null ? 0 : liquid.itemID);
			stream.writeShort(liquid == null ? 0 : liquid.itemMeta);
		}
		if (ByteUtils.get(sendMask, Flags.amount)) {
			stream.writeInt(liquid == null ? 0 : liquid.amount);
		}
		if (ByteUtils.get(sendMask, Flags.capacity)) {
			stream.writeInt(capacity);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		NBTTagCompound tankTag = new NBTTagCompound();
		if (containsValidLiquid()) {
			liquid.writeToNBT(tankTag);
		}
		tag.setCompoundTag(name, tankTag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		if (tag.hasKey(name)) {
			NBTTagCompound nbt = tag.getCompoundTag(name);
			LiquidStack liquid = LiquidStack.loadLiquidStackFromNBT(nbt);
			if (liquid != null) {
				this.liquid = liquid;
			}
		}
	}

	public int getSpace() {
		return getCapacity() - getAmount();
	}

}
