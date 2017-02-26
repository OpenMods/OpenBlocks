package openblocks.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import openblocks.Config;
import openblocks.api.InventoryEvent;
import openblocks.api.InventoryEvent.SubInventory;
import openmods.Log;
import openmods.inventory.GenericInventory;
import openmods.utils.ItemUtils;
import openmods.utils.TagUtils;
import org.apache.commons.lang3.StringUtils;

public class PlayerInventoryStore {

	public static final String TAG_PLAYER_UUID = "PlayerUUID";

	public static final String TAG_PLAYER_NAME = "PlayerName";

	private static final String TAG_LOCATION = "Location";

	private static final String TAG_INVENTORY = "Inventory";

	private static final String TAG_SUB_INVENTORIES = "SubInventories";

	private static final String TAG_SLOT = "Slot";

	private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	private static final Pattern SAFE_CHARS = Pattern.compile("[^A-Za-z0-9_-]");

	private static final String PREFIX = "inventory-";

	private PlayerInventoryStore() {}

	public static final PlayerInventoryStore instance = new PlayerInventoryStore();

	public interface ExtrasFiller {
		public void addExtras(NBTTagCompound meta);
	}

	public static class LoadedInventories {

		public final IInventory mainInventory;

		public Map<String, SubInventory> subInventories;

		private LoadedInventories(IInventory mainInventory, Map<String, SubInventory> subInventories) {
			this.mainInventory = mainInventory;
			this.subInventories = subInventories;
		}
	}

	private synchronized File getNewDumpFile(Date date, String player, World world, String type) {
		String dateStr = formatter.format(date);

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

	public File storePlayerInventory(final EntityPlayer player, String type) {
		final InventoryEvent.Store evt = new InventoryEvent.Store(player);
		MinecraftForge.EVENT_BUS.post(evt);

		final GameProfile profile = player.getGameProfile();
		return storeInventory(player.inventory, profile.getName(), type, player.worldObj,
				createExtrasFiller(profile, player.posX, player.posY, player.posZ, evt.getSubInventories()));

	}

	private static ExtrasFiller createExtrasFiller(final GameProfile profile, final double x, final double y, final double z, final Map<String, SubInventory> subs) {
		return new ExtrasFiller() {
			@Override
			public void addExtras(NBTTagCompound meta) {
				meta.setString(TAG_PLAYER_NAME, profile.getName());
				meta.setString(TAG_PLAYER_UUID, profile.getId().toString());

				meta.setTag(TAG_LOCATION, TagUtils.store(x, y, z));

				NBTTagCompound subInventories = new NBTTagCompound();

				for (Map.Entry<String, SubInventory> e : subs.entrySet()) {
					NBTTagList subInventory = new NBTTagList();

					for (Map.Entry<Integer, ItemStack> ie : e.getValue().asMap().entrySet()) {
						ItemStack stack = ie.getValue();
						if (stack != null) {
							NBTTagCompound stacktag = ItemUtils.writeStack(stack);
							stacktag.setInteger(TAG_SLOT, ie.getKey());
							subInventory.appendTag(stacktag);
						}
					}

					subInventories.setTag(e.getKey(), subInventory);
				}

				meta.setTag(TAG_SUB_INVENTORIES, subInventories);
			}
		};
	}

	public File storeInventory(IInventory inventory, String name, String type, World world, ExtrasFiller filler) {
		GenericInventory copy = new GenericInventory("tmp", false, inventory.getSizeInventory());
		copy.copyFrom(inventory);

		Date now = new Date();

		Matcher matcher = SAFE_CHARS.matcher(name);
		String playerName = matcher.replaceAll("_");
		File dumpFile = getNewDumpFile(now, playerName, world, type);

		NBTTagCompound root = new NBTTagCompound();

		{
			final NBTTagCompound invData = new NBTTagCompound();
			copy.writeToNBT(invData);
			root.setTag(TAG_INVENTORY, invData);
		}

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
			Log.warn(e, "Failed to dump data for player %s, file %s", name, dumpFile.getAbsoluteFile());
		}

		return dumpFile;
	}

	private static IInventory loadInventory(NBTTagCompound rootTag) {
		if (!rootTag.hasKey(TAG_INVENTORY, Constants.NBT.TAG_COMPOUND)) {
			Log.debug("No main inventory found");
			return null;
		}

		NBTTagCompound invTag = rootTag.getCompoundTag(TAG_INVENTORY);
		GenericInventory result = new GenericInventory("tmp", false, 0);
		result.readFromNBT(invTag);
		return result;
	}

	private static SubInventory loadSubInventory(NBTTagList subTag) {
		SubInventory result = new SubInventory();

		for (int i = 0; i < subTag.tagCount(); i++) {
			NBTTagCompound itemTag = subTag.getCompoundTagAt(i);

			if (!itemTag.hasNoTags()) {
				int slot = itemTag.getInteger(TAG_SLOT);
				ItemStack stack = ItemUtils.readStack(itemTag);
				if (stack != null) result.addItemStack(slot, stack);
			}
		}

		return result;
	}

	private static Map<String, SubInventory> loadSubInventories(NBTTagCompound subsTag) {
		Map<String, SubInventory> result = Maps.newHashMap();

		@SuppressWarnings("unchecked")
		final Set<String> keys = subsTag.func_150296_c();
		for (String key : keys) {
			NBTTagList subTag = subsTag.getTagList(key, Constants.NBT.TAG_COMPOUND);
			final SubInventory sub = loadSubInventory(subTag);
			result.put(key, sub);
		}

		return result;
	}

	private static NBTTagCompound loadInventoryTag(World world, String fileId) {
		File file = world.getSaveHandler().getMapFileFromName(PREFIX + stripFilename(fileId));

		try {
			InputStream stream = new FileInputStream(file);
			try {
				return CompressedStreamTools.readCompressed(stream);
			} finally {
				stream.close();
			}
		} catch (IOException e) {
			Log.warn(e, "Failed to read data from file %s", file.getAbsoluteFile());
			return null;
		}
	}

	public List<String> getMatchedDumps(World world, String prefix) {
		File saveFolder = getSaveFolder(world);
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

	public static File getSaveFolder(World world) {
		File dummy = world.getSaveHandler().getMapFileFromName("dummy");
		return dummy.getParentFile();
	}

	public LoadedInventories loadInventories(World world, String fileId) {
		final NBTTagCompound rootTag = loadInventoryTag(world, fileId);
		if (rootTag == null) return null;

		IInventory mainInventory = loadInventory(rootTag);

		final Map<String, SubInventory> subInventories;
		if (rootTag.hasKey(TAG_SUB_INVENTORIES, Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound subsTag = rootTag.getCompoundTag(TAG_SUB_INVENTORIES);
			subInventories = loadSubInventories(subsTag);
		} else {
			subInventories = Maps.newHashMap();
		}

		return new LoadedInventories(mainInventory, subInventories);
	}

	public boolean restoreInventory(EntityPlayer player, String fileId) {
		final LoadedInventories inventories = loadInventories(player.worldObj, fileId);
		if (inventories == null) return false;

		final IInventory main = inventories.mainInventory;
		if (main != null) {
			InventoryPlayer current = player.inventory;
			final int targetInventorySize = current.getSizeInventory();
			final int sourceInventorySize = main.getSizeInventory();

			for (int i = 0; i < sourceInventorySize; i++) {
				final ItemStack stack = main.getStackInSlot(i);
				if (i < targetInventorySize) current.setInventorySlotContents(i, stack);
				else player.dropPlayerItemWithRandomChoice(stack, false);
			}
		}

		final Map<String, SubInventory> subs = inventories.subInventories;
		if (subs != null) {
			MinecraftForge.EVENT_BUS.post(new InventoryEvent.Load(player, subs));
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
