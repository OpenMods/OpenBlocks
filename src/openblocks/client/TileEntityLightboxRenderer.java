package openblocks.client;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityLightbox;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityLightboxRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderBlocks = new RenderBlocks();
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) {
		
		GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
			TileEntityLightbox lightbox = (TileEntityLightbox) tileentity;
			ForgeDirection surface = lightbox.getSurface();
			//ForgeDirection rotation = lightbox.getSurface();
			System.out.println(surface);
			if (surface == ForgeDirection.UP) { 
				GL11.glRotatef(90.0f, 1, 0, 0);
			}else if (surface == ForgeDirection.DOWN) { 
				GL11.glRotatef(-90.0f, 1, 0, 0);
			} else {
				GL11.glRotatef(90.0f * -surface.ordinal(), 0, 1, 0);
			}
			GL11.glPushMatrix();
				GL11.glDisable(2896);
				
				Tessellator t = Tessellator.instance;
				renderBlocks.setRenderBounds(0.8D, 0.0D, 0.0D, 1D, 1D, 1d);
				t.startDrawingQuads();
				t.setColorRGBA(255, 255, 255, 100);
				t.setBrightness(230);
				this.bindTextureByName("/mods/openblocks/textures/blocks/guide.png");
				Icon renderingIcon = OpenBlocks.Blocks.lightbox.getBlockTextureFromSide(0);
				renderBlocks.renderFaceXNeg(OpenBlocks.Blocks.lightbox, -0.5D, 0.0D, -0.5D, renderingIcon);
				renderBlocks.renderFaceXPos(OpenBlocks.Blocks.lightbox, -0.5D, 0.0D, -0.5D, renderingIcon);
				renderBlocks.renderFaceYNeg(OpenBlocks.Blocks.lightbox, -0.5D, 0.0D, -0.5D, renderingIcon);
				renderBlocks.renderFaceYPos(OpenBlocks.Blocks.lightbox, -0.5D, 0.0D, -0.5D, renderingIcon);
				renderBlocks.renderFaceZNeg(OpenBlocks.Blocks.lightbox, -0.5D, 0.0D, -0.5D, renderingIcon);
				renderBlocks.renderFaceZPos(OpenBlocks.Blocks.lightbox, -0.5D, 0.0D, -0.5D, renderingIcon);
				t.draw();
				
			GL11.glEnable(2896);
			GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
