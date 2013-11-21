package openblocks.common.events;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import openblocks.OpenBlocks;
import openmods.common.tileentity.OpenTileEntity;
import openmods.network.EventPacket;
import openmods.network.PacketHandler;

import com.google.common.base.Preconditions;

import cpw.mods.fml.common.network.Player;

public class TileEntityMessageEventPacket extends EventPacket {

	public int xCoord;
	public int yCoord;
	public int zCoord;

	public TileEntityMessageEventPacket() {}

	public TileEntityMessageEventPacket(OpenTileEntity tile) {
		xCoord = tile.xCoord;
		yCoord = tile.yCoord;
		zCoord = tile.zCoord;
	}

	@Override
	public void readFromStream(DataInput input) throws IOException {
		xCoord = input.readInt();
		yCoord = input.readInt();
		zCoord = input.readInt();
		readPayload(input);
	}

	protected void readPayload(DataInput input) {}

	@Override
	public void writeToStream(DataOutput output) throws IOException {
		output.writeInt(xCoord);
		output.writeInt(yCoord);
		output.writeInt(zCoord);
		writePayload(output);
	}

	protected void writePayload(DataOutput output) {}

	protected World getWorld() {
		return ((EntityPlayer)player).worldObj;
	}

	public OpenTileEntity getTileEntity() {
		World world = getWorld();
		Preconditions.checkNotNull(world, "Invalid packet data");

		TileEntity te = world.getBlockTileEntity(xCoord, yCoord, zCoord);
		return (te instanceof OpenTileEntity)? (OpenTileEntity)te : null;
	}

	public void sendToWatchers(WorldServer world) {
		if (checkSendToClient()) {
			Packet packet = serializeEvent(this);
			for (EntityPlayer player : PacketHandler.getPlayersWatchingBlock(world, xCoord, zCoord))
				OpenBlocks.proxy.sendPacketToPlayer((Player)player, packet);
		}
	}

	@Override
	public EventType getType() {
		return EventType.TILE_ENTITY_NOTIFY;
	}
}
