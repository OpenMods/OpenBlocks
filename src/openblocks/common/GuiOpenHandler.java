package openblocks.common;

import openblocks.OpenBlocks.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;

public class GuiOpenHandler {
	
	@ForgeSubscribe
	public void onGuiOpen(PlayerOpenContainerEvent event) {
		if (event.entityPlayer != null) {
			ItemStack held = event.entityPlayer.getHeldItem();
			if (held != null && held.getItem() != null && held.getItem().equals(Items.cursor)) {
				event.setResult(Result.ALLOW);
			}
		}
	}
}
