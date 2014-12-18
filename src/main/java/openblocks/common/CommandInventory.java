package openblocks.common;

import static openmods.utils.CommandUtils.filterPrefixes;
import static openmods.utils.CommandUtils.fiterPlayerNames;
import static openmods.utils.CommandUtils.getPlayer;

import java.io.File;
import java.util.List;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import openmods.Log;
import openmods.inventory.InventoryUtils;
import openmods.utils.BlockUtils;

import com.google.common.collect.Lists;

public class CommandInventory implements ICommand {

	private static final String COMMAND_RESTORE = "restore";

	private static final String COMMAND_SPAWN = "spawn";

	private static final String COMMAND_STORE = "store";

	private static final String NAME = "ob_inventory";

	private static final List<String> SUB_COMMANDS = Lists.newArrayList(COMMAND_STORE, COMMAND_RESTORE, COMMAND_SPAWN);

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
				NAME + " restore <player> <file without 'inventory-' and '.dat'> OR" +
				NAME + " spawn <file without 'inventory-' and '.dat'> OR " +
				NAME + " spawn <file without 'inventory-' and '.dat'> <index of item>";
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List getCommandAliases() {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length < 1) throw new SyntaxErrorException();

		String subCommand = args[0];

		if (subCommand.equalsIgnoreCase(COMMAND_RESTORE)) {
			if (args.length != 3) throw new SyntaxErrorException();
			String playerName = args[1];
			String id = args[2];
			EntityPlayerMP player = getPlayer(sender, playerName);

			final boolean success;
			try {
				success = PlayerInventoryStore.instance.restoreInventory(player, id);
			} catch (Exception e) {
				Log.warn(e, "Failed to restore inventory, player %s, file %s", playerName, id);
				throw new CommandException("openblocks.misc.cant_restore_player", playerName);
			}

			if (success) sender.addChatMessage(new ChatComponentTranslation("openblocks.misc.restored_inventory", playerName));
			else throw new CommandException("openblocks.misc.cant_restore_player", playerName);

		} else if (subCommand.equalsIgnoreCase(COMMAND_STORE)) {
			if (args.length != 2) throw new SyntaxErrorException();
			String playerName = args[1];
			EntityPlayerMP player = getPlayer(sender, playerName);
			try {
				File result = PlayerInventoryStore.instance.storePlayerInventory(player, "command");
				sender.addChatMessage(new ChatComponentTranslation(
						"openblocks.misc.stored_inventory",
						result.getAbsolutePath()));
			} catch (Exception e) {
				Log.warn(e, "Failed to store inventory, player %s, file %s", playerName);
				throw new CommandException("openblocks.misc.cant_store", playerName);
			}
		} else if (subCommand.equalsIgnoreCase(COMMAND_SPAWN)) {
			if (args.length != 2 && args.length != 3) throw new SyntaxErrorException();
			String id = args[1];

			final IInventory inventory;

			try {
				inventory = PlayerInventoryStore.loadInventory(sender.getEntityWorld(), id);
			} catch (Exception e) {
				Log.warn(e, "Failed to restore inventory, file %s", id);
				throw new CommandException("openblocks.misc.cant_restore_inventory");
			}

			if (inventory == null) throw new CommandException("openblocks.misc.cant_restore_inventory");

			final List<ItemStack> toRestore;
			if (args.length == 3) {
				String item = args[2];
				final ItemStack stack;
				try {
					int itemId = Integer.parseInt(item);
					stack = inventory.getStackInSlot(itemId);
				} catch (Exception t) {
					throw new CommandException("openblocks.misc.invalid_index");
				}

				if (stack == null) throw new CommandException("openblocks.misc.empty_slot");
				toRestore = Lists.newArrayList(stack);
			} else {
				toRestore = InventoryUtils.getInventoryContents(inventory);
			}

			final ChunkCoordinates coords = sender.getPlayerCoordinates();
			for (ItemStack stack : toRestore)
				if (stack != null) BlockUtils.dropItemStackInWorld(sender.getEntityWorld(), coords.posX, coords.posY, coords.posZ, stack);

		} else throw new SyntaxErrorException();
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender.canCommandSenderUseCommand(4, NAME);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 0) return null;
		if (args.length == 1) return filterPrefixes(args[0], SUB_COMMANDS);

		final String subCommand = args[0];

		if (subCommand.equals(COMMAND_SPAWN)) {
			if (args.length == 2) return PlayerInventoryStore.instance.getMatchedDumps(sender.getEntityWorld(), args[1]);
		} else {
			if (args.length == 2) return fiterPlayerNames(args[1]);

			if (args.length == 3) {
				String prefix = args[2];
				if (subCommand.equalsIgnoreCase(COMMAND_RESTORE)) return PlayerInventoryStore.instance.getMatchedDumps(sender.getEntityWorld(), prefix);
			}
		}

		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		if (args.length > 1 && args[0].equals(COMMAND_SPAWN)) return false;
		return index == 1;
	}

}
