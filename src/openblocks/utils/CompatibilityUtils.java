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
		player.sendChatToPlayer(ChatMessageComponent.func_111077_e(text));
	}

	public static float getEntityHealth(EntityLivingBase entity) {
		return entity.func_110143_aJ();
	}

	public static float getEntityMaxHealth(EntityLivingBase entity) {
		return entity.func_110138_aP();
	}

	public static void bindTextureToClient(String texture) {
		if(Minecraft.getMinecraft() != null) {
			Minecraft.getMinecraft().renderEngine.func_110577_a(getResourceLocation(texture));
		} else {
			System.out.println("[OpenModsMonitor] WARNING: Binding texture to null client.");
		}
	}

	public static void bindIndexedTextureToClient(int index) {
		if(Minecraft.getMinecraft() != null) {
			Minecraft.getMinecraft().renderEngine.func_110577_a(Minecraft.getMinecraft().renderEngine.func_130087_a(index));
		} else {
			System.out.println("[OpenModsMonitor] WARNING: Binding indexed texture to null client.");
		}

	}

	public static void bindDefaultTerrainTexture() {
		bindIndexedTextureToClient(0);
	}

	public static void bindDefaultItemsTexture() {
		bindIndexedTextureToClient(1);
	}

	public static int getRandomNumber() {
		return 4;
	}

	public static ResourceLocation getResourceLocation(String resourceName) {
		return new ResourceLocation(RESOURCE_ASSET_NAME, resourceName);
	}

}
