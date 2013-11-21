package openmods.network;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.event.Event;
import openblocks.Log;
import openblocks.OpenBlocks;
import openblocks.common.MapDataManager;
import openblocks.common.events.PlayerMovementEvent;
import openblocks.common.events.StencilCraftEvent;
import openblocks.common.events.TileEntityMessageEventPacket;
import openblocks.utils.ByteUtils;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.network.Player;

public abstract class EventPacket extends Event {
	public enum PacketDirection {
		TO_CLIENT(false, true),
		FROM_CLIENT(true, false),
		ANY(true, true);

		public final boolean toServer;
		public final boolean toClient;

		private PacketDirection(boolean toServer, boolean toClient) {
			this.toServer = toServer;
			this.toClient = toClient;
		}

		public boolean validateSend(boolean isRemote) {
			return (isRemote && toServer) || (!isRemote && toClient);
		}

		public boolean validateReceive(boolean isRemote) {
			return (isRemote && toClient) || (!isRemote && toServer);
		}
	}

	public enum EventType {
		MAP_DATA_REQUEST {
			@Override
			public EventPacket createPacket() {
				return new MapDataManager.MapDataRequestEvent();
			}

			@Override
			public PacketDirection getDirection() {
				return PacketDirection.FROM_CLIENT;
			}
		},
		MAP_DATA_RESPONSE {
			@Override
			public EventPacket createPacket() {
				return new MapDataManager.MapDataResponseEvent();
			}

			@Override
			public PacketDirection getDirection() {
				return PacketDirection.TO_CLIENT;
			}

			@Override
			public boolean isCompressed() {
				return true;
			}
		},
		MAP_UPDATES {
			@Override
			public EventPacket createPacket() {
				return new MapDataManager.MapUpdatesEvent();
			}

			@Override
			public PacketDirection getDirection() {
				return PacketDirection.TO_CLIENT;
			}
		},
		TILE_ENTITY_NOTIFY {

			@Override
			public EventPacket createPacket() {
				return new TileEntityMessageEventPacket();
			}

			@Override
			public PacketDirection getDirection() {
				return PacketDirection.ANY;
			}

		},
		PLAYER_MOVEMENT {
			@Override
			public EventPacket createPacket() {
				return new PlayerMovementEvent();
			}

			@Override
			public PacketDirection getDirection() {
				return PacketDirection.FROM_CLIENT;
			}
		},
		STENCIL_CRAFT {
			@Override
			public EventPacket createPacket() {
				return new StencilCraftEvent();
			}

			@Override
			public PacketDirection getDirection() {
				return PacketDirection.FROM_CLIENT;
			}
		};

		public static final EventType[] VALUES = values();

		public abstract EventPacket createPacket();

		public abstract PacketDirection getDirection();

		public boolean isCompressed() {
			return false;
		}
	}

	public static EventPacket deserializeEvent(Packet250CustomPayload packet) throws IOException {
		ByteArrayInputStream bytes = new ByteArrayInputStream(packet.data);

		EventPacket event;
		EventType type;
		{
			DataInput input = new DataInputStream(bytes);
			int id = ByteUtils.readVLI(input);
			type = EventType.VALUES[id];
			event = type.createPacket();
		}

		InputStream stream = type.isCompressed()? new GZIPInputStream(bytes) : bytes;

		{
			DataInput input = new DataInputStream(stream);
			event.readFromStream(input);
		}

		stream.close();
		return event;
	}

	public static Packet250CustomPayload serializeEvent(EventPacket event) {
		try {
			ByteArrayOutputStream payload = new ByteArrayOutputStream();

			EventType type = event.getType();
			{
				DataOutput output = new DataOutputStream(payload);
				ByteUtils.writeVLI(output, type.ordinal());
			}

			OutputStream stream = type.isCompressed()? new GZIPOutputStream(payload) : payload;

			{
				DataOutput output = new DataOutputStream(stream);
				event.writeToStream(output);
				stream.close();
			}

			Packet250CustomPayload result = new Packet250CustomPayload(PacketHandler.CHANNEL_EVENTS, payload.toByteArray());
			return result;
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public INetworkManager manager;

	public Player player;

	public abstract EventType getType();

	protected abstract void readFromStream(DataInput input) throws IOException;

	protected abstract void writeToStream(DataOutput output) throws IOException;

	public void reply(EventPacket reply) {
		boolean isRemote = !(player instanceof EntityPlayerMP);
		if (!getType().getDirection().validateSend(isRemote)) manager.addToSendQueue(serializeEvent(reply));
		else Log.warn("Invalid sent direction for packet '%s'", this);
	}

	protected boolean checkSendToClient() {
		if (!getType().getDirection().toClient) {
			Log.warn("Trying to sent message '%s' to client", this);
			return false;
		}
		return true;
	}

	protected boolean checkSendToServer() {
		if (!getType().getDirection().toServer) {
			Log.warn("Trying to sent message '%s' to server", this);
			return false;
		}
		return true;
	}

	public void sendToPlayer(Player player) {
		if (checkSendToClient()) OpenBlocks.proxy.sendPacketToPlayer(player, serializeEvent(this));
	}

	public void sendToServer() {
		if (checkSendToServer()) OpenBlocks.proxy.sendPacketToServer(serializeEvent(this));
	}
}