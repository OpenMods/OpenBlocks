package openblocks.rubbish;

import static openmods.utils.CommandUtils.*;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import openblocks.enchantments.FlimFlamEnchantmentsHandler;

import com.google.common.collect.Lists;

public class CommandLuck implements ICommand {

	private static final String NAME = "luck";

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
		return NAME + " <player> [<amount>]";
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) {
		if (params.length < 1) throw error("openblocks.misc.command.invalid");

		String playerName = params[0];
		EntityPlayer player = getPlayer(sender, playerName);

		if (params.length == 1) {
			int result = FlimFlamEnchantmentsHandler.getLuck(player);
			respond(sender, "openblocks.misc.command.luck_current", playerName, result);
		} else if (params.length == 2) {
			int amount;
			try {
				amount = Integer.parseInt(params[1]);
			} catch (NumberFormatException e) {
				throw error("openblocks.misc.command.invalid");
			}

			int result = FlimFlamEnchantmentsHandler.modifyLuck(player, amount);
			respond(sender, "openblocks.misc.command.luck_added", playerName, result);
		} else throw error("openblocks.misc.command.invalid");
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender.canCommandSenderUseCommand(4, NAME); // OP
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List addTabCompletionOptions(ICommandSender sender, String[] params) {
		if (params.length == 1) {
			String playerPrefix = params[0];
			return fiterPlayerNames(playerPrefix);
		}
		return Lists.newArrayList();
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return i == 0;
	}

}
