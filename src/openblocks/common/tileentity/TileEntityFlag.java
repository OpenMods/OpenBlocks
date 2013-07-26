package openblocks.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import openblocks.OpenBlocks;

public class TileEntityFlag extends TileEntity {

	private float rotation = 0f;
	private int color = 8450847;
	private int colorIndex = 0;
	
	public TileEntityFlag() {
	}
	
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

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
		if (tag.hasKey("color")) {
			color = tag.getInteger("color");
		}
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setFloat("rotation", rotation);
		tag.setInteger("color", color);
	}

	public Icon getIcon() {
		return OpenBlocks.Blocks.flag.getIcon(0, 0);
	}

	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void onActivated(EntityPlayer player) {
		colorIndex++;
		int red   = (int) (Math.sin(0.4*colorIndex + 0) * 127 + 128) % 255;
		int green = (int) (Math.sin(0.4*colorIndex + 2) * 127 + 128) % 255;
		int blue  = (int) (Math.sin(0.4*colorIndex + 4) * 127 + 128) % 255;
		setColor(red << 16 | green << 8 | blue);
	}

	
}
