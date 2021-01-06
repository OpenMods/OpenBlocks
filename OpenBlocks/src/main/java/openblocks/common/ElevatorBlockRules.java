package openblocks.common;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import openblocks.Config;
import openblocks.api.ElevatorCheckEvent;
import openblocks.api.IElevatorBlock.PlayerRotation;
import openmods.Log;
import openmods.colors.ColorMeta;
import openmods.config.properties.ConfigurationChange;

public class ElevatorBlockRules {
	public final static ElevatorBlockRules instance = new ElevatorBlockRules();

	private ElevatorBlockRules() {
	}

	public enum Action {
		IGNORE,
		ABORT,
		INCREMENT
	}

	private static class ElevatorOverride {
		public final DyeColor color;
		public final PlayerRotation rotation;

		public ElevatorOverride(DyeColor color, PlayerRotation rotation) {
			this.color = color;
			this.rotation = rotation;
		}
	}

	private static final Map<String, Action> ACTIONS;

	static {
		ImmutableMap.Builder<String, Action> builder = ImmutableMap.builder();
		for (Action a : Action.values()) {
			builder.put(a.name().toLowerCase(Locale.ENGLISH), a);
		}
		ACTIONS = builder.build();
	}

	@Nullable
	private Map<Block, Action> rules;

	@Nullable
	private Map<BlockState, ElevatorOverride> overrides;

	private Map<Block, Action> getRules() {
		if (rules == null) {
			rules = Maps.newIdentityHashMap();
			for (String entry : Config.elevatorRules) {
				try {
					tryAddRule(rules, entry);
				} catch (Throwable t) {
					Log.warn(t, "Invalid entry in elevator actions: %s", entry);
				}
			}
		}

		return rules;
	}

	private static void tryAddRule(Map<Block, Action> rules, String entry) {
		String[] parts = entry.split(":");
		if (parts.length == 0) {
			return;
		}
		Preconditions.checkState(parts.length == 3, "Each entry must have exactly 3 colon-separated fields");
		final String modId = parts[0];
		final String blockName = parts[1];
		final String actionName = parts[2].toLowerCase(Locale.ENGLISH);
		final Action action = ACTIONS.get(actionName);

		Preconditions.checkNotNull(action, "Unknown action: %s", actionName);

		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(modId, blockName));

		if (block != Blocks.AIR) {
			rules.put(block, action);
		} else {
			Log.warn("Can't find block %s", entry);
		}
	}

	private Map<BlockState, ElevatorOverride> getOverrides() {
		if (overrides == null) {
			overrides = Maps.newIdentityHashMap();
			for (String entry : Config.elevatorOverrides) {
				try {
					tryAddOverride(overrides, entry);
				} catch (Throwable t) {
					Log.warn(t, "Invalid entry in elevator overrides: %s", entry);
				}
			}
		}

		return overrides;
	}

	private static void tryAddOverride(Map<BlockState, ElevatorOverride> overrides, String entry) throws Exception {
		final List<String> parts = Splitter.on(';').splitToList(entry);
		if (parts.size() == 0) {
			return;
		}

		Preconditions.checkState(parts.size() == 1 || parts.size() == 2, "Each entry be either 'blockState' or 'blockState=color'");

		final List<BlockState> blockStates = parseBlockDesc(parts.get(0));

		final DyeColor color;
		if (parts.size() == 2) {
			final ColorMeta colorMeta = ColorMeta.fromId(parts.get(1));
			color = colorMeta.vanillaEnum;
		} else {
			color = DyeColor.WHITE;
		}

		for (BlockState state : blockStates) {
			overrides.put(state, new ElevatorOverride(color, PlayerRotation.NONE));
		}

	}

	private static List<BlockState> parseBlockDesc(String blockDesc) {
		BlockStateParser result;
		try {
			result = new BlockStateParser(new StringReader(blockDesc), false).parse(false);
		} catch (CommandSyntaxException e) {
			Log.warn("Can't find block %s", blockDesc);
			return Collections.emptyList();
		}
		if (result.getProperties().isEmpty()) {
			return result.getState().getBlock().getStateContainer().getValidStates();
		} else {
			return ImmutableList.of(result.getState());
		}
	}

	@SubscribeEvent
	public void onReconfig(ConfigurationChange.Post evt) {
		if (evt.check("dropblock", "specialBlockRules")) {
			rules = null;
		}
		if (evt.check("dropblock", "overrides")) {
			overrides = null;
		}
	}

	private static boolean isPassable(IBlockReader world, BlockPos pos, BlockState state) {
		return Config.elevatorIgnoreHalfBlocks && !state.isNormalCube(world, pos);
	}

	public Action getActionForBlock(IBlockReader world, BlockPos pos, BlockState state) {
		Action action = getRules().get(state.getBlock());
		if (action != null) {
			return action;
		}

		return isPassable(world, pos, state)? Action.IGNORE : Action.INCREMENT;
	}

	public void configureEvent(ElevatorCheckEvent evt) {
		final Map<BlockState, ElevatorOverride> overrides = getOverrides();
		final ElevatorOverride elevatorOverride = overrides.get(evt.getState());
		if (elevatorOverride != null) {
			evt.setColor(elevatorOverride.color);
			evt.setRotation(elevatorOverride.rotation);
		}
	}

}
