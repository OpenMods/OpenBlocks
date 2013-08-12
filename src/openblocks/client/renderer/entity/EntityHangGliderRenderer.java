package openblocks.client.renderer.entity;

import openblocks.common.entity.EntityHangGlider;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

public class EntityHangGliderRenderer extends Render {

	private static final float QUAD_HALF_SIZE = 2.4f;
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float f1) {

		EntityHangGlider glider = (EntityHangGlider) entity;

        GL11.glPushMatrix();
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            GL11.glTranslatef((float)x, (float)y+0.4f, (float)z);
        }else {
            GL11.glTranslatef((float)x, (float)y-0.2f, (float)z);
        }

        float rotation = this.interpolateRotation(glider.prevRotationYaw, glider.rotationYaw, f1);        
        // Maybe this should be pushed in to the matrix and popped out too
        GL11.glRotatef(180.0F - rotation, 0.0F, 1.0F, 0.0F);
        
        // Push matrix to hold it's location for rendering other stuff */
        GL11.glPushMatrix();		
    	this.renderManager.renderEngine.bindTexture("/mods/openblocks/textures/models/hangglider.png");
    	renderGlider();
    	GL11.glPopMatrix();
    	
    	// Render other stuff here if you wish
        GL11.glPopMatrix();        
	}
	
	private void renderGlider() {
        GL11.glDisable(GL11.GL_CULL_FACE);
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.setColorRGBA(255, 255, 255, 255);
		t.addVertexWithUV(QUAD_HALF_SIZE, 0, QUAD_HALF_SIZE, 1, 1);
		t.addVertexWithUV(-QUAD_HALF_SIZE, 0, QUAD_HALF_SIZE, 0, 1);
		t.addVertexWithUV(-QUAD_HALF_SIZE, 0, -QUAD_HALF_SIZE, 0, 0);
		t.addVertexWithUV(QUAD_HALF_SIZE, 0, -QUAD_HALF_SIZE, 1, 0);
		t.draw();
        GL11.glEnable(GL11.GL_CULL_FACE);    
	}
	
	
	
	/* Interpolate rotation */
    private float interpolateRotation(float prevRotation, float nextRotation, float modifier)
    {
        float rotation;

        for (rotation = nextRotation - prevRotation; rotation < -180.0F; rotation += 360.0F)
        {
            ;
        }

        while (rotation >= 180.0F)
        {
            rotation -= 360.0F;
        }

        return prevRotation + modifier * rotation;
    }
}
