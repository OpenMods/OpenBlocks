package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.api.ISurfaceAttachment;
import openblocks.common.block.BlockFlag;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableFloat;
import openblocks.sync.SyncableInt;

public class TileEntityFlag extends NetworkedTileEntity implements
		ISurfaceAttachment {

	public enum Keys {
		rotation, colorIndex
	}

	private SyncableFloat rotation = new SyncableFloat(0.0f);
	private SyncableInt colorIndex = new SyncableInt(0);

	public TileEntityFlag() {
		addSyncedObject(Keys.rotation, rotation);
		addSyncedObject(Keys.colorIndex, colorIndex);
	}

	@Override
	protected void initialize() {}

	@Override
	public void onSynced(List<ISyncableObject> changes) {}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		colorIndex.readFromNBT(tag, "color");
		rotation.readFromNBT(tag, "rotation");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		colorIndex.writeToNBT(tag, "color");
		rotation.writeToNBT(tag, "rotation");
	}

	public float getRotation() {
		return rotation.getValue();
	}

	public Icon getIcon() {
		return OpenBlocks.Blocks.flag.getIcon(0, 0);
	}

	public void setColorIndex(int index) {
		colorIndex.setValue(index);
	}

	public int getColor() {
		if (colorIndex.getValue() >= BlockFlag.COLORS.length) colorIndex.setValue(0);
		return BlockFlag.COLORS[colorIndex.getValue()];
	}

	public void setSurfaceAndRotation(ForgeDirection surface, float rot) {
		// this.surface = ForgeDirection.DOWN; // TODO: FIX
		rotation.setValue(rot);
		sync();
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN; // TODO: fix
	}

}
