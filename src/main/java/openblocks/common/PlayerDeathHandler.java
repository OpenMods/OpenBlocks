package openblocks.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityGrave;
import openmods.GenericInventory;
import openmods.Log;
import openmods.utils.InventoryUtils;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class PlayerDeathHandler {

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onPlayerDrops(PlayerDropsEvent event) {
		if (!Config.FEATURES.isBlockEnabled("grave")) return;

		final List<EntityItem> drops = event.drops;
		if (drops.isEmpty()) return;

		final EntityLivingBase entity = event.entityLiving;
		if (!(entity instanceof EntityPlayer) || entity instanceof FakePlayer) return;

		EntityPlayer player = (EntityPlayer)entity;
		World world = player.worldObj;
		if (world.isRemote || world.getGameRules().getGameRuleBooleanValue("keepInventory")) return;

		int x = MathHelper.floor_double(player.posX);
		int y = MathHelper.floor_double(player.posY);
		int z = MathHelper.floor_double(player.posZ);

		for (int distance = 0; distance < 5; distance++)
			for (int checkX = x - distance; checkX <= x + distance; checkX++)
				for (int checkY = y - distance; checkY <= y + distance; checkY++)
					for (int checkZ = z - distance; checkZ <= z + distance; checkZ++)
						if (tryPlaceGrave(world, checkX, checkY, checkZ, player, drops)) {
							Log.debug("Placing grave for player '%s' @ (%d,%d,%d) with %d items", player.getGameProfile(), checkX, checkY, checkZ, drops.size());
							if (Config.debugGraves) dumpDebugInfo(event);
							drops.clear();
							event.setCanceled(true);
							return;
						}

	}

	private static void dumpDebugInfo(PlayerDropsEvent event) {
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
							Object o = ReflectionHelper.getPrivateValue(ASMEventHandler.class, (ASMEventHandler)listener, "handler");
							Log.debug("\t%s", o.getClass());
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

	private static boolean tryPlaceGrave(World world, int x, int y, int z, EntityPlayer stiff, List<EntityItem> drops) {
		if (!canPlaceGrave(world, x, y, z)) return false;

		world.setBlock(x, y, z, OpenBlocks.Blocks.grave, 0, 2);
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile == null || !(tile instanceof TileEntityGrave)) return false;

		TileEntityGrave grave = (TileEntityGrave)tile;

		GenericInventory invent = new GenericInventory("tmpplayer", false, drops.size());
		for (EntityItem entityItem : drops) {
			ItemStack stack = entityItem.getEntityItem();
			if (stack != null) InventoryUtils.insertItemIntoInventory(invent, stack.copy());
		}

		grave.setUsername(stiff.getGameProfile().getName());
		grave.setLoot(invent);
		return true;
	}

	private static boolean canPlaceGrave(World world, int x, int y, int z) {
		if (!world.blockExists(x, y, z)) return false;

		Block block = world.getBlock(x, y, z);
		if (block == null) return true;

		return (block.isAir(world, x, y, z) || block.isReplaceable(world, x, y, z));
	}
}
