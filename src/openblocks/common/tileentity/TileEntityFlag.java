package openblocks.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.api.ISurfaceAttachment;
import openblocks.common.block.BlockFlag;

public class TileEntityFlag extends TileEntity implements ISurfaceAttachment {

	private float rotation = 0f;
	private int colorIndex = 0;
	private ForgeDirection surface = ForgeDirection.DOWN;

	public TileEntityFlag() {}

	public float getRotation() {
		return rotation;
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
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("rotation")) {
			rotation = tag.getFloat("rotation");
		}
		if (tag.hasKey("surface")) {
			surface = ForgeDirection.getOrientation(tag.getInteger("surface"));
		}
		if (tag.hasKey("color")) {
			colorIndex = tag.getInteger("color");
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setFloat("rotation", rotation);
		tag.setInteger("surface", surface.ordinal());
		tag.setInteger("color", colorIndex);
	}

	public Icon getIcon() {
		return OpenBlocks.Blocks.flag.getIcon(0, 0);
	}

	public int getColor() {
		if (colorIndex >= BlockFlag.COLORS.length) colorIndex = 0;
		return BlockFlag.COLORS[colorIndex];
	}

	public void setColorIndex(int colorIndex) {
		this.colorIndex = colorIndex;
	}

	public void setSurfaceAndRotation(ForgeDirection surface, float rotation) {
		this.surface = surface;
		this.rotation = rotation;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return surface;
	}

}
