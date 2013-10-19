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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.ForgeDirection;
import openblocks.client.gui.GuiLightbox;
import openblocks.common.GenericInventory;
import openblocks.common.api.IAwareTile;
import openblocks.common.api.IHasGui;
import openblocks.common.api.ISurfaceAttachment;
import openblocks.common.container.ContainerLightbox;

public class TileEntityLightbox extends OpenTileEntity implements IInventory,
		ISurfaceAttachment, IAwareTile, IHasGui {

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

	public TileEntityLightbox() {}

	@SuppressWarnings("unchecked")
	@Override
	public void updateEntity() {

		if (!worldObj.isRemote) {

			// it doesnt matter if we're not updating constantly, right?
			// I mean, the maps will take longer to load in
			// but less lag..
			if (tickCounter % 2 == 0) {

				ItemStack itemstack = inventory.getStackInSlot(0);

				if (itemstack != null && itemstack.getItem().isMap()) {
					List<EntityPlayer> nearbyPlayers = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(10, 10, 10));

					for (EntityPlayer player : nearbyPlayers) {

						if (player instanceof EntityPlayerMP) {

							EntityPlayerMP mpPlayer = (EntityPlayerMP)player;

							if (mpPlayer.playerNetServerHandler.packetSize() <= 5) {

								MapData mapdata = Item.map.getMapData(itemstack, worldObj);

								mapdata.func_82568_a(mpPlayer);

								Packet packet = ((ItemMapBase)Item.itemsList[itemstack.itemID]).createMapDataPacket(itemstack, this.worldObj, mpPlayer);

								if (packet != null) {
									mpPlayer.playerNetServerHandler.sendPacketToPlayer(packet);
								}
							}
						}
					}
				}
			}
		}

		tickCounter++;
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerLightbox(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiLightbox(new ContainerLightbox(player.inventory, this));
	}

	public void setSurfaceAndRotation(ForgeDirection surface, ForgeDirection rotation) {
		this.surface = surface;
		this.rotation = rotation;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
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
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return (itemstack != null && itemstack.getItem().isMap());
	}

	@Override
	public Packet getDescriptionPacket() {
		return Packet132TileEntity.writeToPacket(this);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
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

	@Override
	public void onBlockBroken() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockAdded() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			openGui(player);
		}
		if (player.isSneaking()) { return false; }
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
	}

}
