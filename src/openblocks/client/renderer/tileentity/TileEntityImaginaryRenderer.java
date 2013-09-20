package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import openblocks.Config;
import openblocks.OpenBlocks.Blocks;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityImaginary.Property;

import org.lwjgl.opengl.GL11;

public class TileEntityImaginaryRenderer extends TileEntitySpecialRenderer {
	
	private Integer pencilDisplayList;
	private Integer crayonDisplayList;
	
	public static void drawCube(Tessellator tes, Icon icon) {
		final double delta0 = 0.001;
		final double delta1 = 1 - 0.001;
		
		tes.addVertexWithUV(0, 0, delta0, icon.getMinU(), icon.getMinV());
		tes.addVertexWithUV(0, 1, delta0, icon.getMinU(), icon.getMaxV());
		tes.addVertexWithUV(1, 1, delta0, icon.getMaxU(), icon.getMaxV());
		tes.addVertexWithUV(1, 0, delta0, icon.getMaxU(), icon.getMinV());
		
		tes.addVertexWithUV(0, 0, delta1, icon.getMinU(), icon.getMinV());
		tes.addVertexWithUV(1, 0, delta1, icon.getMinU(), icon.getMaxV());
		tes.addVertexWithUV(1, 1, delta1, icon.getMaxU(), icon.getMaxV());
		tes.addVertexWithUV(0, 1, delta1, icon.getMaxU(), icon.getMinV());
		
		tes.addVertexWithUV(delta1, 0, 0, icon.getMinU(), icon.getMinV());
		tes.addVertexWithUV(delta1, 1, 0, icon.getMinU(), icon.getMaxV());
		tes.addVertexWithUV(delta1, 1, 1, icon.getMaxU(), icon.getMaxV());
		tes.addVertexWithUV(delta1, 0, 1, icon.getMaxU(), icon.getMinV());
		
		tes.addVertexWithUV(delta0, 0, 0, icon.getMinU(), icon.getMinV());
		tes.addVertexWithUV(delta0, 0, 1, icon.getMinU(), icon.getMaxV());
		tes.addVertexWithUV(delta0, 1, 1, icon.getMaxU(), icon.getMaxV());
		tes.addVertexWithUV(delta0, 1, 0, icon.getMaxU(), icon.getMinV());
		
		tes.addVertexWithUV(0, delta1, 0, icon.getMinU(), icon.getMinV());
		tes.addVertexWithUV(0, delta1, 1, icon.getMinU(), icon.getMaxV());
		tes.addVertexWithUV(1, delta1, 1, icon.getMaxU(), icon.getMaxV());
		tes.addVertexWithUV(1, delta1, 0, icon.getMaxU(), icon.getMinV());
		
		tes.addVertexWithUV(0, delta0, 0, icon.getMinU(), icon.getMinV());
		tes.addVertexWithUV(1, delta0, 0, icon.getMinU(), icon.getMaxV());
		tes.addVertexWithUV(1, delta0, 1, icon.getMaxU(), icon.getMaxV());
		tes.addVertexWithUV(0, delta0, 1, icon.getMaxU(), icon.getMinV());
	}
	

	private static int createList(Icon texture) {
		int displayList = GL11.glGenLists(1);
		
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		Tessellator tes = new Tessellator();
		tes.startDrawingQuads();
		drawCube(tes, texture);
		tes.draw();
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		
		GL11.glEndList();
		
		return displayList;
	}
	
	private int getCrayonDisplayList() {
		if (crayonDisplayList == null)
			crayonDisplayList = createList(Blocks.imaginary.textureCrayon);
		
		return crayonDisplayList;
	}
	
	private int getPencilDisplayList() {
		if (pencilDisplayList == null)
			pencilDisplayList = createList(Blocks.imaginary.texturePencil);
		
		return pencilDisplayList;
	}
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTicks) {
		final TileEntityImaginary te = (TileEntityImaginary)tileentity;
		
		boolean isVisible = te.is(Property.VISIBLE);
		
		if (isVisible && te.visibility < 1)
			te.visibility = Math.min(te.visibility + Config.imaginaryFadingSpeed, 1);
		else if (!isVisible && te.visibility > 0)
			te.visibility = Math.max(te.visibility - Config.imaginaryFadingSpeed, 0);
		
		if (te.visibility <= 0)
			return;
		
		bindTexture(TextureMap.locationBlocksTexture);
		
		int displayList;
		if (!te.isPencil()) {
			displayList = getCrayonDisplayList();
			byte red = (byte)(te.color >> 16);   
			byte green = (byte)(te.color >> 8);
			byte blue = (byte)(te.color >> 0);
			GL11.glColor4ub(red, green, blue, (byte)(255 * te.visibility));
		} else {
			displayList = getPencilDisplayList();
			GL11.glColor4ub((byte)255, (byte)255, (byte)255, (byte)(255 * te.visibility));
		}
		
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
		GL11.glCallList(displayList);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glPopMatrix();
	}

	@Override
	protected void finalize() throws Throwable {
		if (crayonDisplayList != null)
			GL11.glDeleteLists(crayonDisplayList, 1);
		
		if (pencilDisplayList != null)
			GL11.glDeleteLists(pencilDisplayList, 1);
	}

	
}
