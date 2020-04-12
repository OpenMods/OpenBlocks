package openblocks.common;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import openblocks.enchantments.FlimFlamEnchantmentsHandler;

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
			if (evt.player instanceof ServerPlayerEntity) FlimFlamEnchantmentsHandler.deliverKarma((ServerPlayerEntity)evt.player);
		}
	}
}
