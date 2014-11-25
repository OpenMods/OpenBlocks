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
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import openblocks.Config;
import openmods.Log;
import openmods.inventory.GenericInventory;

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

	private static File getNewDumpFile(Date date, String player, World world) {
		String dateStr = FORMATTER.format(date);

		int id = 0;
		while (true) {
			String filename = String.format(PREFIX + "%s-%s-%d", player, dateStr, id);
			File file = world.getSaveHandler().getMapFileFromName(filename);
			if (!file.exists()) return file;
			id++;
		}
	}

	private static String stripFilename(String name) {
		return StringUtils.removeEndIgnoreCase(StringUtils.removeStartIgnoreCase(name, PREFIX), ".dat");
	}

	public File storePlayerInventory(EntityPlayer player) {
		InventoryPlayer inv = player.inventory;
		GenericInventory copy = new GenericInventory("tmp", false, inv.getSizeInventory());
		copy.copyFrom(inv);

		Date now = new Date();

		GameProfile profile = player.getGameProfile();
		String name = profile.getName();
		Matcher matcher = SAFE_CHARS.matcher(name);
		String playerName = matcher.replaceAll("_");
		File dumpFile = getNewDumpFile(now, playerName, player.worldObj);

		NBTTagCompound invData = new NBTTagCompound();
		copy.writeToNBT(invData);

		NBTTagCompound root = new NBTTagCompound();
		root.setTag(TAG_INVENTORY, invData);

		root.setLong("Created", now.getTime());
		root.setString("PlayerName", name);
		root.setString("PlayerUUID", profile.getId().toString());

		NBTTagCompound location = new NBTTagCompound();
		location.setDouble("X", player.posX);
		location.setDouble("Y", player.posY);
		location.setDouble("Z", player.posZ);
		root.setTag("Location", location);

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

	private static IInventory loadInventory(World world, String fileId) {
		File file = world.getSaveHandler().getMapFileFromName(PREFIX + fileId);

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
		fileId = stripFilename(fileId);

		IInventory restored = loadInventory(player.worldObj, fileId);
		if (restored == null) return false;

		InventoryPlayer current = player.inventory;
		if (current.getSizeInventory() < restored.getSizeInventory()) {
			Log.info("Target inventory too small, %d < %d", current.getSizeInventory(), restored.getSizeInventory());
			return false;
		}

		for (int i = 0; i < restored.getSizeInventory(); i++)
			current.setInventorySlotContents(i, restored.getStackInSlot(i));

		return true;
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onPlayerDeath(LivingDeathEvent event) {
		if (Config.dumpStiffsStuff && (event.entity instanceof EntityPlayerMP) && !(event.entity instanceof FakePlayer)) {
			EntityPlayer player = (EntityPlayer)event.entity;
			final String playerName = player.getDisplayName();
			try {

				File file = storePlayerInventory(player);
				Log.info("Storing post-mortem inventory into %s. It can be restored with command '/ob_inventory restore %s %s'",
						file.getAbsolutePath(), playerName, stripFilename(file.getName()));
			} catch (Exception e) {
				Log.severe(e, "Failed to store inventory for player %s", playerName);
			}
		}
	}

}
