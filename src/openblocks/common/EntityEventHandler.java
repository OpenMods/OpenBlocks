package openblocks.common;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.utils.BlockUtils;
import openmods.utils.PlayerUtils;

import org.apache.commons.lang3.ArrayUtils;

public class EntityEventHandler {

	public static final String OPENBLOCKS_PERSIST_TAG = "OpenBlocks";
	public static final String GIVEN_MANUAL_TAG = "givenManual";
	
	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {

		if (Config.disableMobNames.length > 0 && event.entity != null
				&& EntityList.classToStringMapping.containsKey(event.entity.getClass())) {
			String livingName = (String)EntityList.classToStringMapping.get(event.entity.getClass());

			if (ArrayUtils.contains(Config.disableMobNames, livingName)) {
				event.entity.setDead();
				return;
			}
		}
		
		
		/**
		 * If the player hasn't been given a manual, we'll give him one! (or throw it on the floor..)
		 */
		if (OpenBlocks.Items.infoBook != null && event.entity instanceof EntityPlayer) {
			
			EntityPlayer player = (EntityPlayer) event.entity;
			
			NBTTagCompound persistTag = PlayerUtils.getModPlayerPersistTag(player, "OpenBlocks");
			
			if (!persistTag.getBoolean(GIVEN_MANUAL_TAG)) {
				
				ItemStack manual = new ItemStack(OpenBlocks.Items.infoBook);
				
				if (!player.inventory.addItemStackToInventory(manual)) {
					BlockUtils.dropItemStackInWorld(player.worldObj, player.posX, player.posY, player.posZ, manual);
				}
				
				persistTag.setBoolean(GIVEN_MANUAL_TAG, true);
			}
		}
	}
}
