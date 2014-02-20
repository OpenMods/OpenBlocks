package openblocks.client.stencils;

import java.math.BigInteger;

import net.minecraft.client.renderer.texture.TextureMap;
import openblocks.Config;

public class StencilManager extends IconPool<BigInteger, StencilIconPair> {

	private StencilManager() {
		super(Config.stencilIntialPoolSize);
	}

	public static final StencilManager instance = new StencilManager();

	@Override
	protected void onReload(TextureMap map) {
		StencilIcon.CoverIcon.clearBaseData();
	}

	@Override
	protected StencilIconPair createData(TextureMap map, String id) {
		final String coverId = "openblocks.stencil_cover_" + id;
		final String invertedId = "openblocks.stencil_inverted_" + id;

		StencilIcon coverIcon = new StencilIcon.CoverIcon(coverId);
		StencilIcon invertedIcon = new StencilIcon.InvertedIcon(invertedId);

		map.setTextureEntry(coverId, coverIcon);
		map.setTextureEntry(invertedId, invertedIcon);
		return new StencilIconPair(coverIcon, invertedIcon);
	}

	@Override
	protected void loadData(StencilIconPair icons, BigInteger bits) {
		icons.invertedIcon.loadBits(bits);
		icons.coverIcon.loadBits(bits);
	}

	@Override
	protected int getNewSize() {
		return (int)(size * Config.stencilPoolGrowthRate);
	}
}
