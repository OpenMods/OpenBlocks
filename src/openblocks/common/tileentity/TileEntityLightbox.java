package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.api.ISurfaceAttachment;
import openblocks.common.GenericInventory;

public class TileEntityLightbox extends TileEntity implements IInventory,
		ISurfaceAttachment {

	private GenericInventory inventory = new GenericInventory("lightbox", false, 1);

	/**
	 * The surface it's attached to. Could be any of the 6 main directions
	 */
	private ForgeDirection surface = ForgeDirection.DOWN;

	/**
	 * If surface is UP or DOWN, rotation can be EAST/NORTH/SOUTH/WEST
	 */
	private ForgeDirection rotation = ForgeDirection.EAST;

	/**
	 * just a tick counter used for sending updates
	 */
	private int tickCounter = 0;

	private AxisAlignedBB playerRange = null;

	public TileEntityLightbox() {

	}

	@Override
	public void updateEntity() {

		if (OpenBlocks.proxy.isServer()) {

			// it doesnt matter if we're not updating constantly, right?
			// I mean, the maps will take longer to load in
			// but less lag..
			if (tickCounter % 2 == 0) {

				ItemStack itemstack = inventory.getStackInSlot(0);

				for (EntityPlayer player : (List<EntityPlayer>) worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(10, 10, 10))) {

					if (player instanceof EntityPlayerMP) {

						EntityPlayerMP mpPlayer = (EntityPlayerMP) player;

						if (itemstack != null
								&& Item.itemsList[itemstack.itemID].isMap()
								&& mpPlayer.playerNetServerHandler.packetSize() <= 5) {

							MapData mapdata = Item.map.getMapData(itemstack, worldObj);

							mapdata.func_82568_a(mpPlayer);

							Packet packet = ((ItemMapBase) Item.itemsList[itemstack.itemID]).createMapDataPacket(itemstack, this.worldObj, mpPlayer);

							if (packet != null) {
								mpPlayer.playerNetServerHandler.sendPacketToPlayer(packet);
							}
						}
					}
				}
			}
		}

		tickCounter++;
	}

	public void setSurfaceAndRotation(ForgeDirection surface, ForgeDirection rotation) {
		this.surface = surface;
		this.rotation = rotation;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public ForgeDirection getRotation() {
		return rotation;
	}

	public ForgeDirection getSurface() {
		return surface;
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory.getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return inventory.isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {
		inventory.openChest();
	}

	@Override
	public void closeChest() {
		inventory.closeChest();
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return inventory.isStackValidForSlot(i, itemstack);
	}

	@Override
	public Packet getDescriptionPacket() {
		Packet132TileEntityData packet = new Packet132TileEntityData();
		packet.actionType = 0;
		packet.xPosition = xCoord;
		packet.yPosition = yCoord;
		packet.zPosition = zCoord;
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNetwork(nbt);
		packet.customParam1 = nbt;
		return packet;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNetwork(pkt.customParam1);
	}

	public void writeToNetwork(NBTTagCompound tag) {
		writeToNBT(tag);
	}

	public void readFromNetwork(NBTTagCompound tag) {
		readFromNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
		if (tag.hasKey("rotation")) {
			rotation = ForgeDirection.getOrientation(tag.getInteger("rotation"));
		}
		if (tag.hasKey("surface")) {
			surface = ForgeDirection.getOrientation(tag.getInteger("surface"));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		tag.setInteger("rotation", rotation.ordinal());
		tag.setInteger("surface", surface.ordinal());
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return surface;
	}
}
