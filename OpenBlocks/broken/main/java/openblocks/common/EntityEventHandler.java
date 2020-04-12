package openblocks.common;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.Log;
import openmods.config.properties.ConfigurationChange;
import openmods.utils.BlockUtils;
import openmods.utils.PlayerUtils;

public class EntityEventHandler {

	public static final String OPENBLOCKS_PERSIST_TAG = "OpenBlocks";
	public static final String GIVEN_MANUAL_TAG = "givenManual";
	public static final String LATEST_CHANGELOG_TAG = "latestChangelog";

	private Set<Class<? extends Entity>> entityBlacklist;

	private Set<Class<? extends Entity>> getBlacklist() {
		if (entityBlacklist == null) {
			entityBlacklist = Sets.newIdentityHashSet();

			Set<ResourceLocation> unknownNames = Sets.newHashSet();
			for (String name : Config.disableMobNames) {
				final ResourceLocation location = new ResourceLocation(name);

				Class<? extends Entity> cls = EntityList.getClass(location);
				if (cls != null) entityBlacklist.add(cls);
				else unknownNames.add(location);
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

		final Entity entity = event.getEntity();
		if (entity != null) {
			Set<Class<? extends Entity>> blacklist = getBlacklist();
			if (blacklist.contains(entity.getClass())) {
				entity.setDead();
				event.setCanceled(true);
				return;
			}
		}

		/*
		 * If the player hasn't been given a manual, we'll give him one! (or
		 * throw it on the floor..)
		 */
		if (Config.spamInfoBook && !event.getWorld().isRemote && entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)entity;
			CompoundNBT persistTag = PlayerUtils.getModPlayerPersistTag(player, "OpenBlocks");

			boolean shouldGiveManual = OpenBlocks.Items.infoBook != null && !persistTag.getBoolean(GIVEN_MANUAL_TAG);
			if (shouldGiveManual) {
				ItemStack manual = new ItemStack(OpenBlocks.Items.infoBook);
				if (!player.inventory.addItemStackToInventory(manual)) {
					BlockUtils.dropItemStackInWorld(player.world, player.posX, player.posY, player.posZ, manual);
				}
				persistTag.setBoolean(GIVEN_MANUAL_TAG, true);
			}
		}
	}
}
