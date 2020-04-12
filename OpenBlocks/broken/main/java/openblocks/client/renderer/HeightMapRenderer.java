package openblocks.client.renderer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import openblocks.common.HeightMapData;
import openmods.renderer.DynamicTextureAtlas;
import openmods.renderer.DynamicTextureAtlas.AtlasCell;
import org.lwjgl.opengl.GL11;

public class HeightMapRenderer {
	public static final HeightMapRenderer instance = new HeightMapRenderer();

	private HeightMapRenderer() {}

	public static final double PLANE_HEIGHT = 1.0 / 64.0;

	private enum PlaneOrientation {
		XY,
		XZ,
		YZ
	}

	private static class PlaneData {
		public PlaneOrientation orientation;
		public int param;
		public DynamicTextureAtlas.AtlasCell texture;
	}

	private static class MapRenderData {
		public int renderedDataHash;
		public DynamicTextureAtlas atlas;
		public Integer displayList;

		public void free() {
			if (displayList != null) {
				GL11.glDeleteLists(displayList, 1);
				displayList = null;
			}
		}

		private List<PlaneData> updateMapTexture(HeightMapData map) {
			TextureManager manager = Minecraft.getMinecraft().renderEngine;
			if (atlas == null) atlas = new DynamicTextureAtlas(manager, 64);
			atlas.clearCells();

			List<PlaneData> planes = Lists.newArrayList();

			createXZPlanes(map, planes);
			if (!planes.isEmpty()) atlas.compile();
			return planes;
		}

		private void createXZPlanes(HeightMapData map, List<PlaneData> planes) {
			int[][] levels = new int[256][];

			for (HeightMapData.LayerData layer : map.layers)
				for (int x = 0; x < 64; x++)
					for (int y = 0; y < 64; y++) {
						int index = 64 * y + x;
						byte color = layer.colorMap[index];

						if (color == 0) continue;
						// stupid signed bytes
						int height = layer.heightMap[index] & 0xFF;

						int fullColor = MapColor.COLORS[color].colorValue;
						int[] plane = getPlane(levels, height);
						plane[index] = fullColor | (layer.alpha << 24);
					}

			createPlanes(planes, levels, PlaneOrientation.XZ);
		}

		public static int[] getPlane(int[][] levels, int height) {
			int[] level = levels[height];

			if (level == null) {
				level = new int[64 * 64];
				levels[height] = level;
			}

			return level;
		}

		private void createPlanes(List<PlaneData> planes, int[][] levels, PlaneOrientation orientation) {
			for (int z = 0; z < levels.length; z++) {
				int[] level = levels[z];
				if (level != null) {
					PlaneData plane = new PlaneData();
					plane.orientation = orientation;
					plane.param = z;
					plane.texture = atlas.allocateCell();
					plane.texture.setPixels(level);
					planes.add(plane);
				}
			}
		}

		private void compileDisplayList(List<PlaneData> planes) {
			if (displayList == null) displayList = GL11.glGenLists(1);

			GL11.glNewList(displayList, GL11.GL_COMPILE);

			final Tessellator tes = new Tessellator(4 * (3 + 2) * 4 * 2);
			BufferBuilder wr = tes.getBuffer();

			wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			for (PlaneData plane : planes) {
				final AtlasCell tex = plane.texture;
				final double param = PLANE_HEIGHT * plane.param;
				switch (plane.orientation) {
					case XZ: {
						wr.pos(0, param, 0).tex(tex.minU, tex.minV).endVertex();
						wr.pos(0, param, 1).tex(tex.minU, tex.maxV).endVertex();
						wr.pos(1, param, 1).tex(tex.maxU, tex.maxV).endVertex();
						wr.pos(1, param, 0).tex(tex.maxU, tex.minV).endVertex();
						break;
					}
					case XY: {
						wr.pos(0, 0, param).tex(tex.minU, tex.minV).endVertex();
						wr.pos(0, 1, param).tex(tex.minU, tex.maxV).endVertex();
						wr.pos(1, 1, param).tex(tex.maxU, tex.maxV).endVertex();
						wr.pos(1, 0, param).tex(tex.maxU, tex.minV).endVertex();
						break;
					}
					case YZ: {
						wr.pos(param, 0, 0).tex(tex.minU, tex.minV).endVertex();
						wr.pos(param, 1, 0).tex(tex.minU, tex.maxV).endVertex();
						wr.pos(param, 1, 1).tex(tex.maxU, tex.maxV).endVertex();
						wr.pos(param, 0, 1).tex(tex.maxU, tex.minV).endVertex();
						break;
					}
					default:
						break;
				}

			}
			tes.draw();
			GL11.glEndList();
		}

		public boolean needsUpdate(HeightMapData map) {
			return System.identityHashCode(map) != renderedDataHash;
		}

		public void update(HeightMapData map) {
			List<PlaneData> planes = updateMapTexture(map);
			compileDisplayList(planes);
			renderedDataHash = System.identityHashCode(map);
		}

		public void render() {
			Preconditions.checkNotNull(displayList, "Display list not compiled");
			atlas.bind();

			GlStateManager.disableCull();
			GlStateManager.enableBlend();

			GL11.glCallList(displayList);

			GlStateManager.enableCull();
			GlStateManager.disableBlend();
		}
	}

	private final Map<Integer, MapRenderData> cache = Maps.newHashMap();

	@Override
	protected void finalize() {
		for (MapRenderData data : cache.values())
			data.free();
	}

	public void render(int mapId, HeightMapData data) {
		MapRenderData renderData;
		synchronized (cache) {
			renderData = cache.get(mapId);

			if (renderData == null) {
				renderData = new MapRenderData();
				cache.put(mapId, renderData);
			}
		}

		if (renderData.needsUpdate(data)) renderData.update(data);

		renderData.render();
	}
}
