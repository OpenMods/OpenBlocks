package openblocks.common;

import net.minecraft.world.GameRules;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GameRuleManager {

	public static class GameRule {
		public static final String SPAWN_GRAVES = "openblocks:spawn_graves";
	}

	private static void addRule(GameRules rules, String name, String defaultValue) {
		if (!rules.hasRule(name)) rules.addGameRule(name, defaultValue);
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load evt) {
		final GameRules rules = evt.world.getGameRules();
		addRule(rules, GameRule.SPAWN_GRAVES, "true");
	}
}
