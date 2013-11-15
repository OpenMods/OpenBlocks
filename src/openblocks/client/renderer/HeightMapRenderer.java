package openblocks.client.renderer;

import java.util.List;
import java.util.Map;

import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import openblocks.Log;
import openblocks.client.renderer.DynamicTextureAtlas.AtlasCell;
import openblocks.common.HeightMapData;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
			Log.info("Layers: %d", planes.size());

			atlas.compile();
			return planes;
		}

		private void createXZPlanes(HeightMapData map, List<PlaneData> planes) {
			int[][] levels = new int[256][];

			for (HeightMapData.LayerData layer : map.layers)
				for (int x = 0; x < 64; x++)
					for (int y = 0; y < 64; y++) {
						int index = 64 * y + x;
						byte color = layer.colorMap[index];
						byte height = layer.heightMap[index];

						if (color == 0) continue;

						int fullColor = MapColor.mapColorArray[color].colorValue;
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

			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);

			Tessellator tes = new Tessellator();
			tes.startDrawingQuads();
			tes.setColorOpaque(255, 255, 255);

			for (PlaneData plane : planes) {
				final AtlasCell tex = plane.texture;
				final double param = PLANE_HEIGHT * plane.param;
				switch (plane.orientation) {
					case XZ: {
						tes.addVertexWithUV(0, param, 0, tex.minU, tex.minV);
						tes.addVertexWithUV(0, param, 1, tex.minU, tex.maxV);
						tes.addVertexWithUV(1, param, 1, tex.maxU, tex.maxV);
						tes.addVertexWithUV(1, param, 0, tex.maxU, tex.minV);
						break;
					}
					case XY: {
						tes.addVertexWithUV(0, 0, param, tex.minU, tex.minV);
						tes.addVertexWithUV(0, 1, param, tex.minU, tex.maxV);
						tes.addVertexWithUV(1, 1, param, tex.maxU, tex.maxV);
						tes.addVertexWithUV(1, 0, param, tex.maxU, tex.minV);
						break;
					}
					case YZ: {
						tes.addVertexWithUV(param, 0, 0, tex.minU, tex.minV);
						tes.addVertexWithUV(param, 1, 0, tex.minU, tex.maxV);
						tes.addVertexWithUV(param, 1, 1, tex.maxU, tex.maxV);
						tes.addVertexWithUV(param, 0, 1, tex.maxU, tex.minV);
						break;
					}
					default:
						break;
				}

			}
			tes.draw();

			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
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
			GL11.glCallList(displayList);
		}
	}

	private final Map<Integer, MapRenderData> cache = Maps.newHashMap();

	@Override
	protected void finalize() throws Throwable {
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
