package openblocks.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.ASMEventHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.ListenerList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.api.GraveDropsEvent;
import openblocks.api.GraveSpawnEvent;
import openblocks.common.GameRuleManager.GameRule;
import openblocks.common.PlayerInventoryStore.ExtrasFiller;
import openblocks.common.tileentity.TileEntityGrave;
import openmods.Log;
import openmods.inventory.GenericInventory;
import openmods.utils.NbtUtils;
import openmods.world.DelayedActionTickHandler;
import org.apache.logging.log4j.Level;

public class PlayerDeathHandler {

	private static final Comparator<BlockPos> SEARCH_COMPARATOR = new Comparator<BlockPos>() {

		private int coordSum(BlockPos c) {
			return Math.abs(c.getX()) + Math.abs(c.getY()) + Math.abs(c.getZ());
		}

		private int coordMax(BlockPos c) {
			return Math.max(Math.max(Math.abs(c.getX()), Math.abs(c.getY())), Math.abs(c.getZ()));
		}

		@Override
		public int compare(BlockPos a, BlockPos b) {
			// first order by Manhattan distance
			int diff = coordSum(a) - coordSum(b);
			if (diff != 0) return diff;

			// then by distance from axis
			return coordMax(b) - coordMax(a);
		}
	};

	private static class SearchOrder implements Iterable<BlockPos> {
		public final int size;

		private final List<BlockPos> coords;

		public SearchOrder(int size) {
			this.size = size;

			List<BlockPos> coords = Lists.newArrayList();

			for (int x = -size; x <= size; x++)
				for (int y = -size; y <= size; y++)
					for (int z = -size; z <= size; z++)
						coords.add(new BlockPos(x, y, z));

			coords.sort(SEARCH_COMPARATOR);

			this.coords = ImmutableList.copyOf(coords);
		}

		@Override
		public Iterator<BlockPos> iterator() {
			return coords.iterator();
		}
	}

	private static SearchOrder searchOrder;

	private static Iterable<BlockPos> getSearchOrder(int size) {
		if (searchOrder == null || searchOrder.size != size) searchOrder = new SearchOrder(size);
		return searchOrder;
	}

	private abstract static class GravePlacementChecker {
		public boolean canPlace(World world, PlayerEntity player, BlockPos pos) {
			if (!world.isBlockLoaded(pos)) return false;
			if (!world.isBlockModifiable(player, pos)) return false;

			BlockState block = world.getBlockState(pos);
			return checkBlock(world, pos, block);
		}

		public abstract boolean checkBlock(World world, BlockPos pos, BlockState state);
	}

	private static final GravePlacementChecker POLITE = new GravePlacementChecker() {
		@Override
		public boolean checkBlock(World world, BlockPos pos, BlockState state) {
			final Block block = state.getBlock();
			return (block.isAir(state, world, pos) || block.isReplaceable(world, pos));
		}
	};

	private static final GravePlacementChecker BRUTAL = new GravePlacementChecker() {
		@Override
		public boolean checkBlock(World world, BlockPos pos, BlockState state) {
			return state.getBlockHardness(world, pos) >= 0 && world.getTileEntity(pos) == null;
		}
	};

	private static class GraveCallable implements Runnable {

		private final ITextComponent cause;

		private final GameProfile stiffId;

		private final BlockPos playerPos;

		private final List<ItemEntity> loot;

		private final WeakReference<World> world;

		private final WeakReference<PlayerEntity> exPlayer;

		public GraveCallable(World world, PlayerEntity exPlayer, List<ItemEntity> loot) {
			this.playerPos = exPlayer.getPosition();

			this.world = new WeakReference<>(world);

			this.exPlayer = new WeakReference<>(exPlayer);
			this.stiffId = exPlayer.getGameProfile();

			final ITextComponent day = formatDate(world);
			final ITextComponent deathCause = exPlayer.getCombatTracker().getDeathMessage();
			this.cause = new TranslationTextComponent("openblocks.misc.grave_msg", deathCause, day);

			this.loot = ImmutableList.copyOf(loot);
		}

		private static ITextComponent formatDate(World world) {
			final long time = world.getTotalWorldTime();
			final String day = String.format("%.1f", time / 24000.0);
			final ITextComponent dayComponent = new StringTextComponent(day);
			dayComponent.getStyle().setColor(TextFormatting.WHITE).setBold(true);
			return dayComponent;
		}

		private void setCommonStoreInfo(CompoundNBT meta, boolean placed) {
			meta.setString(PlayerInventoryStore.TAG_PLAYER_NAME, stiffId.getName());
			meta.setString(PlayerInventoryStore.TAG_PLAYER_UUID, stiffId.getId().toString());
			meta.setTag("PlayerLocation", NbtUtils.store(playerPos));
			meta.setBoolean("Placed", placed);
		}

		private boolean tryPlaceGrave(World world, final BlockPos gravePos, String gravestoneText, ITextComponent deathMessage) {
			world.setBlockState(gravePos, OpenBlocks.Blocks.grave.getDefaultState());
			TileEntity tile = world.getTileEntity(gravePos);
			if (!(tile instanceof TileEntityGrave)) {
				Log.warn("Failed to place grave @ %s: invalid tile entity: %s(%s)", gravePos, tile, tile != null? tile.getClass() : "?");
				return false;
			}

			TileEntityGrave grave = (TileEntityGrave)tile;

			IInventory loot = getLoot();

			if (Config.backupGraves) backupGrave(world, loot, meta -> {
				setCommonStoreInfo(meta, true);
				meta.setTag("GraveLocation", NbtUtils.store(gravePos));

			});

			Log.info("Grave for (%s,%s) was spawned at (%s) (player died at (%s))", stiffId.getId(), stiffId.getName(), gravePos, playerPos);

			grave.setUsername(gravestoneText);
			grave.setLoot(loot);
			grave.setDeathMessage(deathMessage);
			return true;
		}

		protected IInventory getLoot() {
			final GenericInventory loot = new GenericInventory("tmpplayer", false, this.loot.size());
			final IItemHandler handler = loot.getHandler();
			for (ItemEntity entityItem : this.loot) {
				ItemStack stack = entityItem.getItem();
				if (!stack.isEmpty()) ItemHandlerHelper.insertItemStacked(handler, stack, false);
			}
			return loot;
		}

		private boolean trySpawnGrave(PlayerEntity player, World world) {
			final BlockPos location = findLocation(world, player);

			String gravestoneText = stiffId.getName();
			final GraveSpawnEvent evt = new GraveSpawnEvent(player, location, loot, gravestoneText, cause);

			if (MinecraftForge.EVENT_BUS.post(evt)) {
				Log.warn("Grave event for player %s cancelled, no grave will spawn", stiffId);
				return false;
			}

			if (evt.location == null) {
				Log.warn("No location for grave found, no grave will spawn", stiffId);
				return false;
			}

			Log.log(debugLevel(), "Grave for %s will be spawned at (%s)", stiffId, evt.location);

			final BlockPos under = evt.location.down();
			if (Config.graveBase && canSpawnBase(world, player, under)) {
				world.setBlockState(under, Blocks.DIRT.getDefaultState());
			}

			return tryPlaceGrave(world, evt.location, evt.gravestoneText, evt.clickText);
		}

		private static boolean canSpawnBase(World world, PlayerEntity player, BlockPos pos) {
			return world.isBlockLoaded(pos)
					&& world.isAirBlock(pos)
					&& world.isBlockModifiable(player, pos);
		}

		private BlockPos findLocation(World world, PlayerEntity player, GravePlacementChecker checker) {
			final int limitedPosY = Math.min(Math.max(playerPos.getY(), Config.minGraveY), Config.maxGraveY);
			BlockPos searchPos = new BlockPos(playerPos.getX(), limitedPosY, playerPos.getZ());
			final int searchSize = Config.graveSpawnRange / 2;

			for (BlockPos c : getSearchOrder(searchSize)) {
				final BlockPos tryPos = searchPos.add(c);
				final int y = tryPos.getY();
				if (y > Config.maxGraveY || y < Config.minGraveY) continue;
				if (checker.canPlace(world, player, tryPos)) return tryPos;
			}

			return null;
		}

		private BlockPos findLocation(World world, PlayerEntity player) {
			BlockPos location = findLocation(world, player, POLITE);
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
			PlayerEntity player = exPlayer.get();
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
					backupGrave(world, loot, meta -> setCommonStoreInfo(meta, false));
				}

				for (ItemEntity drop : loot)
					world.spawnEntity(drop);
			}
		}
	}

	private static Level debugLevel() {
		return Config.debugGraves? Level.INFO : Level.DEBUG;
	}

	@SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
	public void onPlayerDrops(PlayerDropsEvent event) {
		World world = event.getEntityPlayer().world;
		if (world.isRemote) return;

		if (Config.debugGraves) dumpDebugInfo(event);

		final PlayerEntity player = event.getEntityPlayer();

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

		final List<ItemEntity> drops = event.getDrops();
		if (drops.isEmpty()) {
			Log.log(debugLevel(), "No drops from player '%s', grave will not be spawned'", player);
			return;
		}

		final GameRules gameRules = world.getGameRules();
		if (gameRules.getBoolean("keepInventory") ||
				!gameRules.getBoolean(GameRule.SPAWN_GRAVES)) {
			Log.log(debugLevel(), "Graves disabled by gamerule (player '%s')", player);
			return;
		}

		final GraveDropsEvent dropsEvent = new GraveDropsEvent(player);
		for (ItemEntity drop : drops)
			dropsEvent.addItem(drop);

		if (MinecraftForge.EVENT_BUS.post(dropsEvent)) {
			Log.warn("Grave drops event for player '%s' cancelled, grave will not be spawned'", player);
			return;
		}

		final List<ItemEntity> graveLoot = Lists.newArrayList();
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
			Log.log(debugLevel(), "No grave in drops for player '%s' after firing event, grave will not be spawned'", player);
			drops.addAll(graveLoot); // re-add any loot that would have gone to grave
			return;
		}

		Log.log(debugLevel(), "Scheduling grave placement for player '%s':'%s' with %d item(s) stored and %d item(s) dropped",
				player, player.getGameProfile(), graveLoot.size(), drops.size());

		DelayedActionTickHandler.INSTANCE.addTickCallback(world, new GraveCallable(world, player, graveLoot));
	}

	// TODO: candidate for scripting
	private static boolean tryConsumeGrave(PlayerEntity player, Iterable<ItemEntity> graveLoot) {
		if (!Config.requiresGraveInInv || player.capabilities.isCreativeMode) return true;

		final Item graveItem = Item.getItemFromBlock(OpenBlocks.Blocks.grave);
		if (graveItem == Items.AIR) return true;

		final Iterator<ItemEntity> lootIter = graveLoot.iterator();
		while (lootIter.hasNext()) {
			final ItemEntity drop = lootIter.next();
			final ItemStack itemStack = drop.getItem();
			if (itemStack.getItem() == graveItem && !itemStack.isEmpty()) {
				itemStack.shrink(1);
				if (itemStack.isEmpty()) {
					lootIter.remove();
				} else {
					drop.setItem(itemStack);
				}

				return true;
			}
		}

		return false;
	}

	private static void dumpDebugInfo(PlayerDropsEvent event) {
		Log.info("Trying to spawn grave for player '%s':'%s'", event.getEntityPlayer(), event.getEntityPlayer().getGameProfile());

		int i = 0;
		for (ItemEntity e : event.getDrops())
			Log.info("\tGrave drop %d: %s -> %s", i++, e.getClass(), e.getItem());

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
