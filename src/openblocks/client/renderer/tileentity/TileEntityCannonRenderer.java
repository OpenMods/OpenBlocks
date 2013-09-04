package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelCannon;
import openblocks.common.tileentity.TileEntityCannon;

import org.lwjgl.opengl.GL11;

public class TileEntityCannonRenderer extends TileEntitySpecialRenderer {
	
	private ModelCannon model = new ModelCannon();
	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/cannon.png");
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		TileEntityCannon cannon = (TileEntityCannon) tileentity;
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glPushMatrix();
		GL11.glTranslated(0.5, 1.0, 0.5);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		func_110628_a(texture);
		model.render(tileentity, f);
		GL11.glPopMatrix();
		if (cannon.renderLine) { 
		GL11.glPushMatrix();
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glColor4f(0, 0, 0, 0.2f);
		double motionX = cannon.motionX * 1.4;
		double motionY = cannon.motionY * 1.4;
		double motionZ = cannon.motionZ * 1.4;
		float posX = 0.5f;
		float posY = 0.5f;
		float posZ = 0.5f;
			for (int i = 0; i < 100; i++) {
				GL11.glVertex3f(posX, posY, posZ);
				motionY -= 0.03999999910593033D;
				posX += motionX;
				posY += motionY;
				posZ += motionZ;
				motionX *= 0.98;
				motionY *= 0.9800000190734863D;
				motionZ *= 0.98;
			}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
	}

}
