package openblocks.common.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGrave extends TileEntity {

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
}
