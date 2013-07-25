package openblocks.common.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityGrave extends TileEntity {

	private ForgeDirection rotation;
	private String perishedUsername;
	private ItemStack[] loot;
	
	public String getUsername(){
		return perishedUsername;
	}
	
	public ItemStack[] getLoot(){
		return loot;
	}
	
	public void setUsername(String username){
		this.perishedUsername = username;
	}
	
	public void setLoot(ItemStack[] itemStack){
		if(itemStack == null) itemStack = new ItemStack[0];
		this.loot = itemStack;
	}	

	public void setRotation(ForgeDirection rotation){
		this.rotation = rotation;
	}
	
	public ForgeDirection getRotation() {
		if(		rotation == null ||
				rotation == ForgeDirection.UNKNOWN ||
				rotation == ForgeDirection.UP ||
				rotation == ForgeDirection.DOWN){
			rotation = ForgeDirection.NORTH;
			System.out.println("OpenBlocks: WARNING: Grave was badly rotated, I fixed it for you. You're welcome.");
		}
		return rotation;
	}

}
