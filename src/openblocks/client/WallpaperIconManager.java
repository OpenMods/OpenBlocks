package openblocks.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import openblocks.common.WallpaperManager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;

public class WallpaperIconManager {

	public static LinkedList<String> unusedIcons = Lists.newLinkedList();
	public static Map<String, String> names = Maps.newHashMap();
	
	public static void registerIcon(String iconName) {
		unusedIcons.add(iconName);
	}
	
	public static Icon requestWallpaper(String textureName) {
		if (names.containsKey(textureName)) {
			return getBlockIcon(names.get(textureName));
		}
		String iconName = unusedIcons.poll();
		if (iconName != null) {
			names.put(textureName, iconName);
			new WallpaperManager.WallpaperRequestEvent(textureName).sendToServer();
		}

		System.out.println("sent request "+textureName);
		return getBlockIcon(iconName);
	}

	public static void setWallpaper(String id, int[] colorData) {
		Icon icon = getBlockIcon(names.get(id));
		TextureAtlasSprite sprite = (TextureAtlasSprite) icon;
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getBlockTextureMap().getGlTextureId());
		TextureUtil.uploadTextureSub(colorData, icon.getIconWidth(), icon.getIconHeight(), sprite.getOriginX(), sprite.getOriginY(), false, false);
	}

	private static Icon getBlockIcon(String iconName) {
		return getBlockTextureMap().getAtlasSprite(iconName);
	}
	
	public static TextureMap getBlockTextureMap() {
		Minecraft mc = Minecraft.getMinecraft();
		return (TextureMap)mc.renderEngine.getTexture(TextureMap.locationBlocksTexture);
	}

	public static void unloadAll() {
		for (String name : names.values()) {
			unusedIcons.add(name);
		}
		names.clear();
	}
	
}
