package openblocks.common;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityGrave;
import openmods.GenericInventory;
import openmods.utils.InventoryUtils;

public class PlayerDeathHandler {

	@ForgeSubscribe(priority = EventPriority.LOW)
	public void onPlayerDrops(PlayerDropsEvent event) {
		if (event.entityLiving != null
				&& event.entityLiving instanceof EntityPlayer && !(event.entityLiving instanceof FakePlayer)) {

			EntityPlayer player = (EntityPlayer)event.entityLiving;
			World world = player.worldObj;

			if (!world.isRemote
					&& !world.getGameRules().getGameRuleBooleanValue("keepInventory")) {
				int x = (int)player.posX;
				int y = (int)player.posY;
				int z = (int)player.posZ;
				boolean aboveIsAir = false;
				
				

				for (int checkY = y + 2; checkY > y - 4; checkY--) {
					int bId = world.getBlockId(x, checkY, z);
					Block block = Block.blocksList[bId];
					boolean thisIsAir = world.isAirBlock(x, checkY, z) || (block != null && block.isBlockReplaceable(world, x, checkY, z));

					if (!thisIsAir && aboveIsAir) {
						checkY++;
						world.setBlock(x, checkY, z, Config.blockGraveId, 0, 2);
						TileEntity tile = world.getBlockTileEntity(x, checkY, z);
						if (tile != null && tile instanceof TileEntityGrave) {
							TileEntityGrave grave = (TileEntityGrave)tile;
							grave.setUsername(player.username);
							GenericInventory invent = new GenericInventory("tmpplayer", false, 1000);
							for (EntityItem entityItem : event.drops) {
								ItemStack stack = entityItem.getEntityItem();
								InventoryUtils.insertItemIntoInventory(invent, stack);
							}
							grave.setLoot(invent);
							event.setCanceled(true);
							break;
						}
					} else if (thisIsAir) {
						aboveIsAir = true;
					}
				}
			}
		}
	}
}
