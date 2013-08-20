package openblocks.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.api.IAwareTile;
import openblocks.utils.BlockUtils;

public class TileEntityTrophy extends OpenTileEntity implements IAwareTile {

	public static Trophy debugTrophy = Trophy.Wolf;

	public Trophy trophyType;

	private ForgeDirection rotation = ForgeDirection.EAST;

	private int sinceLastActivate = 0;

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

	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			trophyType.executeTickBehavior(this);
			if (sinceLastActivate < Integer.MAX_VALUE) {
				sinceLastActivate++;
			}
		}
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub

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
			trophyType.playSound(worldObj, xCoord, yCoord, zCoord);
			trophyType.executeActivateBehavior(this, player);
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub

	}

	public Trophy getTrophyType() {
		return trophyType;
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		/**
		 * Debug only. These will be dropped randomly with mobs!
		 */
		if (!worldObj.isRemote) {
			if (stack.hasTagCompound()) {
				NBTTagCompound tag = stack.getTagCompound();
				if (tag.hasKey("entity")) {
					String entityKey = tag.getString("entity");
					trophyType = Trophy.valueOf(entityKey);
				}
			}
			if (trophyType == null) {
				int next = (debugTrophy.ordinal() + 1) % Trophy.values().length;
				debugTrophy = Trophy.values()[next];
				trophyType = debugTrophy;
			}
			rotation = BlockUtils.get2dOrientation(player);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("trophytype")) {
			trophyType = Trophy.valueOf(tag.getString("trophytype"));
		}
		if (tag.hasKey("rotation")) {
			rotation = ForgeDirection.getOrientation(tag.getInteger("rotation"));
		}
		if (tag.hasKey("sinceLastActivate")) {
			sinceLastActivate = tag.getInteger("sinceLastActivate");
		}
	}

	public int sinceLastActivate() {
		return sinceLastActivate;
	}

	public void resetActivationTimer() {
		sinceLastActivate = 0;
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setString("trophytype", trophyType.toString());
		tag.setInteger("rotation", rotation.ordinal());
		tag.setInteger("sinceLastActivate", sinceLastActivate);
	}

	public ForgeDirection getRotation() {
		return rotation;
	}

}
