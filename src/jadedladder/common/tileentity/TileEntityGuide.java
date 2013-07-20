package jadedladder.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jadedladder.JadedLadder;
import jadedladder.common.block.BlockGuide;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityGuide extends TileEntity {

	public enum Mode {
		Sphere, Cube, Pyramid, Cylinder
	}

	private int currentStrength = 0;
	private Mode currentMode = Mode.Sphere;

	public Mode getCurrentMode() {
		return currentMode;
	}

	public void updateRedstoneState() {
		int newStrength = worldObj.getBlockPowerInput(xCoord, yCoord, zCoord);
		if (currentStrength != newStrength) {
			currentStrength = newStrength;
			onStrengthChanged();
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
		packet.customParam1 = nbt;
		return packet;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.customParam1);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		currentStrength = nbt.getInteger("currentStrength");
		if (nbt.hasKey("mode")) {
			currentMode = Mode.values()[nbt.getInteger("mode")];
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("currentStrength", currentStrength);
		nbt.setInteger("mode", currentMode.ordinal());
	}

	public void onStrengthChanged() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public int getCurrentStrength() {
		return currentStrength;
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB box = super.getRenderBoundingBox();
		return box.expand(currentStrength, currentStrength, currentStrength)
				.offset(0, currentStrength / 2, 0);
	}

	public void switchMode() {
		int nextMode = currentMode.ordinal() + 1;
		if (nextMode >= Mode.values().length) {
			nextMode = 0;
		}
		currentMode = Mode.values()[nextMode];
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

}
