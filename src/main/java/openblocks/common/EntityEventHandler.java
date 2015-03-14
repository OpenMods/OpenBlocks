package openblocks.common;

import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.Log;
import openmods.config.properties.ConfigurationChange;
import openmods.utils.BlockUtils;
import openmods.utils.PlayerUtils;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EntityEventHandler {

	public static final String OPENBLOCKS_PERSIST_TAG = "OpenBlocks";
	public static final String GIVEN_MANUAL_TAG = "givenManual";
	public static final String LATEST_CHANGELOG_TAG = "latestChangelog";

	private Set<Class<?>> entityBlacklist;

	@SuppressWarnings("unchecked")
	private Set<Class<?>> getBlacklist() {
		if (entityBlacklist == null) {
			entityBlacklist = Sets.newIdentityHashSet();

			Set<String> unknownNames = Sets.newHashSet();
			for (String name : Config.disableMobNames) {

				Class<?> cls = (Class<?>)EntityList.stringToClassMapping.get(name);
				if (cls != null) entityBlacklist.add(cls);
				else unknownNames.add(name);
			}

			// using Class.forName is unsafe
			for (Class<?> cls : (Set<Class<?>>)EntityList.classToStringMapping.keySet()) {
				if (unknownNames.isEmpty()) break;
				if (unknownNames.remove(cls.getName())) entityBlacklist.add(cls);
			}

			if (!unknownNames.isEmpty()) Log.warn("Can't identify mobs for blacklist: %s", unknownNames);
		}

		return entityBlacklist;
	}

	@SubscribeEvent
	public void onReconfigure(ConfigurationChange.Post evt) {
		if (evt.check("additional", "disableMobNames")) entityBlacklist = null;
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {

		final Entity entity = event.entity;
		if (entity != null) {
			Set<Class<?>> blacklist = getBlacklist();
			if (blacklist.contains(entity.getClass())) {
				entity.setDead();
				event.setCanceled(true);
				return;
			}
		}

		/**
		 * If the player hasn't been given a manual, we'll give him one! (or
		 * throw it on the floor..)
		 */
		if (Config.spamInfoBook && !event.world.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)entity;
			NBTTagCompound persistTag = PlayerUtils.getModPlayerPersistTag(player, "OpenBlocks");

			boolean shouldGiveManual = OpenBlocks.Items.infoBook != null && !persistTag.getBoolean(GIVEN_MANUAL_TAG);
			if (shouldGiveManual) {
				ItemStack manual = new ItemStack(OpenBlocks.Items.infoBook);
				if (!player.inventory.addItemStackToInventory(manual)) {
					BlockUtils.dropItemStackInWorld(player.worldObj, player.posX, player.posY, player.posZ, manual);
				}
				persistTag.setBoolean(GIVEN_MANUAL_TAG, true);
			}
		}
	}
}
