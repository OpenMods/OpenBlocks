package jadedladder.common.tileentity;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityGuide extends TileEntity {

	public enum Mode {
		Sphere, Cube, Pyramid, Cylinder
	}

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
		if (currentMode == Mode.Sphere) { 
			makeSphere(width, height, depth);
		}
	}
	
	/**
	 * Thanks to WorldEdit for this!
	 * @param radiusX
	 * @param radiusY
	 * @param radiusZ
	 * @return
	 */
	public int makeSphere( double radiusX, double radiusY, double radiusZ) {
		int affected = 0;

        radiusX += 0.5;
        radiusY += 0.5;
        radiusZ += 0.5;

        final double invRadiusX = 1 / radiusX;
        final double invRadiusY = 1 / radiusY;
        final double invRadiusZ = 1 / radiusZ;

        final int ceilRadiusX = (int) Math.ceil(radiusX);
        final int ceilRadiusY = (int) Math.ceil(radiusY);
        final int ceilRadiusZ = (int) Math.ceil(radiusZ);

        double nextXn = 0;
        forX: for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextYn = 0;
            forY: for (int y = 0; y <= ceilRadiusY; ++y) {
                final double yn = nextYn;
                nextYn = (y + 1) * invRadiusY;
                double nextZn = 0;
                forZ: for (int z = 0; z <= ceilRadiusZ; ++z) {
                    final double zn = nextZn;
                    nextZn = (z + 1) * invRadiusZ;

                    double distanceSq = lengthSq(xn, yn, zn);
                    if (distanceSq > 1) {
                        if (z == 0) {
                            if (y == 0) {
                                break forX;
                            }
                            break forY;
                        }
                        break forZ;
                    }

                    if (lengthSq(nextXn, yn, zn) <= 1 && lengthSq(xn, nextYn, zn) <= 1 && lengthSq(xn, yn, nextZn) <= 1) {
                        continue;
                    }

                    if (setBlock(x, y, z)) {
                        ++affected;
                    }
                    if (setBlock(-x, y, z)) {
                        ++affected;
                    }
                    if (setBlock(x, -y, z)) {
                        ++affected;
                    }
                    if (setBlock(x, y, -z)) {
                        ++affected;
                    }
                    if (setBlock(-x, -y, z)) {
                        ++affected;
                    }
                    if (setBlock(x, -y, -z)) {
                        ++affected;
                    }
                    if (setBlock(-x, y, -z)) {
                        ++affected;
                    }
                    if (setBlock(-x, -y, -z)) {
                        ++affected;
                    }
                    
                }
            }
        }

        return affected;
    }
	
	private boolean setBlock(double x, double y, double z) {
		shape[height+(int)y][width+(int)x][depth+(int)z] = true;
		return true;
	}

    private static final double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
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

	public void switchMode() {
		int nextMode = currentMode.ordinal() + 1;
		if (nextMode >= Mode.values().length) {
			nextMode = 0;
		}
		currentMode = Mode.values()[nextMode];
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void changeDimension(ForgeDirection orientation) {

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
		
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

}
