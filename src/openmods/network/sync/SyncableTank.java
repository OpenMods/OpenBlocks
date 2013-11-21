package openmods.network.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import openblocks.OpenBlocks;
import openblocks.common.GenericTank;

public class SyncableTank extends GenericTank implements ISyncableObject {

	private boolean dirty = false;
	private long ticksSinceChange = 0;

	public SyncableTank(int capacity, FluidStack... acceptableFluids) {
		super(capacity, acceptableFluids);
	}

	@Override
	public int getTicksSinceChange(World world) {
		return (int)(OpenBlocks.proxy.getTicks(world) - ticksSinceChange);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void markClean() {
		dirty = false;
	}

	@Override
	public void markDirty() {
		dirty = true;
	}

	@Override
	public void readFromStream(DataInput stream) throws IOException {
		int fluidId = stream.readInt();
		if (fluidId > -1) {
			int fluidAmount = stream.readInt();
			this.fluid = new FluidStack(fluidId, fluidAmount);
		} else {
			this.fluid = null;
		}
	}

	@Override
	public void writeToStream(DataOutput stream, boolean fullData) throws IOException {
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
	public void resetChangeTimer(World world) {
		ticksSinceChange = OpenBlocks.proxy.getTicks(world);
	}

}
