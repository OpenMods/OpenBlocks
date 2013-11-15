package openblocks.common;

import java.util.EnumSet;

import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler {

	private static final int MAP_UPDATE_DELAY = 10;
	private int mapUpdateCount;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.contains(TickType.SERVER)) {
			if (mapUpdateCount++ > MAP_UPDATE_DELAY) {
				mapUpdateCount = 0;
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				if (server != null && server.isServerRunning()) {
					MapDataManager.instance.sendUpdates(server);
				}
			}

		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() {
		return "OpenBlocksCommonTick";
	}

}
