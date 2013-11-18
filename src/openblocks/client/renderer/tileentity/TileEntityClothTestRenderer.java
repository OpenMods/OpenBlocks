package openblocks.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;

import openblocks.common.tileentity.TileEntityClothTest;
import openblocks.physics.Cloth;
import openblocks.physics.FastVector;
import openblocks.physics.Point;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityClothTestRenderer extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float partial) {
		TileEntityClothTest clothTE = (TileEntityClothTest) tileentity;
		Cloth cloth = clothTE.cloth;
		if(cloth != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			Tessellator t = Tessellator.instance;
			{
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_BLEND);
				t.startDrawingQuads();
				t.setColorRGBA_F(0.2f, 0.2f, 1f, 0.9f);
				for(int i = 0; i < cloth.points.length-1; i++) {
					for(int j = 0; j < cloth.points[i].length-1; j++) {
						FastVector tl = cloth.points[i][j].getCurrent();
						FastVector tr = cloth.points[i][j+1].getCurrent();
						FastVector bl = cloth.points[i+1][j].getCurrent();
						FastVector br = cloth.points[i+1][j+1].getCurrent();
						t.addVertex(tr.x, tr.y, tr.z);
						t.addVertex(tl.x, tl.y, tl.z);
						t.addVertex(bl.x, bl.y, bl.z);
						//t.addVertex(tr.x, tr.y, tr.z);
						//t.addVertex(bl.x, bl.y, bl.z);
						t.addVertex(br.x, br.y, br.z);
					}
				}
				t.draw();
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glPopMatrix();
			}
			GL11.glPopMatrix();
		}
	}

}
