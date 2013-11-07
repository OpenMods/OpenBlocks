package openblocks.network;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.event.Event;
import openblocks.OpenBlocks;
import openblocks.utils.ByteUtils;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.network.Player;

public abstract class EventPacket extends Event {
	public enum EventType {
		DUMMY {
			@Override
			public EventPacket createPacket() {
				// just to make it compile, will be used for maps
				return null;
			}
		},		
		TILE_MESSAGE {
			public EventPacket createPacket() {
				return new TileEntityMessageEventPacket();
			}
		};

		public static final EventType[] VALUES = values();

		public abstract EventPacket createPacket();
	}

	public static EventPacket deserializeEvent(Packet250CustomPayload packet) throws IOException {
		ByteArrayInputStream bytes = new ByteArrayInputStream(packet.data);

		EventPacket event;
		{
			DataInput input = new DataInputStream(bytes);
			int id = ByteUtils.readVLI(input);
			EventType type = EventType.VALUES[id];
			event = type.createPacket();
		}

		InputStream stream = event.isCompressed()? new GZIPInputStream(bytes) : bytes;

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

			{
				DataOutput output = new DataOutputStream(payload);
				EventType type = event.getType();
				ByteUtils.writeVLI(output, type.ordinal());
			}

			OutputStream stream = event.isCompressed()? new GZIPOutputStream(payload) : payload;

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

	public boolean isCompressed() {
		return false;
	}

	public abstract void readFromStream(DataInput input) throws IOException;

	public abstract void writeToStream(DataOutput output) throws IOException;

	public void reply(EventPacket reply) {
		manager.addToSendQueue(serializeEvent(reply));
	}

	public void sendToPlayer(Player player) {
		OpenBlocks.proxy.sendPacketToPlayer(player, serializeEvent(this));
	}

	public void sendToServer() {
		OpenBlocks.proxy.sendPacketToServer(serializeEvent(this));
	}
}