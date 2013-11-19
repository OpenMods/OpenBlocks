package openblocks.client;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemSleepingBag;
import openblocks.physics.Cloth;
import openblocks.physics.FastVector;
import openblocks.utils.CompatibilityUtils;

public class PlayerRenderEventHandler {

	public static final boolean CLOTH_CAPES_ENABLED = true;
	public static final HashMap<String, Cloth> PLAYER_CAPE_PHYSICS = new HashMap<String, Cloth>();
	
	@ForgeSubscribe
	public void onPrePlayerRender(RenderPlayerEvent.Pre event) {
		if (OpenBlocks.Items.sleepingBag != null && event.entityPlayer != null) {
			if (event.entityPlayer.isPlayerSleeping() && ItemSleepingBag.isWearingSleepingBag(event.entityPlayer)) {
				event.entityPlayer.yOffset = .7f;
			}
		}
	}
	
	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(CLOTH_CAPES_ENABLED && event.entity instanceof EntityPlayer) {
			PLAYER_CAPE_PHYSICS.put(((EntityPlayer)event.entity).username, new Cloth(15, 15, 15f));
		}
	}
	
	@ForgeSubscribe
	public void onPrePlayerSpecialRender(RenderPlayerEvent.Specials.Pre event) {
		/* Disable render of normal cape */
	}
	
	@ForgeSubscribe
	public void onPostPlayerSpecialRender(RenderPlayerEvent.Specials.Post event) {
		/* Render the cape */
		renderCape(event.renderer, event.entityPlayer, event.partialRenderTick);
	}

	private void renderCape(RenderPlayer renderer, EntityPlayer entityPlayer, float partialTick) {
		Cloth cloth = null;
		String username = entityPlayer.username;
		if(PLAYER_CAPE_PHYSICS.containsKey(username)) {
			cloth = PLAYER_CAPE_PHYSICS.get(username);
		}
		if(cloth != null) {
			//RenderHelper.disableStandardItemLighting();
			GL11.glPushMatrix();
			GL11.glTranslatef(0F, 0.0F, -1.0F / 16);
			GL11.glTranslatef(-0.5F, 0.0F, 0.2F);
			Tessellator t = Tessellator.instance;
			{
				GL11.glPushMatrix();
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glColor4f(0.1f,0.1f,0.1f, 1);
				
				//GL11.glDisable(GL11.GL_TEXTURE);
				//CompatibilityUtils.bindTextureToClient("textures/models/hangglider.png");
				
				t.startDrawingQuads();
				for(int i = 0; i < cloth.points.length-1; i++) {
					for(int j = 0; j < cloth.points[i].length-1; j++) {
						FastVector tr = cloth.points[i][j].getCurrent();
						FastVector tl = cloth.points[i][j+1].getCurrent();
						FastVector br = cloth.points[i+1][j].getCurrent();
						FastVector bl = cloth.points[i+1][j+1].getCurrent();
						t.addVertexWithUV(tr.x, tr.y*1.5, tr.z, (double)i/(double)cloth.points.length, (j+1D)/(double)cloth.points[i].length);
						t.addVertexWithUV(tl.x, tl.y*1.5, tl.z, (double)i/(double)cloth.points.length, (double)(j)/(double)cloth.points[i].length);
						t.addVertexWithUV(bl.x, bl.y*1.5, bl.z, (i+1D)/(double)cloth.points.length, (double)(j)/(double)cloth.points[i].length);
						//t.addVertex(tr.x, tr.y, tr.z);
						//t.addVertex(bl.x, bl.y, bl.z);
						t.addVertexWithUV(br.x, br.y*1.5, br.z, (1D+i)/(double)cloth.points.length, (j+1D)/(double)cloth.points[i].length);
					}
				}
				double motionX = entityPlayer.motionX * partialTick;
				double motionY = entityPlayer.onGround ? 0 : entityPlayer.motionY * partialTick;
				double motionZ = entityPlayer.motionZ * partialTick;
				double horizontal = Math.sqrt(motionX * motionX + motionZ * motionZ);
				double rotation = Math.toRadians(entityPlayer.rotationYaw);
				cloth.getClosestPoint(new FastVector(0.5, 0.5, 0)).applyForce(new FastVector(Math.sin(rotation) * horizontal, motionY * 0.06, Math.cos(rotation) * horizontal));
				t.draw();
				//GL11.glEnable(GL11.GL_TEXTURE);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glPopMatrix();
				//RenderHelper.enableStandardItemLighting();
			}
			GL11.glPopMatrix();
		}		
	}
	
	
}
