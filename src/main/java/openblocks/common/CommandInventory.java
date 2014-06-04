package openblocks.common;

import static openmods.utils.CommandUtils.filterPrefixes;
import static openmods.utils.CommandUtils.fiterPlayerNames;
import static openmods.utils.CommandUtils.getPlayer;

import java.io.File;
import java.util.List;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import openmods.Log;

import com.google.common.collect.Lists;

public class CommandInventory implements ICommand {

	private static final String COMMAND_RESTORE = "restore";

	private static final String COMMAND_STORE = "store";

	private static final String NAME = "ob_inventory";

	private static final List<String> SUB_COMMANDS = Lists.newArrayList(COMMAND_STORE, COMMAND_RESTORE);

	@Override
	public int compareTo(Object o) {
		return NAME.compareTo(((ICommand)o).getCommandName());
	}

	@Override
	public String getCommandName() {
		return NAME;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return NAME + " store <player> OR " +
				NAME + " restore <file>";
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length < 2) throw new SyntaxErrorException();

		String subCommand = args[0];
		String playerName = args[1];
		EntityPlayerMP player = getPlayer(sender, playerName);

		if (subCommand.equalsIgnoreCase(COMMAND_RESTORE)) {
			if (args.length != 3) throw new SyntaxErrorException();
			String id = args[2];
			try {
				if (PlayerInventoryStore.instance.restoreInventory(player, id)) {
					sender.addChatMessage(new ChatComponentTranslation("openblocks.misc.restored_inventory", playerName));
				} else throw new CommandException("openblocks.misc.cant_restore", playerName);
			} catch (Exception e) {
				Log.warn(e, "Failed to restore inventory, player %s, file %s", playerName, id);
				throw new CommandException("openblocks.misc.cant_restore", playerName);
			}
		} else if (subCommand.equalsIgnoreCase(COMMAND_STORE)) {
			try {
				File result = PlayerInventoryStore.instance.storePlayerInventory(player);
				sender.addChatMessage(new ChatComponentTranslation(
						"openblocks.misc.stored_inventory",
						result.getAbsolutePath()));
			} catch (Exception e) {
				Log.warn(e, "Failed to store inventory, player %s, file %s", playerName);
				throw new CommandException("openblocks.misc.cant_Store", playerName);
			}
		} else throw new SyntaxErrorException();
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender.canCommandSenderUseCommand(4, NAME);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) return filterPrefixes(args[0], SUB_COMMANDS);

		if (args.length == 2) return fiterPlayerNames(args[1]);

		if (args.length == 3) {
			String subCommand = args[0];
			String prefix = args[2];
			if (subCommand.equalsIgnoreCase(COMMAND_RESTORE)) return PlayerInventoryStore.instance.getMatchedDumps(sender.getEntityWorld(), prefix);
		}

		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 1;
	}

}
