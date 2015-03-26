package openblocks.common;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.ReflectionHelper;

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

		private boolean tryPlaceGrave(World world, final int x, final int y, final int z) {
			world.setBlock(x, y, z, OpenBlocks.Blocks.grave, 0, BlockNotifyFlags.ALL);
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile == null || !(tile instanceof TileEntityGrave)) {
				Log.warn("Failed to place grave @ %d,%d,%d: invalid tile entity: %s(%s)", x, y, z, tile, tile != null? tile.getClass() : "?");
				return false;
			}

			TileEntityGrave grave = (TileEntityGrave)tile;

			IInventory loot = new GenericInventory("tmpplayer", false, this.loot.size());
			for (EntityItem entityItem : this.loot) {
				ItemStack stack = entityItem.getEntityItem();
				if (stack != null) ItemDistribution.insertItemIntoInventory(loot, stack.copy());
			}

			if (Config.backupGraves) {
				try {
					File backup = PlayerInventoryStore.instance.storeInventory(loot, stiffId.getName(), "grave", world,
							new ExtrasFiller() {
								@Override
								public void addExtras(NBTTagCompound meta) {
									meta.setString("PlayerName", stiffId.getName());
									meta.setString("PlayerUUID", stiffId.getId().toString());
									meta.setTag("GraveLocation", TagUtils.store(x, y, z));
									meta.setTag("PlayerLocation", TagUtils.store(posX, posY, posZ));
								}
							});

					Log.info("Grave backup for player %s saved to %s", stiffId, backup);
				} catch (Throwable t) {
					Log.warn("Failed to store grave backup for player %s", stiffId);
				}
			}

			grave.setUsername(stiffId.getName());
			grave.setLoot(loot);
			grave.setDeathMessage(cause);
			return true;
		}

		private boolean trySpawnGrave(EntityPlayer player, World world) {
			final Coord location = findLocation(world, player);

			GraveSpawnEvent evt = location == null
					? new GraveSpawnEvent(player, loot, cause)
					: new GraveSpawnEvent(player, location.x, location.y, location.z, loot, cause);

			if (MinecraftForge.EVENT_BUS.post(evt) || !evt.hasLocation()) return false;

			final int x = evt.getX();
			final int y = evt.getY();
			final int z = evt.getZ();

			if (Config.graveBase && canSpawnBase(world, player, x, y - 1, z)) {
				world.setBlock(x, y - 1, z, Blocks.dirt);
			}

			return tryPlaceGrave(world, evt.getX(), evt.getY(), evt.getZ());
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
				for (EntityItem drop : loot)
					world.spawnEntityInWorld(drop);
			}
		}

	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onPlayerDrops(PlayerDropsEvent event) {
		World world = event.entityPlayer.worldObj;
		if (world.isRemote) return;

		if (Config.debugGraves) dumpDebugInfo(event);

		final EntityPlayer player = event.entityPlayer;

		if (OpenBlocks.Blocks.grave == null) {
			Log.debug("Graves disabled, not placing (player '%s')", player);
			return;
		}

		if (player instanceof FakePlayer) {
			Log.debug("'%s' (%s) is a fake player, ignoring", player, player.getClass());
			return;
		}

		final List<EntityItem> drops = event.drops;
		if (drops.isEmpty()) {
			Log.debug("No drops from player '%s'", player);
			return;
		}

		final GameRules gameRules = world.getGameRules();
		if (gameRules.getGameRuleBooleanValue("keepInventory") ||
				!gameRules.getGameRuleBooleanValue(GameRule.SPAWN_GRAVES)) {
			Log.debug("Graves disabled by gamerule (player '%s')", player);
			return;
		}

		Log.debug("Scheduling grave placement for player '%s':'%s' with %d items", player, player.getGameProfile(), drops.size());

		DelayedActionTickHandler.INSTANCE.addTickCallback(world, new GraveCallable(world, player, drops));
		drops.clear();
		event.setCanceled(true);
	}

	private static void dumpDebugInfo(PlayerDropsEvent event) {
		Log.debug("Trying to spawn grave for player '%s':'%s'", event.entityPlayer, event.entityPlayer.getGameProfile());

		int i = 0;
		for (EntityItem e : event.drops)
			Log.debug("\tGrave drop %d: %s -> %s", i++, e.getClass(), e.getEntityItem());

		ListenerList listeners = event.getListenerList();
		try {
			int busId = 0;
			while (true) {
				Log.debug("Dumping event %s listeners on bus %d", event.getClass(), busId);
				for (IEventListener listener : listeners.getListeners(busId)) {
					if (listener instanceof ASMEventHandler) {
						try {
							final ASMEventHandler handler = (ASMEventHandler)listener;
							Object o = ReflectionHelper.getPrivateValue(ASMEventHandler.class, handler, "handler");
							Log.debug("\t'%s' (handler %s, priority: %s)", handler, o.getClass(), handler.getPriority());
							continue;
						} catch (Throwable e) {
							Log.log(Level.DEBUG, e, "Exception while getting field");
						}
					}

					Log.debug("\t%s", listener.getClass());
				}
				busId++;
			}
		} catch (ArrayIndexOutOfBoundsException terribleLoopExitCondition) {}
	}

}
