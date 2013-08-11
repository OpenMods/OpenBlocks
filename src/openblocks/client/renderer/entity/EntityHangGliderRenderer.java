package openblocks.client.renderer.entity;

import openblocks.common.entity.EntityHangGlider;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

public class EntityHangGliderRenderer extends Render {

	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float f1) {

		EntityHangGlider glider = (EntityHangGlider) entity;
		
        GL11.glPushMatrix();

        float rotYawHead = interpolateRotation(glider.prevRotationYaw, glider.rotationYaw, f1);

        GL11.glTranslatef((float)x, (float)x, (float)z);
        GL11.glRotatef(180.0F - f, 0.0F, 1.0F, 0.0F);

        GL11.glRotatef(180.0F - f, 0.0F, -1.0F, 0.0F);
        GL11.glTranslatef((float)Math.sin(Math.toRadians(rotYawHead)), 0, -((float)Math.cos(Math.toRadians(rotYawHead))));
        GL11.glRotatef(180.0F - f, 0.0F, 1.0F, 0.0F);

        GL11.glScalef(3.0f, 3.0f, 3.0f);
        
    	this.renderManager.renderEngine.bindTexture("/mods/openblocks/textures/models/hangglider.png");

        GL11.glDisable(GL11.GL_CULL_FACE);
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.setColorRGBA(255, 255, 255, 255);
		t.addVertexWithUV( 0.8, 0,  0.5, 1, 1);
		t.addVertexWithUV( -0.8, 0, 0.5, 0, 1);
		t.addVertexWithUV(-0.8, 0, -0.5, 0, 0);
		t.addVertexWithUV(0.8, 0,  -0.5,  1, 0);
		t.draw();
        GL11.glEnable(GL11.GL_CULL_FACE);      
        GL11.glPopMatrix();
        
	}
	
    private float interpolateRotation(float par1, float par2, float par3)
    {
        float f3;

        for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return par1 + par3 * f3;
    }
}
