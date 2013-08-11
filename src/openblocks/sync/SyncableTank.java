package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

public class SyncableTank implements ISyncableObject, ILiquidTank {

	private LiquidStack liquid;
	private int capacity;
	private int tankPressure;
	private boolean hasChanged;
	private int ticksSinceChanged = 0;
	
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
	
	public void clear() {
		liquid = null;
	}

	@Override
	public int fill(LiquidStack resource, boolean doFill) {
		if (resource == null || resource.itemID <= 0) return 0;

		if (liquid == null || liquid.itemID <= 0) {
			if (resource.amount <= capacity) {
				if (doFill) {
					this.liquid = resource.copy();
					setHasChanged();
				}
				return resource.amount;
			} else {
				if (doFill) {
					this.liquid = resource.copy();
					this.liquid.amount = capacity;
					setHasChanged();
				}
				return capacity;
			}
		}
		if (!liquid.isLiquidEqual(resource)) return 0;

		int space = capacity - liquid.amount;
		if (resource.amount <= space) {
			if (doFill) {
				this.liquid.amount += resource.amount;
				setHasChanged();
			}
			return resource.amount;
		} else {
			if (doFill) {
				this.liquid.amount = capacity;
				setHasChanged();
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
		}

		LiquidStack drained = new LiquidStack(liquid.itemID, used, liquid.itemMeta);

		// Reset liquid if emptied
		if (liquid.amount <= 0) {
			liquid = null;
			setHasChanged();
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
		return hasChanged;
	}

	public void resetChangeStatus() {
		ticksSinceChanged++;
		hasChanged = false;
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
		setHasChanged();
	}

	public void setCapacity(int capacity) {
		if (capacity != this.capacity) {
			this.capacity = capacity;
			setHasChanged();
		}
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		int liquidId = stream.readInt();
		int liquidMeta = stream.readInt();
		if (liquidId == 0 && liquidMeta == 0) {
			liquid = null;
		}else {
			liquid = new LiquidStack(liquidId, 1, liquidMeta);
		}
		int amount = stream.readInt();
		if (liquid != null) {
			liquid.amount = amount;
		}
		if (amount == 0) {
			liquid = null;
		}
		capacity = stream.readInt();
	}

	@Override
	public void writeToStream(DataOutputStream stream, boolean fullData) throws IOException {
		stream.writeInt(liquid == null ? 0 : liquid.itemID);
		stream.writeInt(liquid == null ? 0 : liquid.itemMeta);
		stream.writeInt(liquid == null ? 0 : liquid.amount);
		stream.writeInt(capacity);
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
