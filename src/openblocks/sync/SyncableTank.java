package openblocks.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import openblocks.common.GenericTank;

public class SyncableTank extends GenericTank implements ISyncableObject {

	private boolean dirty = false;
	private int ticksSinceChange = 0;

	public SyncableTank(int capacity, FluidStack... acceptableFluids) {
		super(capacity, acceptableFluids);
	}

	public int getTicksSinceChange() {
		return ticksSinceChange;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void markClean() {
		dirty = false;
		ticksSinceChange++;
	}

	@Override
	public void markDirty() {
		dirty = true;
	}

	@Override
	public void readFromStream(DataInputStream stream) throws IOException {
		int fluidId = stream.readInt();
		if (fluidId > -1) {
			int fluidAmount = stream.readInt();
			this.fluid = new FluidStack(fluidId, fluidAmount);
		} else {
			this.fluid = null;
		}
	}

	@Override
	public void writeToStream(DataOutputStream stream, boolean fullData)
			throws IOException {
		if (fluid != null) {
			stream.writeInt(fluid.fluidID);
			stream.writeInt(fluid.amount);
		} else {
			stream.writeInt(-1);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag, String name) {
		this.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag, String name) {
		this.readFromNBT(tag);
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		int filled = super.fill(resource, doFill);
		if (filled > 0) {
			markDirty();
		}
		return filled;
	}

	@Override
	public FluidStack drain(FluidStack stack, boolean doDrain) {
		FluidStack drained = super.drain(stack, doDrain);
		if (drained != null) {
			markDirty();
		}
		return drained;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		FluidStack drained = super.drain(maxDrain, doDrain);
		if (drained != null) {
			markDirty();
		}
		return drained;
	}

	@Override
	public void resetChangeTimer() {
		ticksSinceChange = 0;
	}
}
