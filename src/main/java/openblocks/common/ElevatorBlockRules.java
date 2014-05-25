package openblocks.common;

import java.util.Map;

import net.minecraft.block.Block;
import openblocks.Config;
import openmods.Log;
import openmods.config.ConfigurationChange;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.registry.GameRegistry;

public class ElevatorBlockRules {

	public final static ElevatorBlockRules instance = new ElevatorBlockRules();

	private ElevatorBlockRules() {}

	public enum Action {
		IGNORE,
		ABORT,
		INCREMENT
	}

	private static final Map<String, Action> ACTIONS;

	static {
		ImmutableMap.Builder<String, Action> builder = ImmutableMap.builder();
		for (Action a : Action.values())
			builder.put(a.name().toLowerCase(), a);
		ACTIONS = builder.build();
	}

	private Map<Block, Action> rules;

	private Map<Block, Action> getRules() {
		if (rules == null) {
			rules = Maps.newIdentityHashMap();
			for (String entry : Config.elevatorRules) {
				try {
					tryAddRule(rules, entry);
				} catch (Throwable t) {
					Log.warn(t, "Invalid entry in map blacklist: %s", entry);
				}
			}
		}

		return rules;
	}

	private static void tryAddRule(Map<Block, Action> rules, String entry) {
		String[] parts = entry.split(":");
		if (parts.length == 0) return;
		Preconditions.checkState(parts.length == 3, "Each entry must have exactly 3 colon-separated fields");
		final String modId = parts[0];
		final String blockName = parts[1];
		final String actionName = parts[2].toLowerCase();
		final Action action = ACTIONS.get(actionName);

		Preconditions.checkNotNull(action, "Unknown action: %s", actionName);

		Block block = null;
		if ("id".equalsIgnoreCase(modId)) {
			int blockId = Integer.parseInt(blockName);
			block = Block.blocksList[blockId];
		} else {
			block = GameRegistry.findBlock(modId, blockName);
		}

		if (block != null) rules.put(block, action);
		else Log.warn("Can't find block %s", entry);
	}

	@ForgeSubscribe
	public void onReconfig(ConfigurationChange.Post evt) {
		if (evt.check("dropblock", "specialBlockRules")) rules = null;
	}

	private static boolean isPassable(int blockId) {
		return Config.elevatorIgnoreHalfBlocks && !Block.isNormalCube(blockId);
	}

	public Action getActionForBlock(Block block) {
		if (block == null) return Action.IGNORE;
		Action action = getRules().get(block);
		if (action != null) return action;

		return isPassable(block.blockID)? Action.IGNORE : Action.INCREMENT;
	}

}
