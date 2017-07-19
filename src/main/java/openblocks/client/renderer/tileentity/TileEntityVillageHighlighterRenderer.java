package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelVillage;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openmods.sync.SyncableIntArray;
import openmods.utils.BlockUtils;
import org.lwjgl.opengl.GL11;

public class TileEntityVillageHighlighterRenderer extends TileEntitySpecialRenderer<TileEntityVillageHighlighter> {

	private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(-8, -3, -8, 8, 3, 8);
	private ModelVillage model = new ModelVillage();
	private static final ResourceLocation texture = OpenBlocks.location("textures/models/village.png");

	private static float N = 1500;

	@Override
	public void renderTileEntityAt(TileEntityVillageHighlighter vh, double x, double y, double z, float partialTick, int destroyProcess) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		if (vh != null && vh.isPowered()) {
			Tessellator t = Tessellator.getInstance();
			VertexBuffer wr = t.getBuffer();

			SyncableIntArray villages = vh.getVillageData();
			int[] data = villages.getValue();

			GL11.glPointSize(4.0F);
			GL11.glLineWidth(10F);
			GlStateManager.disableTexture2D();

			final float off = 2 / N;
			final float ratio = (float)(Math.PI * (3 - Math.sqrt(5)));

			wr.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
			for (int i = 0; i < data.length; i += TileEntityVillageHighlighter.VALUES_PER_VILLAGE) {
				int radius = data[i + 0];
				int vX = data[i + 1];
				int vY = data[i + 2];
				int vZ = data[i + 3];
				int id = data[i + 6];

				int color = id % 0xFFFFFF;
				float r = (color >> 16 & 255) / 255.0f;
				float g = (color >> 8 & 255) / 255.0f;
				float b = (color & 255) / 255.0f;

				for (int j = 0; j < N; j++) {
					float py = j * off - 1 + (off / 2);
					float rad = (float)Math.sqrt(1 - py * py);
					float phi = j * ratio;
					float px = MathHelper.cos(phi) * rad;
					float pz = MathHelper.sin(phi) * rad;
					wr.pos(vX + px * radius, vY + py * radius, vZ + pz * radius).color(r, g, b, 1.0f).endVertex();
				}
			}
			t.draw();

			wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

			for (int i = 0; i < data.length; i += TileEntityVillageHighlighter.VALUES_PER_VILLAGE) {
				int vX = data[i + 1];
				int vY = data[i + 2];
				int vZ = data[i + 3];

				AxisAlignedBB centerBox = BOUNDING_BOX.offset(vX, vY, vZ);
				drawBox(wr, centerBox);
			}

			t.draw();
			GlStateManager.enableTexture2D();
		}

		GlStateManager.color(1, 1, 1, 1);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		if (vh != null) GL11.glRotatef(-BlockUtils.getRotationFromOrientation(vh.getOrientation()), 0, 1, 0);
		bindTexture(texture);
		model.render();
		GL11.glPopMatrix();
	}

	private static void addVertex(VertexBuffer wr, double x, double y, double z) {
		wr.pos(x, y, z).endVertex();
	}

	public void drawBox(VertexBuffer wr, AxisAlignedBB bb) {
		// bottom
		addVertex(wr, bb.minX, bb.minY, bb.minZ);
		addVertex(wr, bb.maxX, bb.minY, bb.minZ);

		addVertex(wr, bb.maxX, bb.minY, bb.minZ);
		addVertex(wr, bb.maxX, bb.minY, bb.maxZ);

		addVertex(wr, bb.maxX, bb.minY, bb.maxZ);
		addVertex(wr, bb.minX, bb.minY, bb.maxZ);

		addVertex(wr, bb.minX, bb.minY, bb.maxZ);
		addVertex(wr, bb.minX, bb.minY, bb.minZ);

		// top
		addVertex(wr, bb.minX, bb.maxY, bb.minZ);
		addVertex(wr, bb.maxX, bb.maxY, bb.minZ);

		addVertex(wr, bb.maxX, bb.maxY, bb.minZ);
		addVertex(wr, bb.maxX, bb.maxY, bb.maxZ);

		addVertex(wr, bb.maxX, bb.maxY, bb.maxZ);
		addVertex(wr, bb.minX, bb.maxY, bb.maxZ);

		addVertex(wr, bb.minX, bb.maxY, bb.maxZ);
		addVertex(wr, bb.minX, bb.maxY, bb.minZ);

		// sides
		addVertex(wr, bb.minX, bb.minY, bb.minZ);
		addVertex(wr, bb.minX, bb.maxY, bb.minZ);

		addVertex(wr, bb.maxX, bb.minY, bb.minZ);
		addVertex(wr, bb.maxX, bb.maxY, bb.minZ);

		addVertex(wr, bb.maxX, bb.minY, bb.maxZ);
		addVertex(wr, bb.maxX, bb.maxY, bb.maxZ);

		addVertex(wr, bb.minX, bb.minY, bb.maxZ);
		addVertex(wr, bb.minX, bb.maxY, bb.maxZ);
	}

	@Override
	public boolean isGlobalRenderer(TileEntityVillageHighlighter te) {
		return te.isPowered();
	}

}
