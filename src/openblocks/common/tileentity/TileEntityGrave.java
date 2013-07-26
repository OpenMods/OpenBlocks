package openblocks.common.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openblocks.api.IInventoryContainer;
import openblocks.common.GenericInventory;

public class TileEntityGrave extends TileEntity implements IInventoryContainer {

	private ForgeDirection rotation = ForgeDirection.SOUTH;
	private String perishedUsername;
	private GenericInventory inventory = new GenericInventory("grave", false, 40);
	public boolean onSoil = false;
	
	public TileEntityGrave(){
		
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		/* TODO: Implement ambient sound */
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
			System.out.println("OpenBlocks: WARNING: Grave was badly rotated, I fixed it for you. You're welcome.");
		}
		return rotation;
	}

	@Override
	public IInventory[] getInternalInventories() {
		return new IInventory[] { inventory };
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
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		tag.setString("perishedUsername", getUsername());
		tag.setInteger("rotation", rotation.ordinal());
	}

}
