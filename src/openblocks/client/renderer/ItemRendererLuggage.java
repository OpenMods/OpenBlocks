package openblocks.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import openblocks.common.entity.EntityLuggage;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemRendererLuggage implements IItemRenderer {

	private EntityLuggage luggage = new EntityLuggage(null);

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (Minecraft.getMinecraft().theWorld != null) {
			GL11.glPushMatrix();

			luggage.worldObj = Minecraft.getMinecraft().theWorld;

			GL11.glTranslatef(0.5f, 0, 0.5f);
			luggage.getInventory().clearAndSetSlotCount(27);
			if (item.hasTagCompound()) {
				luggage.getInventory().readFromNBT(item.getTagCompound());
			}
			Render renderer = RenderManager.instance.getEntityRenderObject(luggage);
			if (renderer.getFontRendererFromRenderManager() != null) {
				renderer.doRender(luggage, 0, 0, 0, 0, 0.5f);
			}
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		}
	}

}
