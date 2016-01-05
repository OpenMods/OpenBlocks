package openblocks.rubbish;

import static openmods.utils.CommandUtils.*;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import openblocks.api.IFlimFlamDescription;
import openblocks.enchantments.flimflams.FlimFlamRegistry;
import openmods.utils.CollectionUtils;

public class CommandFlimFlam implements ICommand {

	private static final String NAME = "flimflam";

	@Override
	public int compareTo(ICommand o) {
		return NAME.compareTo(o.getCommandName());
	}

	@Override
	public String getCommandName() {
		return NAME;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return NAME + " <player> [<effect>]";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.emptyList();
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) throws CommandException {
		if (params.length != 1 && params.length != 2) throw error("openblocks.misc.command.invalid");

		String playerName = params[0];
		EntityPlayerMP player = getPlayer(sender, playerName);

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
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender.canCommandSenderUseCommand(4, NAME); // OP
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] params, BlockPos pos) {
		if (params.length == 1) {
			String playerPrefix = params[0];
			return fiterPlayerNames(playerPrefix);
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
