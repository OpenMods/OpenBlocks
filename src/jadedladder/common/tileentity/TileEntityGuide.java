package jadedladder.common.tileentity;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jadedladder.JadedLadder;
import jadedladder.common.IShapeable;
import jadedladder.common.block.BlockGuide;
import jadedladder.shapes.ShapeFactory;
import jadedladder.shapes.ShapeFactory.Mode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityGuide extends TileEntity implements IShapeable {


	private boolean shape[][][];
	
	public int width = 8;
	public int height = 8;
	public int depth = 8;

	private Mode currentMode = Mode.Sphere;

	public Mode getCurrentMode() {
		return currentMode;
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
		recreateShape();
	}

	private void recreateShape() {
		shape = new boolean[height * 2 + 1][width * 2 + 1][depth * 2 + 1];
		ShapeFactory.generateShape(width, height, depth, this, currentMode);
	}	
	
	public void setBlock(int x, int y, int z) {
		try{
			shape[height+y][width+x][depth+z] = true;
		}catch(IndexOutOfBoundsException iobe){
			System.out.println(String.format("Index out of bounds setting block at %s,%s,%s", x,y,z));
		}
	}
    
	public boolean[][][] getShape() {
		return shape;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("width")) {
			width = nbt.getInteger("width");
		}
		if (nbt.hasKey("height")) {
			height = nbt.getInteger("height");
		}
		if (nbt.hasKey("depth")) {
			depth = nbt.getInteger("depth");
		}
		if (nbt.hasKey("mode")) {
			currentMode = Mode.values()[nbt.getInteger("mode")];
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("width", width);
		nbt.setInteger("height", height);
		nbt.setInteger("depth", depth);
		nbt.setInteger("mode", currentMode.ordinal());
	}


	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB box = super.getRenderBoundingBox();
		return box.expand(width, height, depth);
	}


	public void switchMode(EntityPlayer player) {
		switchMode();
		if (player != null) {
			player.sendChatToPlayer(String.format("Changing to %s mode", currentMode.getDisplayName()));
		}
	}
	
	public void switchMode() {
		int nextMode = currentMode.ordinal() + 1;
		if (nextMode >= Mode.values().length) {
			nextMode = 0;
		}
		currentMode = Mode.values()[nextMode];
		if (currentMode.isFixedRatio()) {
			height = depth = width;
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void changeDimensions(ForgeDirection orientation) {
		if (width > 0 && orientation == ForgeDirection.EAST) {
			width--;
		}else if (orientation == ForgeDirection.WEST) {
			width++;
		}else if (orientation == ForgeDirection.NORTH) {
			depth++;
		}else if (depth > 0 && orientation == ForgeDirection.SOUTH) {
			depth--;
		}else if (orientation == ForgeDirection.UP) {
			height++;
		}else if (height > 0 && orientation == ForgeDirection.DOWN) {
			height--;
		}
		if (currentMode.isFixedRatio()) {
			if (width != height && width != depth) {
				height = depth = width;
			}else if (height != width && height != depth) {
				depth = width = height;
			}else if (depth != width && depth != height) {
				width = height = depth;
			}
		}
		
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

}
