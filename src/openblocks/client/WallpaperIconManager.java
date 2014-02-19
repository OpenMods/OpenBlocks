package openblocks.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import openblocks.common.WallpaperManager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;

public class WallpaperIconManager {

	public static LinkedList<Icon> unusedIcons = Lists.newLinkedList();
	public static Map<String, Icon> names = Maps.newHashMap();
	
	public static void registerIcon(Icon icon) {
		unusedIcons.add(icon);
		System.out.println(icon);
	}
	
	public static Icon requestWallpaper(String textureName) {
		if (names.containsKey(textureName)) {
			return names.get(textureName);
		}
		Icon icon = unusedIcons.poll();
		if (icon != null) {
			names.put(textureName, icon);
			new WallpaperManager.WallpaperRequestEvent(textureName).sendToServer();
		}
		return names.get(textureName);
	}

	public static void setWallpaper(String id, int[] colorData) {
		Icon icon = names.get(id);
		TextureAtlasSprite sprite = (TextureAtlasSprite) icon;
		System.out.println(sprite);
		TextureUtil.uploadTextureSub(colorData, icon.getIconWidth(), icon.getIconHeight(), sprite.getOriginX(), sprite.getOriginY(), false, false);
	}

}
