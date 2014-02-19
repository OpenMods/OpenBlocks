package openblocks.events;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

import openblocks.client.WallpaperManager;
import openblocks.common.WallpaperData;
import openmods.OpenMods;
import openmods.network.EventPacket;
import openmods.network.IEventPacketType;
import openmods.network.events.TileEntityMessageEventPacket;
import openmods.tileentity.OpenTileEntity;

public class WallpaperEvents {
	
	public static class WallpaperRequestEvent extends EventPacket {

		private String id;
		
		public WallpaperRequestEvent() {}
		
		public WallpaperRequestEvent(String id) {
			this.id = id;
		}
		
		@Override
		public IEventPacketType getType() {
			return EventTypes.WALLPAPER_REQUEST;
		}

		@Override
		protected void readFromStream(DataInput input) throws IOException {
			id = input.readUTF();
			// nope, cant do that here. Gotta subscribe properly I guess.
			World overworld = DimensionManager.getWorld(0);
			WallpaperData data = (WallpaperData) overworld.loadItemData(WallpaperData.class, id);
			new WallpaperResponseEvent(id, data).sendToPlayer(player);
		}

		@Override
		protected void writeToStream(DataOutput output) throws IOException {
			output.writeUTF(id);
		}
		
	}
	
	public static class WallpaperResponseEvent extends EventPacket {

		private WallpaperData data;
		private String id;
		
		public WallpaperResponseEvent() {}
		
		public WallpaperResponseEvent(String id, WallpaperData data) {
			this.id = id;
			this.data = data;
		}

		@Override
		public IEventPacketType getType() {
			return EventTypes.WALLPAPER_RESPONSE;
		}

		@Override
		protected void readFromStream(DataInput input) throws IOException {
			id = input.readUTF();
			data = new WallpaperData(id);
			data.readFromStream(input);
			WallpaperManager.setWallpaper(id, data.colorData);
		}

		@Override
		protected void writeToStream(DataOutput output) throws IOException {
			output.writeUTF(id);
			data.writeToStream(output);
		}
		
	}
	
	public static class WallpaperCreateEvent extends TileEntityMessageEventPacket {

		private int[] colors;
		
		public WallpaperCreateEvent() {}
		
		public WallpaperCreateEvent(OpenTileEntity te, int[] colors) {
			super(te);
			this.colors = colors;
		}
		
		@Override
		protected void readPayload(DataInput input) {
			colors = new int[256];
			try {
				for (int i = 0; i < 256; i++) {
					colors[i] = input.readInt();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void writePayload(DataOutput output) {
			try {
				for (int i = 0; i < 256; i++) {
					output.writeInt(colors[i]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public int[] getColors() {
			return colors;
		}
		
		@Override
		public IEventPacketType getType() {
			return EventTypes.WALLPAPER_CREATE;
		}
	}
}
