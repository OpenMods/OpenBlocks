package openblocks.common.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemImaginationGlasses;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityImaginary extends OpenTileEntity {

	public enum Property {
		VISIBLE, SELECTABLE, SOLID
	}
	
	@SideOnly(Side.CLIENT)
	public float visibility;
	
	public TileEntityImaginary() {}
	
	public TileEntityImaginary(Integer color) {
		this.color = color;
	}
	
	public Integer color;
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		color = tag.hasKey("Color") ? tag.getInteger("Color") : null; 
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		if (color != null)
			tag.setInteger("Color", color);
	}

	@Override
	public Packet getDescriptionPacket() {
        NBTTagCompound data = new NBTTagCompound();
        writeToNBT(data);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 42, data);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		if (pkt.data != null)
			readFromNBT(pkt.data);
	}
	
	public boolean isPencil() {
		return color == null;
	}

	public boolean is(Property what, EntityPlayer player) {
		if (what == Property.SOLID && isPencil())
			return true;
		
		ItemStack helmet = player.inventory.armorItemInSlot(3);
		
		if (helmet == null)
			return false;
		
		Item item = helmet.getItem();
		
		if (item instanceof ItemImaginationGlasses)
			return ((ItemImaginationGlasses)item).checkBlock(what, helmet, this);
		
		return false;
	}
	
	public boolean is(EntityPlayer player) {
		return player.getHeldItem() != null;
	}
	
	public boolean is(Property what, Entity e) {
		return (e instanceof EntityPlayer) && is(what, (EntityPlayer)e);
	}
	
	public boolean is(Property what) {
		EntityPlayer player = OpenBlocks.proxy.getThePlayer(); 
		return player != null && is(what, player);
	}
}
