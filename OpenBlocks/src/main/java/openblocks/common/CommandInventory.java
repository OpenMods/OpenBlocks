package openblocks.common;

import static openmods.utils.CommandUtils.filterPrefixes;
import static openmods.utils.CommandUtils.fiterPlayerNames;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import openblocks.api.InventoryEvent.SubInventory;
import openblocks.common.PlayerInventoryStore.LoadedInventories;
import openmods.Log;
import openmods.utils.BlockUtils;
import openmods.utils.InventoryUtils;

public class CommandInventory implements ICommand {

	private static final String COMMAND_RESTORE = "restore";

	private static final String COMMAND_SPAWN = "spawn";

	private static final String COMMAND_STORE = "store";

	private static final String NAME = "ob_inventory";

	private static final String ID_MAIN_INVENTORY = "main";

	private static final List<String> SUB_COMMANDS = Lists.newArrayList(COMMAND_STORE, COMMAND_RESTORE, COMMAND_SPAWN);

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
		return NAME + " store <player> OR " +
				NAME + " restore <player> <file without 'inventory-' and '.dat'> OR " +
				NAME + " spawn <file without 'inventory-' and '.dat'> [<sub_inventory OR '" + ID_MAIN_INVENTORY + "'>,  [<index of item>]]";
	}

	@Override
	public List<String> getAliases() {
		return Collections.emptyList();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) throw new SyntaxErrorException();

		String subCommand = args[0];

		if (subCommand.equalsIgnoreCase(COMMAND_RESTORE)) {
			if (args.length != 3) throw new SyntaxErrorException();
			String playerName = args[1];
			String id = args[2];
			ServerPlayerEntity player = CommandBase.getPlayer(server, sender, playerName);

			final boolean success;
			try {
				success = PlayerInventoryStore.instance.restoreInventory(player, id);
			} catch (Exception e) {
				Log.warn(e, "Failed to restore inventory, player %s, file %s", playerName, id);
				throw new CommandException("openblocks.misc.cant_restore_player", playerName);
			}

			if (success) sender.sendMessage(new TranslationTextComponent("openblocks.misc.restored_inventory", playerName));
			else throw new CommandException("openblocks.misc.cant_restore_player", playerName);

		} else if (subCommand.equalsIgnoreCase(COMMAND_STORE)) {
			if (args.length != 2) throw new SyntaxErrorException();
			String playerName = args[1];
			ServerPlayerEntity player = CommandBase.getPlayer(server, sender, playerName);
			try {
				File result = PlayerInventoryStore.instance.storePlayerInventory(player, "command");
				sender.sendMessage(new TranslationTextComponent(
						"openblocks.misc.stored_inventory",
						result.getAbsolutePath()));
			} catch (Exception e) {
				Log.warn(e, "Failed to store inventory, player %s, file %s", playerName);
				throw new CommandException("openblocks.misc.cant_store", playerName);
			}
		} else if (subCommand.equalsIgnoreCase(COMMAND_SPAWN)) {
			if (args.length != 2 && args.length != 3 && args.length != 4) throw new SyntaxErrorException();
			final String id = args[1];

			final String target = (args.length > 2)? args[2] : ID_MAIN_INVENTORY;

			LoadedInventories loadedInventories = loadInventories(sender, id);
			if (loadedInventories == null) throw new CommandException("openblocks.misc.cant_restore_inventory");

			final List<ItemStack> toRestore;

			if (ID_MAIN_INVENTORY.equals(target)) {
				final IInventory inventory = loadedInventories.mainInventory;
				if (inventory == null) throw new CommandException("openblocks.misc.cant_restore_inventory");

				if (args.length == 4) {
					final int item = getSlotId(args[3]);
					final ItemStack stack = inventory.getStackInSlot(item);
					if (stack.isEmpty()) throw new CommandException("openblocks.misc.empty_slot");
					toRestore = Lists.newArrayList(stack);
				} else {
					toRestore = InventoryUtils.getInventoryContents(inventory);
				}
			} else {
				SubInventory inventory = loadedInventories.subInventories.get(target);
				if (inventory == null) throw new CommandException("openblocks.misc.invalid_sub_inventory", target);

				if (args.length == 4) {
					final int item = getSlotId(args[3]);
					final ItemStack stack = inventory.getItemStack(item);
					if (stack.isEmpty()) throw new CommandException("openblocks.misc.empty_slot");
					toRestore = Lists.newArrayList(stack);
				} else {
					toRestore = Lists.newArrayList(inventory.asMap().values());
				}
			}

			final BlockPos coords = sender.getPosition();
			for (ItemStack stack : toRestore)
				if (!stack.isEmpty()) BlockUtils.dropItemStackInWorld(sender.getEntityWorld(), coords, stack);

		} else throw new SyntaxErrorException();
	}

	private static LoadedInventories loadInventories(ICommandSender sender, String id) throws CommandException {
		try {
			return PlayerInventoryStore.instance.loadInventories(sender.getEntityWorld(), id);
		} catch (Exception e) {
			Log.warn(e, "Failed to restore inventory, file %s", id);
			throw new CommandException("openblocks.misc.cant_restore_inventory");
		}
	}

	private static int getSlotId(String item) throws CommandException {
		try {
			return Integer.parseInt(item);
		} catch (Exception t) {
			throw new CommandException("openblocks.misc.invalid_index");
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canUseCommand(4, NAME);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		if (args.length == 0) return Collections.emptyList();
		if (args.length == 1) return filterPrefixes(args[0], SUB_COMMANDS);

		final String subCommand = args[0];

		if (subCommand.equals(COMMAND_SPAWN)) {
			if (args.length == 2) return PlayerInventoryStore.instance.getMatchedDumps(sender.getEntityWorld(), args[1]);

			if (args.length == 3) {
				final String fileId = args[1];
				try {
					LoadedInventories inventories = PlayerInventoryStore.instance.loadInventories(sender.getEntityWorld(), fileId);
					if (inventories == null) return Collections.emptyList();
					List<String> result = Lists.newArrayList(ID_MAIN_INVENTORY);
					result.addAll(inventories.subInventories.keySet());
					return filterPrefixes(args[2], result);
				} catch (Exception e) {
					// just ignore, don't spam
				}
			}
		} else {
			if (args.length == 2) return fiterPlayerNames(server, args[1]);

			if (args.length == 3) {
				String prefix = args[2];
				if (subCommand.equalsIgnoreCase(COMMAND_RESTORE)) return PlayerInventoryStore.instance.getMatchedDumps(sender.getEntityWorld(), prefix);
			}
		}

		return Collections.emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		if (args.length > 1 && args[0].equals(COMMAND_SPAWN)) return false;
		return index == 1;
	}

}
