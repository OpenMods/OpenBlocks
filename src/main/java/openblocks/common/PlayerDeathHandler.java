package openblocks.common;

import java.util.List;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityGrave;
import openmods.GenericInventory;
import openmods.Log;
import openmods.utils.InventoryUtils;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class PlayerDeathHandler {

	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onPlayerDrops(PlayerDropsEvent event) {
		if (Config.blockGraveId == 0) return;

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
							Log.fine("Placing grave for player '%s' @ (%d,%d,%d) with %d items", player.username, checkX, checkY, checkZ, drops.size());
							if (Config.debugGraves) dumpDebugInfo(event);
							drops.clear();
							event.setCanceled(true);
							return;
						}

	}

	private static void dumpDebugInfo(PlayerDropsEvent event) {
		int i = 0;
		for (EntityItem e : event.drops)
			Log.fine("\tGrave drop %d: %s -> %s", i++, e.getClass(), e.getEntityItem());

		ListenerList listeners = event.getListenerList();
		try {
			int busId = 0;
			while (true) {
				Log.fine("Dumping event %s listeners on bus %d", event.getClass(), busId);
				for (IEventListener listener : listeners.getListeners(busId)) {
					if (listener instanceof ASMEventHandler) {
						try {
							Object o = ReflectionHelper.getPrivateValue(ASMEventHandler.class, (ASMEventHandler)listener, "handler");
							Log.fine("\t%s", o.getClass());
							continue;
						} catch (Throwable e) {
							Log.log(Level.FINE, e, "Exception while getting field");
						}
					}

					Log.fine("\t%s", listener.getClass());
				}
				busId++;
			}
		} catch (ArrayIndexOutOfBoundsException terribleLoopExitCondition) {}
	}

	private static boolean tryPlaceGrave(World world, int x, int y, int z, EntityPlayer stiff, List<EntityItem> drops) {
		if (!canPlaceGrave(world, x, y, z)) return false;

		world.setBlock(x, y, z, Config.blockGraveId, 0, 2);
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile == null || !(tile instanceof TileEntityGrave)) return false;

		TileEntityGrave grave = (TileEntityGrave)tile;

		GenericInventory invent = new GenericInventory("tmpplayer", false, drops.size());
		for (EntityItem entityItem : drops) {
			ItemStack stack = entityItem.getEntityItem();
			if (stack != null) InventoryUtils.insertItemIntoInventory(invent, stack.copy());
		}

		grave.setUsername(stiff.username);
		grave.setLoot(invent);
		return true;
	}

	private static boolean canPlaceGrave(World world, int x, int y, int z) {
		if (!world.blockExists(x, y, z)) return false;

		int blockId = world.getBlockId(x, y, z);
		Block block = Block.blocksList[blockId];
		if (block == null) return true;

		return (block.isAirBlock(world, x, y, z) || block.isBlockReplaceable(world, x, y, z));
	}
}
