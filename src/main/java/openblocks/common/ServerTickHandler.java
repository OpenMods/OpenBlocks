package openblocks.common;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import openblocks.enchantments.FlimFlamEnchantmentsHandler;
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
		} else if (type.contains(TickType.PLAYER)) {
			Object player = tickData[0];
			if (player instanceof EntityPlayerMP) FlimFlamEnchantmentsHandler.deliverKarma((EntityPlayerMP)player);
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER, TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "OpenBlocksCommonTick";
	}

}
