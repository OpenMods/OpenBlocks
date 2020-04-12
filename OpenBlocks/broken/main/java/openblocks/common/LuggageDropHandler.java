package openblocks.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.common.item.ItemLuggage;

public class LuggageDropHandler {

	private static boolean shouldPreventDespawn(ItemEntity entity) {
		ItemStack stack = entity.getItem();
		return !stack.isEmpty() && stack.getItem() instanceof ItemLuggage && stack.hasTagCompound();
	}

	@SubscribeEvent
	public void onItemDrop(EntityJoinWorldEvent evt) {
		final Entity entity = evt.getEntity();
		if (entity instanceof ItemEntity && shouldPreventDespawn((ItemEntity)entity)) {
			entity.setEntityInvulnerable(true);
		}
	}

	@SubscribeEvent
	public void onItemExpire(ItemExpireEvent evt) {
		if (shouldPreventDespawn(evt.getEntityItem())) {
			evt.setExtraLife(evt.getExtraLife() + 0x12F58BF);
			evt.setCanceled(true);
		}
	}
}
