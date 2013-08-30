package openblocks.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;

public final class CompatibilityUtils {

	/* Bugger me this is ugly, Needs testing! - NC */
	public static void sendChatToPlayer(EntityPlayer player, String text) {
		player.sendChatToPlayer(ChatMessageComponent.func_111082_b("chat.type.text", new Object[] {text}));
	}
	
	public static float getEntityHealth(EntityLivingBase entity) {
		return entity.func_110143_aJ();
	}
	
	public static float getEntityMaxHealth(EntityLivingBase entity) {
		return entity.func_110138_aP();
	}
	
}
