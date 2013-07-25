package openblocks.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLiving;

public class EntityGhostRenderer extends RenderLiving {

	public EntityGhostRenderer() {
        super(new ModelBiped(0.0F), 0.5F);
	}
	
	public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9)
    {
		super.doRenderLiving(par1EntityLiving, par2, par4, par6, par8, par9);
    }
	
	
	
	protected void renderModel(EntityLiving living, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.loadDownloadableImageTexture(living.skinUrl, living.getTexture());
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
        this.mainModel.render(living, par2, par3, par4, par5, par6, par7);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        
    }
}
