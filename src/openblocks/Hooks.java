package openblocks;

import openblocks.common.item.ItemSleepingBag;
import net.minecraft.entity.player.EntityPlayer;

public class Hooks {
	public static boolean isInBed(EntityPlayer player) {
		return ItemSleepingBag.isWearingSleepingBag(player);
	}
}
