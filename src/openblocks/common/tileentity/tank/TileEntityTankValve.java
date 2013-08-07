package openblocks.common.tileentity.tank;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import cpw.mods.fml.common.Loader;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import openblocks.api.IAwareTile;
import openblocks.api.ISurfaceAttachment;
import openblocks.client.fx.FXLiquidSpray;
import openblocks.common.tileentity.OpenTileEntity;
import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;
import openblocks.utils.BlockUtils;

public class TileEntityTankValve extends TileEntityTankBase implements ISurfaceAttachment, ITankContainer, IAwareTile {

	private LiquidTank fakeTank = new LiquidTank(0);
	private ForgeDirection rotation = ForgeDirection.EAST;
	
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		TileEntityTank tank = getTankInDirection(rotation);
		int filled = 0;
		if (tank != null) {
			lastLiquid = resource;
			filled = tank.fill(resource, doFill, null);
		}
		return filled;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		ILiquidTank tank = getTank(direction, null);
		if (tank != null) {
			return new ILiquidTank[] { fakeTank };
		}
		return new ILiquidTank[] { fakeTank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		TileEntityTank[] tanks = getSurroundingTanks();
		if (tanks.length > 0) {
			return tanks[0].getInternalTank();
		}
		return fakeTank;
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		// TODO Auto-generated method stub
		
	}

	public void setRotation(ForgeDirection rotation) {
		this.rotation = rotation;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public ForgeDirection getRotation() {
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
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("rotation")) {
			rotation = ForgeDirection.getOrientation(tag.getInteger("rotation"));
		}
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("rotation", rotation.ordinal());
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return rotation;
	}
	
	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, float hitX, float hitY, float hitZ) {
		super.onBlockPlacedBy(player, side, hitX, hitY, hitZ);
		setRotation(side.getOpposite());
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) {
			if (lastLiquid != null) {
				FXLiquidSpray fx = new FXLiquidSpray(worldObj, lastLiquid, xCoord, yCoord, zCoord, 1.5F, 0xFF0000, 6);
				fx.noClip = true;
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}
}
