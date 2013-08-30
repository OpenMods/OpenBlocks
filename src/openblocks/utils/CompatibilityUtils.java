package openblocks.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ResourceLocation;

public final class CompatibilityUtils {

	public static final String RESOURCE_ASSET_NAME = "openblocks";
	
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
	
	public static void bindTextureToClient(Minecraft client, String texture) {
		client.renderEngine.func_110577_a(getResourceLocation(texture));
	}
	
	public static ResourceLocation getResourceLocation(String resourceName) {
		return new ResourceLocation(RESOURCE_ASSET_NAME, resourceName);
	}
	
}
