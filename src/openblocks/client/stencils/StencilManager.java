package openblocks.client.stencils;

import java.math.BigInteger;
import java.util.Deque;
import java.util.Map;

import net.minecraft.client.renderer.texture.TextureMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StencilManager {

	private static final int PLACEHOLDER_COUNT = 128;

	public static class StencilData {
		public final StencilIcon coverIcon;
		public final StencilIcon invertedIcon;

		public StencilData(StencilIcon coverIcon, StencilIcon invertedIcon) {
			this.coverIcon = coverIcon;
			this.invertedIcon = invertedIcon;
		}
	}

	private StencilManager() {}

	public static final StencilManager instance = new StencilManager();

	private final Map<BigInteger, StencilData> usedIcons = Maps.newHashMap();
	private final Deque<StencilData> freeIcons = Lists.newLinkedList();

	public synchronized StencilData getStencilIcon(BigInteger bits) {
		StencilData icon = usedIcons.get(bits);
		if (icon == null) {
			icon = createIcons(bits);
			usedIcons.put(bits, icon);
		}

		return icon;
	}

	private StencilData createIcons(BigInteger bits) {
		StencilData data = freeIcons.pop();
		data.invertedIcon.loadBits(bits);
		data.coverIcon.loadBits(bits);
		return data;
	}

	public synchronized void allocatePlaceholderIcons(TextureMap registry) {
		usedIcons.clear();
		freeIcons.clear();

		for (int i = 0; i < PLACEHOLDER_COUNT; i++) {
			final String coverId = "openblocks.stencil_cover_" + i;
			final String invertedId = "openblocks.stencil_inverted_" + i;

			StencilIcon coverIcon = new StencilIcon.CoverIcon(coverId);
			StencilIcon invertedIcon = new StencilIcon.InvertedIcon(invertedId);

			registry.setTextureEntry(coverId, coverIcon);
			registry.setTextureEntry(invertedId, invertedIcon);

			freeIcons.add(new StencilData(coverIcon, invertedIcon));
		}
	}
}
