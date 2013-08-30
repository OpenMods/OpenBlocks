package openblocks.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;

public final class CompatibilityUtils {

	/* Bugger me this is ugly, Needs testing! - NC */
	public static void sendChatToPlayer(EntityPlayer player, String text) {
		player.sendChatToPlayer(ChatMessageComponent.func_111082_b("chat.type.text", new Object[] {text}));
	}
	
}
