package openblocks.client.stencils;

import java.util.Deque;
import java.util.Map;
import java.util.NoSuchElementException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import openmods.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class IconPool<K, T> {

	protected int size;
	private final Map<K, T> usedIcons = Maps.newHashMap();
	private final Deque<T> freeIcons = Lists.newLinkedList();
	private T emptyIcon;

	public IconPool(int intialSize) {
		this.size = intialSize;
	}

	public synchronized T getIcon(K bits) {
		T icon = usedIcons.get(bits);
		if (icon == null) {
			try {
				icon = freeIcons.pop();
				loadData(icon, bits);
			} catch (NoSuchElementException e) {
				int newSize = getNewSize();
				if (newSize > size) {
					Log.info("Resizing stencil pool from %d to %d. This may generate some lag and visual glitches. If this happens to ofter consider changing your settings", size, newSize);
					size = newSize;
					forceReload();
					icon = freeIcons.pop();
				} else {
					Log.warn("No icons left in pool (size: %d), but can't resize. Returning empty one", size);
					icon = emptyIcon;
				}
			}

			usedIcons.put(bits, icon);
		}

		return icon;
	}

	protected abstract T createData(TextureMap map, String id);

	protected abstract void loadData(T icons, K data);

	protected abstract int getNewSize();

	private static void forceReload() {
		final Minecraft minecraft = Minecraft.getMinecraft();
		final TextureManager manager = minecraft.renderEngine;
		final RenderGlobal renderer = minecraft.renderGlobal;

		TextureObject blockMap = manager.getTexture(TextureMap.locationBlocksTexture);
		manager.loadTexture(TextureMap.locationBlocksTexture, blockMap);
		if (renderer != null) renderer.loadRenderers();
	}

	@ForgeSubscribe
	public synchronized void onReload(TextureStitchEvent.Pre evt) {
		final TextureMap textureMap = evt.map;
		if (textureMap.textureType != 0) return;

		usedIcons.clear();
		freeIcons.clear();

		emptyIcon = createData(textureMap, "empty");

		for (int i = 0; i < size; i++)
			freeIcons.add(createData(textureMap, Integer.toString(i)));

		onReload(textureMap);
	}

	protected void onReload(TextureMap map) {}

	@ForgeSubscribe
	public synchronized void onWorldUnload(WorldEvent.Unload evt) {
		freeIcons.addAll(usedIcons.values());
		usedIcons.clear();
	}
}
