package openblocks.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.eventhandler.ASMEventHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.IEventListener;
import cpw.mods.fml.common.eventhandler.ListenerList;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.api.GraveDropsEvent;
import openblocks.api.GraveSpawnEvent;
import openblocks.common.GameRuleManager.GameRule;
import openblocks.common.PlayerInventoryStore.ExtrasFiller;
import openblocks.common.tileentity.TileEntityGrave;
import openmods.Log;
import openmods.inventory.GenericInventory;
import openmods.inventory.legacy.ItemDistribution;
import openmods.utils.BlockNotifyFlags;
import openmods.utils.Coord;
import openmods.utils.TagUtils;
import openmods.world.DelayedActionTickHandler;
import org.apache.logging.log4j.Level;

public class PlayerDeathHandler {

	private static final Comparator<Coord> SEARCH_COMPARATOR = new Comparator<Coord>() {

		private int coordSum(Coord c) {
			return Math.abs(c.x) + Math.abs(c.y) + Math.abs(c.z);
		}

		private int coordMax(Coord c) {
			return Math.max(Math.max(Math.abs(c.x), Math.abs(c.y)), Math.abs(c.z));
		}

		@Override
		public int compare(Coord a, Coord b) {
			// first order by Manhattan distance
			int diff = coordSum(a) - coordSum(b);
			if (diff != 0) return diff;

			// then by distance from axis
			return coordMax(b) - coordMax(a);
		}
	};

	private static class SearchOrder implements Iterable<Coord> {
		public final int size;

		private final List<Coord> coords;

		public SearchOrder(int size) {
			this.size = size;

			List<Coord> coords = Lists.newArrayList();

			for (int x = -size; x <= size; x++)
				for (int y = -size; y <= size; y++)
					for (int z = -size; z <= size; z++)
						coords.add(new Coord(x, y, z));

			Collections.sort(coords, SEARCH_COMPARATOR);

			this.coords = ImmutableList.copyOf(coords);
		}

		@Override
		public Iterator<Coord> iterator() {
			return coords.iterator();
		}
	}

	private static SearchOrder searchOrder;

	private static Iterable<Coord> getSearchOrder(int size) {
		if (searchOrder == null || searchOrder.size != size) searchOrder = new SearchOrder(size);
		return searchOrder;
	}

	private abstract static class GravePlacementChecker {
		public boolean canPlace(World world, EntityPlayer player, int x, int y, int z) {
			if (!world.blockExists(x, y, z)) return false;
			if (!world.canMineBlock(player, x, y, z)) return false;

			Block block = world.getBlock(x, y, z);
			return checkBlock(world, x, y, z, block);
		}

		public abstract boolean checkBlock(World world, int x, int y, int z, Block block);
	}

	private static final GravePlacementChecker POLITE = new GravePlacementChecker() {
		@Override
		public boolean checkBlock(World world, int x, int y, int z, Block block) {
			return (block.isAir(world, x, y, z) || block.isReplaceable(world, x, y, z));
		}
	};

	private static final GravePlacementChecker BRUTAL = new GravePlacementChecker() {
		@Override
		public boolean checkBlock(World world, int x, int y, int z, Block block) {
			return block.getBlockHardness(world, x, y, z) >= 0 && world.getTileEntity(x, y, z) == null;
		}
	};

	private static class GraveCallable implements Runnable {

		private final IChatComponent cause;

		private final GameProfile stiffId;

		private final int posX, posY, posZ;

		private final List<EntityItem> loot;

		private final WeakReference<World> world;

		private final WeakReference<EntityPlayer> exPlayer;

		public GraveCallable(World world, EntityPlayer exPlayer, List<EntityItem> loot) {
			this.posX = MathHelper.floor_double(exPlayer.posX);
			this.posY = MathHelper.floor_double(exPlayer.posY);
			this.posZ = MathHelper.floor_double(exPlayer.posZ);

			this.world = new WeakReference<World>(world);

			this.exPlayer = new WeakReference<EntityPlayer>(exPlayer);
			this.stiffId = exPlayer.getGameProfile();

			final IChatComponent day = formatDate(world);
			final IChatComponent deathCause = exPlayer.func_110142_aN().func_151521_b();
			this.cause = new ChatComponentTranslation("openblocks.misc.grave_msg", deathCause, day);

			this.loot = ImmutableList.copyOf(loot);
		}

		private static IChatComponent formatDate(World world) {
			final long time = world.getTotalWorldTime();
			final String day = String.format("%.1f", time / 24000.0);
			final IChatComponent dayComponent = new ChatComponentText(day);
			dayComponent.getChatStyle().setColor(EnumChatFormatting.WHITE).setBold(true);
			return dayComponent;
		}

		private void setCommonStoreInfo(NBTTagCompound meta, boolean placed) {
			meta.setString(PlayerInventoryStore.TAG_PLAYER_NAME, stiffId.getName());
			meta.setString(PlayerInventoryStore.TAG_PLAYER_UUID, stiffId.getId().toString());
			meta.setTag("PlayerLocation", TagUtils.store(posX, posY, posZ));
			meta.setBoolean("Placed", placed);
		}

		private boolean tryPlaceGrave(World world, final int x, final int y, final int z, String gravestoneText, IChatComponent deathMessage) {
			world.setBlock(x, y, z, OpenBlocks.Blocks.grave, 0, BlockNotifyFlags.ALL);
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile == null || !(tile instanceof TileEntityGrave)) {
				Log.warn("Failed to place grave @ %d,%d,%d: invalid tile entity: %s(%s)", x, y, z, tile, tile != null? tile.getClass() : "?");
				return false;
			}

			TileEntityGrave grave = (TileEntityGrave)tile;

			IInventory loot = getLoot();

			if (Config.backupGraves) backupGrave(world, loot, new ExtrasFiller() {
				@Override
				public void addExtras(NBTTagCompound meta) {
					setCommonStoreInfo(meta, true);
					meta.setTag("GraveLocation", TagUtils.store(x, y, z));

				}
			});

			Log.info("Grave for (%s,%s) was spawned at (%d,%d,%d)", stiffId.getId(), stiffId.getName(), x, y, z);

			grave.setUsername(gravestoneText);
			grave.setLoot(loot);
			grave.setDeathMessage(deathMessage);
			return true;
		}

		protected IInventory getLoot() {
			IInventory loot = new GenericInventory("tmpplayer", false, this.loot.size());
			for (EntityItem entityItem : this.loot) {
				ItemStack stack = entityItem.getEntityItem();
				if (stack != null) ItemDistribution.insertItemIntoInventory(loot, stack.copy());
			}
			return loot;
		}

		private boolean trySpawnGrave(EntityPlayer player, World world) {
			final Coord location = findLocation(world, player);

			String gravestoneText = stiffId.getName();
			final GraveSpawnEvent evt = location == null
					? new GraveSpawnEvent(player, loot, gravestoneText, cause)
					: new GraveSpawnEvent(player, location.x, location.y, location.z, loot, gravestoneText, cause);

			if (MinecraftForge.EVENT_BUS.post(evt)) {
				Log.warn("Grave event for player %s cancelled, no grave will spawn", stiffId);
				return false;
			}

			if (!evt.hasLocation()) {
				Log.warn("No location for grave found, no grave will spawn", stiffId);
				return false;
			}

			final int x = evt.getX();
			final int y = evt.getY();
			final int z = evt.getZ();

			Log.log(debugLevel(), "Grave for %s will be spawned at (%d,%d,%d)", stiffId, x, y, z);

			if (Config.graveBase && canSpawnBase(world, player, x, y - 1, z)) {
				world.setBlock(x, y - 1, z, Blocks.dirt);
			}

			return tryPlaceGrave(world, evt.getX(), evt.getY(), evt.getZ(), evt.gravestoneText, evt.clickText);
		}

		private static boolean canSpawnBase(World world, EntityPlayer player, int x, int y, int z) {
			return world.blockExists(x, y, z)
					&& world.getBlock(x, y, z).isAir(world, x, y, z)
					&& world.canMineBlock(player, x, y, z);
		}

		private Coord findLocation(World world, EntityPlayer player, GravePlacementChecker checker) {
			final int correctedY = Config.voidGraves? Math.max(posY, 1) : posY;

			final int searchSize = Config.graveSpawnRange / 2;

			for (Coord c : getSearchOrder(searchSize)) {
				final int x = posX + c.x;
				final int y = correctedY + c.y;
				final int z = posZ + c.z;
				if (checker.canPlace(world, player, x, y, z)) return new Coord(x, y, z);
			}

			return null;
		}

		private Coord findLocation(World world, EntityPlayer player) {
			Coord location = findLocation(world, player, POLITE);
			if (location != null) return location;

			if (Config.destructiveGraves) {
				Log.warn("Failed to place grave for player %s, going berserk", stiffId);
				return findLocation(world, player, BRUTAL);
			}

			return null;
		}

		private void backupGrave(World world, IInventory loot, ExtrasFiller filler) {
			try {
				File backup = PlayerInventoryStore.instance.storeInventory(loot, stiffId.getName(), "grave", world, filler);
				Log.log(debugLevel(), "Grave backup for player %s saved to %s", stiffId, backup);
			} catch (Throwable t) {
				Log.warn("Failed to store grave backup for player %s", stiffId);
			}
		}

		@Override
		public void run() {
			EntityPlayer player = exPlayer.get();
			if (player == null) {
				Log.warn("Lost player while placing player %s grave", stiffId);
				return;
			}

			World world = this.world.get();
			if (world == null) {
				Log.warn("Lost world while placing player %s grave", stiffId);
				return;
			}

			if (!trySpawnGrave(player, world)) {
				if (Config.backupGraves) {
					IInventory loot = getLoot();
					backupGrave(world, loot, new ExtrasFiller() {
						@Override
						public void addExtras(NBTTagCompound meta) {
							setCommonStoreInfo(meta, false);
						}
					});
				}

				for (EntityItem drop : loot)
					world.spawnEntityInWorld(drop);
			}
		}
	}

	private static Level debugLevel() {
		return Config.debugGraves? Level.INFO : Level.DEBUG;
	}

	@SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
	public void onPlayerDrops(PlayerDropsEvent event) {
		World world = event.entityPlayer.worldObj;
		if (world.isRemote) return;

		if (Config.debugGraves) dumpDebugInfo(event);

		final EntityPlayer player = event.entityPlayer;

		if (OpenBlocks.Blocks.grave == null) {
			Log.log(debugLevel(), "OpenBlocks graves disabled, not placing (player '%s')", player);
			return;
		}

		if (player instanceof FakePlayer) {
			Log.debug("'%s' (%s) is a fake player, grave will not be spawned", player, player.getClass());
			return;
		}

		if (event.isCanceled()) {
			Log.warn("Event for player '%s' cancelled, grave will not be spawned", player);
			return;
		}

		final List<EntityItem> drops = event.drops;
		if (drops.isEmpty()) {
			Log.log(debugLevel(), "No drops from player '%s', grave will not be spawned'", player);
			return;
		}

		final GameRules gameRules = world.getGameRules();
		if (gameRules.getGameRuleBooleanValue("keepInventory") ||
				!gameRules.getGameRuleBooleanValue(GameRule.SPAWN_GRAVES)) {
			Log.log(debugLevel(), "Graves disabled by gamerule (player '%s')", player);
			return;
		}

		final GraveDropsEvent dropsEvent = new GraveDropsEvent(player);
		for (EntityItem drop : drops)
			dropsEvent.addItem(drop);

		if (MinecraftForge.EVENT_BUS.post(dropsEvent)) {
			Log.warn("Grave drops event for player '%s' cancelled, grave will not be spawned'", player);
			return;
		}

		final List<EntityItem> graveLoot = Lists.newArrayList();
		drops.clear(); // will be rebuilt based from event

		for (GraveDropsEvent.ItemAction entry : dropsEvent.drops) {
			switch (entry.action) {
				case DELETE:
					if (Config.debugGraves) Log.info("Item %s is going to be deleted", entry.item);
					break;
				case DROP:
					if (Config.debugGraves) Log.info("Item %s is going to be dropped", entry.item);
					drops.add(entry.item);
					break;
				default:
				case STORE:
					graveLoot.add(entry.item);
			}
		}

		if (graveLoot.isEmpty()) {
			Log.log(debugLevel(), "No grave drops left for player '%s' after event filtering, grave will not be spawned'", player);
			return;
		}

		if (!tryConsumeGrave(player, Iterables.concat(graveLoot, drops))) {
			Log.log(debugLevel(), "No grave in drops for player '%s', grave will not be spawned'", player);
			return;
		}

		Log.log(debugLevel(), "Scheduling grave placement for player '%s':'%s' with %d item(s) stored and %d item(s) dropped",
				player, player.getGameProfile(), graveLoot.size(), drops.size());

		DelayedActionTickHandler.INSTANCE.addTickCallback(world, new GraveCallable(world, player, graveLoot));
	}

	// TODO: candidate for scripting
	private static boolean tryConsumeGrave(EntityPlayer player, Iterable<EntityItem> graveLoot) {
		if (!Config.requiresGraveInInv || player.capabilities.isCreativeMode) return true;

		final Item graveItem = Item.getItemFromBlock(OpenBlocks.Blocks.grave);
		if (graveItem == null) return true;

		final Iterator<EntityItem> lootIter = graveLoot.iterator();
		while (lootIter.hasNext()) {
			final EntityItem drop = lootIter.next();
			final ItemStack itemStack = drop.getEntityItem();
			if (itemStack != null &&
					itemStack.getItem() == graveItem &&
					itemStack.stackSize > 0) {

				if (--itemStack.stackSize <= 0) {
					lootIter.remove();
				} else {
					drop.setEntityItemStack(itemStack);
				}

				return true;
			}
		}

		return false;
	}

	private static void dumpDebugInfo(PlayerDropsEvent event) {
		Log.info("Trying to spawn grave for player '%s':'%s'", event.entityPlayer, event.entityPlayer.getGameProfile());

		int i = 0;
		for (EntityItem e : event.drops)
			Log.info("\tGrave drop %d: %s -> %s", i++, e.getClass(), e.getEntityItem());

		final ListenerList listeners = event.getListenerList();
		try {
			int busId = 0;
			while (true) {
				Log.info("Dumping event %s listeners on bus %d", event.getClass(), busId);
				for (IEventListener listener : listeners.getListeners(busId)) {
					if (listener instanceof ASMEventHandler) {
						try {
							final ASMEventHandler handler = (ASMEventHandler)listener;
							Object o = ReflectionHelper.getPrivateValue(ASMEventHandler.class, handler, "handler");
							Log.info("\t'%s' (handler %s, priority: %s)", handler, o.getClass(), handler.getPriority());
							continue;
						} catch (Throwable e) {
							Log.log(Level.INFO, e, "Exception while getting field");
						}
					}

					Log.info("\t%s", listener.getClass());
				}
				busId++;
			}
		} catch (ArrayIndexOutOfBoundsException terribleLoopExitCondition) {}
	}

}
