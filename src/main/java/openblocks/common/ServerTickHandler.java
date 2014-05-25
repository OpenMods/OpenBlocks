package openblocks.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import openblocks.enchantments.FlimFlamEnchantmentsHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ServerTickHandler {

	
	// TODO split
	private static final int MAP_UPDATE_DELAY = 10;
	private int mapUpdateCount;

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent evt) {
		if (evt.phase == Phase.START && mapUpdateCount++ > MAP_UPDATE_DELAY) {
				mapUpdateCount = 0;
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				if (server != null && server.isServerRunning()) {
					MapDataManager.instance.sendUpdates(server);
				}
			}
	}
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent evt) {
		if (evt.phase == Phase.START) {
			if (evt.player instanceof EntityPlayerMP) FlimFlamEnchantmentsHandler.deliverKarma((EntityPlayerMP)evt.player);
		}
	}
}
