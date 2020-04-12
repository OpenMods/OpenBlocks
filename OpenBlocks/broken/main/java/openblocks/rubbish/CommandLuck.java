package openblocks.rubbish;

import static openmods.utils.CommandUtils.error;
import static openmods.utils.CommandUtils.fiterPlayerNames;
import static openmods.utils.CommandUtils.respond;

import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import openblocks.enchantments.FlimFlamEnchantmentsHandler;

public class CommandLuck implements ICommand {

	private static final String NAME = "luck";

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
		return NAME + " <player> [<amount>]";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {
		if (params.length < 1) throw error("openblocks.misc.command.invalid");

		String playerName = params[0];
		PlayerEntity player = CommandBase.getPlayer(server, sender, playerName);

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
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canUseCommand(4, NAME); // OP
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] params, BlockPos pos) {
		if (params.length == 1) {
			String playerPrefix = params[0];
			return fiterPlayerNames(server, playerPrefix);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		return i == 0;
	}

}
