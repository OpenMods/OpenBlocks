package openblocks.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeSubscribe;
import openblocks.client.WallpaperIconManager;
import openblocks.events.EventTypes;
import openmods.network.EventPacket;
import openmods.network.IEventPacketType;
import openmods.network.events.TileEntityMessageEventPacket;
import openmods.tileentity.OpenTileEntity;

public class WallpaperManager {

	public static class WallpaperRequestEvent extends EventPacket {

		private String id;
		
		public WallpaperRequestEvent() {}
		
		public WallpaperRequestEvent(String id) {
			this.id = id;
		}
		
		public String getId() {
			return id;
		}
		
		@Override
		public IEventPacketType getType() {
			return EventTypes.WALLPAPER_REQUEST;
		}

		@Override
		protected void readFromStream(DataInput input) throws IOException {
			id = input.readUTF();
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
			WallpaperIconManager.setWallpaper(id, data.colorData);
		}

		@Override
		protected void writeToStream(DataOutput output) throws IOException {
			output.writeUTF(id);
			data.writeToStream(output);
		}
		
		public String getId() {
			return id;
		}
		
		public WallpaperData getData() {
			return data;
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
	
	@ForgeSubscribe
	public void onWallpaperRequest(WallpaperRequestEvent evt) {
		World overworld = DimensionManager.getWorld(0);
		String id = evt.getId();
		WallpaperData data = (WallpaperData) overworld.loadItemData(WallpaperData.class, id);
		evt.reply(new WallpaperResponseEvent(id, data));
	}
	
	@ForgeSubscribe
	public void onWallpaperResponse(WallpaperResponseEvent evt) {
		String id = evt.getId();
		WallpaperIconManager.setWallpaper(id, evt.getData().colorData);
	}
}
