package openblocks.rubbish;

import static openmods.utils.CommandUtils.error;
import static openmods.utils.CommandUtils.filterPrefixes;
import static openmods.utils.CommandUtils.fiterPlayerNames;
import static openmods.utils.CommandUtils.respond;

import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import openblocks.api.IFlimFlamDescription;
import openblocks.enchantments.flimflams.FlimFlamRegistry;
import openmods.utils.CollectionUtils;

public class CommandFlimFlam implements ICommand {

	private static final String NAME = "flimflam";

	@Override
	public int compareTo(ICommand o) {
		return NAME.compareTo(o.getName());
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getUsage(ICommandSender icommandsender) {
		return NAME + " <player> [<effect>]";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
		if (params.length != 1 && params.length != 2) throw error("openblocks.misc.command.invalid");

		String playerName = params[0];
		ServerPlayerEntity player = CommandBase.getPlayer(server, sender, playerName);

		String effectName = (params.length > 1)? params[1] : null;

		IFlimFlamDescription meta;
		if (effectName == null) {
			meta = CollectionUtils.getRandom(FlimFlamRegistry.instance.getFlimFlams());
			effectName = meta.name();
		} else {
			meta = FlimFlamRegistry.instance.getFlimFlamByName(effectName);
			if (meta == null) throw error("openblocks.misc.command.no_flim_flam");
		}

		if (meta.action().execute(player)) {
			respond(sender, "openblocks.misc.command.flim_flam_source", playerName, effectName);
			if (!player.equals(sender)) respond(player, "openblocks.misc.command.flim_flam_target");
		} else throw error("openblocks.misc.command.flim_flam_failed");
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canUseCommand(4, NAME); // OP
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] params, BlockPos pos) {
		if (params.length == 1) {
			String playerPrefix = params[0];
			return fiterPlayerNames(server, playerPrefix);
		}

		if (params.length == 2) {
			String effectPrefix = params[1];
			return filterPrefixes(effectPrefix, FlimFlamRegistry.instance.getAllFlimFlamsNames());
		}

		return Collections.emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return i == 0;
	}

}
