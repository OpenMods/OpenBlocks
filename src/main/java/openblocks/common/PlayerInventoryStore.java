package openblocks.common;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import openblocks.Config;
import openmods.Log;
import openmods.inventory.GenericInventory;
import openmods.utils.TagUtils;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlayerInventoryStore {

	private static final String TAG_INVENTORY = "Inventory";

	private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	private static final Pattern SAFE_CHARS = Pattern.compile("[^A-Za-z0-9_-]");

	private static final String PREFIX = "inventory-";

	private PlayerInventoryStore() {}

	public static final PlayerInventoryStore instance = new PlayerInventoryStore();

	public interface ExtrasFiller {
		public void addExtras(NBTTagCompound meta);
	}

	private static File getNewDumpFile(Date date, String player, World world, String type) {
		String dateStr = FORMATTER.format(date);

		int id = 0;
		while (true) {
			String filename = String.format(PREFIX + "%s-%s-%s-%d", player, dateStr, type, id);
			File file = world.getSaveHandler().getMapFileFromName(filename);
			if (!file.exists()) return file;
			id++;
		}
	}

	private static String stripFilename(String name) {
		return StringUtils.removeEndIgnoreCase(StringUtils.removeStartIgnoreCase(name, PREFIX), ".dat");
	}

	public static ExtrasFiller createDefaultExtrasFiller(final GameProfile profile, final double x, final double y, final double z) {
		return new ExtrasFiller() {
			@Override
			public void addExtras(NBTTagCompound meta) {
				meta.setString("PlayerName", profile.getName());
				meta.setString("PlayerUUID", profile.getId().toString());

				meta.setTag("Location", TagUtils.store(x, y, z));
			}
		};
	}

	public File storePlayerInventory(final EntityPlayer player, String type) {
		final GameProfile profile = player.getGameProfile();
		return storeInventory(player.inventory, profile.getName(), type, player.worldObj,
				createDefaultExtrasFiller(profile, player.posX, player.posY, player.posZ));

	}

	public File storeInventory(IInventory inventory, String name, String type, World world, ExtrasFiller filler) {
		GenericInventory copy = new GenericInventory("tmp", false, inventory.getSizeInventory());
		copy.copyFrom(inventory);

		Date now = new Date();

		Matcher matcher = SAFE_CHARS.matcher(name);
		String playerName = matcher.replaceAll("_");
		File dumpFile = getNewDumpFile(now, playerName, world, type);

		NBTTagCompound invData = new NBTTagCompound();
		copy.writeToNBT(invData);

		NBTTagCompound root = new NBTTagCompound();
		root.setTag(TAG_INVENTORY, invData);

		root.setLong("Created", now.getTime());
		root.setString("Type", type);
		filler.addExtras(root);

		try {
			OutputStream stream = new FileOutputStream(dumpFile);
			try {
				CompressedStreamTools.writeCompressed(root, stream);
			} finally {
				stream.close();
			}
		} catch (IOException e) {
			Log.warn("Failed to dump data for player %s, file %s", name, dumpFile.getAbsoluteFile());
		}

		return dumpFile;
	}

	public static IInventory loadInventory(World world, String fileId) {
		File file = world.getSaveHandler().getMapFileFromName(PREFIX + stripFilename(fileId));

		NBTTagCompound tag;
		try {
			InputStream stream = new FileInputStream(file);
			try {
				tag = CompressedStreamTools.readCompressed(stream);
			} finally {
				stream.close();
			}
		} catch (IOException e) {
			Log.warn("Failed to read data from file %s", file.getAbsoluteFile());
			return null;
		}

		if (!tag.hasKey(TAG_INVENTORY)) return null;

		NBTTagCompound invTag = tag.getCompoundTag(TAG_INVENTORY);
		GenericInventory result = new GenericInventory("tmp", false, 0);
		result.readFromNBT(invTag);
		return result;
	}

	public List<String> getMatchedDumps(World world, String prefix) {
		File dummy = world.getSaveHandler().getMapFileFromName("dummy");
		File saveFolder = dummy.getParentFile();
		final String actualPrefix = StringUtils.startsWithIgnoreCase(prefix, PREFIX)? prefix : PREFIX + prefix;
		File[] files = saveFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(actualPrefix);
			}
		});

		List<String> result = Lists.newArrayList();
		int toCut = PREFIX.length();

		for (File f : files) {
			String name = f.getName();
			result.add(name.substring(toCut, name.length() - 4));
		}

		return result;
	}

	public boolean restoreInventory(EntityPlayer player, String fileId) {
		IInventory restored = loadInventory(player.worldObj, fileId);

		InventoryPlayer current = player.inventory;
		final int targetInventorySize = current.getSizeInventory();
		final int sourceInventorySize = restored.getSizeInventory();

		for (int i = 0; i < sourceInventorySize; i++) {
			final ItemStack stack = restored.getStackInSlot(i);
			if (i < targetInventorySize) current.setInventorySlotContents(i, stack);
			else player.dropPlayerItemWithRandomChoice(stack, false);
		}

		return true;
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onPlayerDeath(LivingDeathEvent event) {
		if (Config.dumpStiffsStuff && (event.entity instanceof EntityPlayerMP) && !(event.entity instanceof FakePlayer)) {
			EntityPlayer player = (EntityPlayer)event.entity;
			final String playerName = player.getDisplayName();
			try {

				File file = storePlayerInventory(player, "death");
				Log.info("Storing post-mortem inventory into %s. It can be restored with command '/ob_inventory restore %s %s'",
						file.getAbsolutePath(), playerName, stripFilename(file.getName()));
			} catch (Exception e) {
				Log.severe(e, "Failed to store inventory for player %s", playerName);
			}
		}
	}

}
