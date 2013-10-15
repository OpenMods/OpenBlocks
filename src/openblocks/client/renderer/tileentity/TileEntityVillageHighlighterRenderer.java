package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelVillage;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openblocks.sync.SyncableIntArray;
import openblocks.utils.BlockUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

public class TileEntityVillageHighlighterRenderer extends
		TileEntitySpecialRenderer {

	private ModelVillage model = new ModelVillage();
	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/village.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		TileEntityVillageHighlighter villagehighlighter = (TileEntityVillageHighlighter)tileentity;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		if (villagehighlighter.isPowered()) {
			GL11.glPushMatrix();
			SyncableIntArray villages = villagehighlighter.getVillageData();
			int[] data = villages.getValue();
			for (int i = 0; i < data.length; i += TileEntityVillageHighlighter.VALUES_PER_VILLAGE) {
				int radius = data[i];
				int vX = data[i + 1];
				int vY = data[i + 2];
				int vZ = data[i + 3];
				int id = data[i + 6];
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glTranslated(vX, vY, vZ);
				GL11.glLineWidth(0.1f);
				int color = id % 0xFFFFFF;
				float r = (color >> 16 & 255) / 255.0F;
				float g = (color >> 8 & 255) / 255.0F;
				float b = (color & 255) / 255.0F;
				GL11.glColor4f(r, g, b, 0.5f);
				// TODO: draw center point here
				GL11.glPushMatrix();
				GL11.glRotatef(90, 1, 0, 0);
				Sphere s = new Sphere();
				s.setDrawStyle(GLU.GLU_LINE);
				s.draw(radius, 100, 100);
				GL11.glPopMatrix();
				drawBox(AxisAlignedBB.getAABBPool().getAABB(-radius, 4, -radius, radius, -4, radius));
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glPopMatrix();
			}
			GL11.glPopMatrix();
		}

		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-BlockUtils.getRotationFromDirection(villagehighlighter.getRotation()), 0, 1, 0);
		bindTexture(texture);
		model.render(villagehighlighter, f);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	public void drawBox(AxisAlignedBB bb) {

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawing(3);
		tessellator.addVertex(bb.minX, bb.minY, bb.minZ);
		tessellator.addVertex(bb.maxX, bb.minY, bb.minZ);
		tessellator.addVertex(bb.maxX, bb.minY, bb.maxZ);
		tessellator.addVertex(bb.minX, bb.minY, bb.maxZ);
		tessellator.addVertex(bb.minX, bb.minY, bb.minZ);
		tessellator.draw();
		tessellator.startDrawing(3);
		tessellator.addVertex(bb.minX, bb.maxY, bb.minZ);
		tessellator.addVertex(bb.maxX, bb.maxY, bb.minZ);
		tessellator.addVertex(bb.maxX, bb.maxY, bb.maxZ);
		tessellator.addVertex(bb.minX, bb.maxY, bb.maxZ);
		tessellator.addVertex(bb.minX, bb.maxY, bb.minZ);
		tessellator.draw();
		tessellator.startDrawing(1);
		tessellator.addVertex(bb.minX, bb.minY, bb.minZ);
		tessellator.addVertex(bb.minX, bb.maxY, bb.minZ);
		tessellator.addVertex(bb.maxX, bb.minY, bb.minZ);
		tessellator.addVertex(bb.maxX, bb.maxY, bb.minZ);
		tessellator.addVertex(bb.maxX, bb.minY, bb.maxZ);
		tessellator.addVertex(bb.maxX, bb.maxY, bb.maxZ);
		tessellator.addVertex(bb.minX, bb.minY, bb.maxZ);
		tessellator.addVertex(bb.minX, bb.maxY, bb.maxZ);
		tessellator.draw();
	}

}
