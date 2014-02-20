package openblocks.client.wallpapers;

import java.util.LinkedList;
import java.util.Map;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import openblocks.common.WallpaperManager;
import openblocks.common.WallpaperManager.WallpaperResponseEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WallpaperIconManager {

	protected WallpaperIconManager() {}
	
	private static final int PLACEHOLDER_COUNT = 128;
	
	public static final WallpaperIconManager instance = new WallpaperIconManager();
	
	public LinkedList<ReplaceableIcon> unusedIcons = Lists.newLinkedList();
	public Map<String, ReplaceableIcon> names = Maps.newHashMap();
	
	public Icon requestWallpaper(String textureName) {
		if (names.containsKey(textureName)) {
			return names.get(textureName);
		}
		ReplaceableIcon spareIcon = unusedIcons.poll();
		if (spareIcon != null) {
			names.put(textureName, spareIcon);
			new WallpaperManager.WallpaperRequestEvent(textureName).sendToServer();
		}

		return spareIcon;
	}
	

	@ForgeSubscribe
	public void onWallpaperResponse(WallpaperResponseEvent evt) {
		String id = evt.getId();
		ReplaceableIcon icon = names.get(id);
		if (icon != null) {
			icon.setBytes(evt.getData().colorData);
		}
	}
	
	@ForgeSubscribe
	public void onWorldUnload(WorldEvent.Unload evt) {
		unloadAll();
	}
	
	public void unloadAll() {
		for (ReplaceableIcon icon : names.values()) {
			unusedIcons.add(icon);
		}
		names.clear();
	}
	
	public void allocatePlaceholderIcons(TextureMap registry) {
		unloadAll();
		unusedIcons.clear();
		for (int i = 0; i < PLACEHOLDER_COUNT; i++) {
			final String textureId = "openblocks.wallpaper_" + i;
			ReplaceableIcon icon = new ReplaceableIcon(textureId);
			unusedIcons.add(icon);
			registry.setTextureEntry(textureId, icon);
		}
	}
	
}
