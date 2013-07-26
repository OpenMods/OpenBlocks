package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;

public class TileEntityTarget extends TileEntity {

	private ForgeDirection rotation = ForgeDirection.WEST;

	private float targetRotation = 0;
	private int strength = 0;
	private int tickCounter = -1;
	private boolean isPowered = false;

	@Override
	public void updateEntity() {
		tickCounter--;
		if (tickCounter == 0) {
			tickCounter = -1;
			strength = 0;
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord,
					OpenBlocks.Config.blockTargetId);
		}
	}

	public void setRotation(ForgeDirection rotation) {
		this.rotation = rotation;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public ForgeDirection getRotation() {
		return rotation;
	}

	public float getTargetRotation() {
		return isPowered ? 0 : -(float) (Math.PI / 2);
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
		tickCounter = 10;
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord,
				OpenBlocks.Config.blockTargetId);
	}

	private void onRedstoneChanged() {
		if (!isPowered) {
			List<EntityArrow> arrows = (List<EntityArrow>) worldObj
					.getEntitiesWithinAABB(
							EntityArrow.class,
							AxisAlignedBB.getAABBPool().getAABB(
									(double) xCoord - 0.1,
									(double) yCoord - 0.1,
									(double) zCoord - 0.1,
									(double) xCoord + 1.1,
									(double) yCoord + 1.1,
									(double) zCoord + 1.1));

			if (arrows.size() > 0) {
				ItemStack newStack = new ItemStack(Item.arrow, arrows.size(), 0);
				EntityItem item = new EntityItem(worldObj, xCoord + 0.5,
						yCoord + 0.5, zCoord + 0.5, newStack);
				worldObj.spawnEntityInWorld(item);
			}
			for (EntityArrow arrow : arrows) {
				arrow.setDead();
			}

		}
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5,
				isPowered ? "openblocks.open" : "openblocks.close", 0.5f, 1.0f);

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
			rotation = ForgeDirection
					.getOrientation(tag.getInteger("rotation"));
		}
		if (tag.hasKey("powered")) {
			isPowered = tag.getBoolean("powered");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("rotation", rotation.ordinal());
		tag.setBoolean("powered", isPowered);
	}

	public void neighbourBlockChanged() {
		boolean nowPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord,
				yCoord, zCoord);
		if (isPowered != nowPowered) {
			isPowered = nowPowered;
			onRedstoneChanged();
		}
	}

	public boolean isPowered() {
		return isPowered;
	}

	public void setPowered(boolean powered) {
		isPowered = powered;
	}
}
