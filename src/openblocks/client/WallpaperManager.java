package openblocks.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import openblocks.events.WallpaperEvents;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.util.Icon;

public class WallpaperManager {

	public static LinkedList<Icon> unusedIcons = Lists.newLinkedList();
	public static Map<String, Icon> names = Maps.newHashMap();
	
	public static void registerIcon(Icon icon) {
		unusedIcons.add(icon);
	}
	
	public static void requestWallpaper(String textureName) {
		if (names.containsKey(textureName)) {
			return;
		}
		Icon icon = unusedIcons.poll();
		if (icon != null) {
			names.put(textureName, icon);
			new WallpaperEvents.WallpaperRequestEvent(textureName).sendToServer();
		}
	}

	public static void setWallpaper(String id, int[] colorData) {
		System.out.println(id);
		System.out.println(colorData.length);
	}

}
