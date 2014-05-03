package openblocks;

import net.minecraft.entity.player.EntityPlayer;
import openblocks.common.item.ItemSleepingBag;

public class Hooks {
	public static boolean isInBed(EntityPlayer player) {
		return ItemSleepingBag.isWearingSleepingBag(player);
	}
}
