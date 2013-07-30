package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.api.IInventoryContainer;
import openblocks.api.ISurfaceAttachment;
import openblocks.common.GenericInventory;

public class TileEntityGrave extends TileEntity implements IInventoryContainer, ISurfaceAttachment {

	private ForgeDirection rotation = ForgeDirection.SOUTH;
	private String perishedUsername;
	private GenericInventory inventory = new GenericInventory("grave", false, 40);
	public boolean onSoil = true;
	
	public TileEntityGrave(){
		
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		/* TODO: Implement ambient sound */
		
		if (OpenBlocks.proxy.isServer()) {
			if (worldObj.difficultySetting > 0 && worldObj.rand.nextDouble() < 0.002) {
				List<Entity> mobs = worldObj.getEntitiesWithinAABB(
													IMob.class,
													AxisAlignedBB.getAABBPool().getAABB(
														xCoord, yCoord, zCoord,
														xCoord+1, yCoord+1, zCoord+1
												).expand(7, 7, 7));
				if (mobs.size() < 5) {
					EntityLiving living = null;
					double chance = worldObj.rand.nextDouble();
					if (chance < 0.5) {
						living = new EntitySkeleton(worldObj);
					}else {
						living = new EntityBat(worldObj);
					}

					living.setPositionAndRotation(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, worldObj.rand.nextFloat() * 360, 0);
					if (living.getCanSpawnHere()) {
						worldObj.spawnEntityInWorld(living);
					}
				}
			}
		}
	}
	
	public String getUsername(){
		return perishedUsername == null ? "Unknown" : perishedUsername;
	}
	
	public IInventory getLoot(){
		return inventory;
	}
	
	public void setUsername(String username){
		this.perishedUsername = username;
	}
	
	public void setLoot(IInventory invent){
		inventory.copyFrom(invent);
	}	

	public void setRotation(ForgeDirection rotation){
		this.rotation = rotation;
	}
	
	public ForgeDirection getRotation() {
		if(	rotation == null ||
			rotation == ForgeDirection.UNKNOWN ||
			rotation == ForgeDirection.UP ||
			rotation == ForgeDirection.DOWN){
			rotation = ForgeDirection.NORTH;
		}
		return rotation;
	}

	@Override
	public IInventory[] getInternalInventories() {
		return new IInventory[] { inventory };
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
		inventory.readFromNBT(tag);
		if (tag.hasKey("perishedUsername")) {
			perishedUsername = tag.getString("perishedUsername");
		}
		if (tag.hasKey("rotation")) {
			rotation = ForgeDirection.getOrientation(tag.getInteger("rotation"));
		}
		if (tag.hasKey("onsoil")) {
			onSoil = tag.getBoolean("onsoil");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		tag.setString("perishedUsername", getUsername());
		tag.setInteger("rotation", rotation.ordinal());
		tag.setBoolean("onsoil", onSoil);
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN;
	}

	public boolean isOnSoil() {
		return onSoil;
	}

}
