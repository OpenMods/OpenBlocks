package openblocks.common;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.GameRuleManager.GameRule;
import openblocks.common.PlayerInventoryStore.ExtrasFiller;
import openblocks.common.tileentity.TileEntityGrave;
import openmods.Log;
import openmods.inventory.GenericInventory;
import openmods.utils.BlockNotifyFlags;
import openmods.utils.InventoryUtils;
import openmods.utils.TagUtils;
import openmods.world.DelayedActionTickHandler;

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class PlayerDeathHandler {

	private interface IGravePlacementChecker {
		public boolean canPlaceGrave(World world, int x, int y, int z);
	}

	private static final IGravePlacementChecker POLITE = new IGravePlacementChecker() {
		@Override
		public boolean canPlaceGrave(World world, int x, int y, int z) {
			if (!world.blockExists(x, y, z)) return false;

			Block block = world.getBlock(x, y, z);
			return (block.isAir(world, x, y, z) || block.isReplaceable(world, x, y, z));
		}
	};

	private static final IGravePlacementChecker BRUTAL = new IGravePlacementChecker() {
		@Override
		public boolean canPlaceGrave(World world, int x, int y, int z) {
			if (!world.blockExists(x, y, z)) return false;

			Block block = world.getBlock(x, y, z);
			return block.getBlockHardness(world, x, y, z) >= 0 && world.getTileEntity(x, y, z) == null;
		}
	};

	private static class GraveCallable implements Runnable {

		private final GameProfile stiffId;

		private final int posX, posY, posZ;

		private final List<EntityItem> loot;

		private final WeakReference<World> world;

		public GraveCallable(World world, EntityPlayer exPlayer, List<EntityItem> loot) {
			this.stiffId = exPlayer.getGameProfile();

			this.posX = MathHelper.floor_double(exPlayer.posX);
			this.posY = MathHelper.floor_double(exPlayer.posY);
			this.posZ = MathHelper.floor_double(exPlayer.posZ);

			this.world = new WeakReference<World>(world);

			this.loot = ImmutableList.copyOf(loot);
		}

		private boolean tryPlaceGrave(World world, final int x, final int y, final int z) {
			world.setBlock(x, y, z, OpenBlocks.Blocks.grave, 0, BlockNotifyFlags.ALL);
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile == null || !(tile instanceof TileEntityGrave)) return false;

			TileEntityGrave grave = (TileEntityGrave)tile;

			IInventory loot = new GenericInventory("tmpplayer", false, this.loot.size());
			for (EntityItem entityItem : this.loot) {
				ItemStack stack = entityItem.getEntityItem();
				if (stack != null) InventoryUtils.insertItemIntoInventory(loot, stack.copy());
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
			return true;
		}

		private boolean tryPlaceGrave(World world, IGravePlacementChecker checker) {
			for (int distance = 0; distance < Config.graveSpawnRange; distance++)
				for (int checkX = posX - distance; checkX <= posX + distance; checkX++)
					for (int checkY = posY - distance; checkY <= posY + distance; checkY++)
						for (int checkZ = posZ - distance; checkZ <= posZ + distance; checkZ++)
							if (checker.canPlaceGrave(world, checkX, checkY, checkZ) &&
									tryPlaceGrave(world, checkX, checkY, checkZ)) {
								Log.debug("Placing grave for player '%s' @ (%d,%d,%d)", stiffId, checkX, checkY, checkZ);
								return true;
							}

			return false;
		}

		@Override
		public void run() {
			World world = this.world.get();
			if (world == null) {
				Log.warn("Lost world while placing player %s grave", stiffId);
				return;
			}

			if (tryPlaceGrave(world, POLITE)) return;

			if (Config.destructiveGraves) {
				Log.warn("Failed to place grave for player %s, going berserk", stiffId);
				if (tryPlaceGrave(world, BRUTAL)) return;
			}

			for (EntityItem drop : loot)
				world.spawnEntityInWorld(drop);
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
