package openblocks.client.renderer.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import openblocks.common.entity.EntityLuggage;
import openmods.utils.LazyValue;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemRendererLuggage implements IItemRenderer {

	private final LazyValue<EntityLuggage> luggage = new LazyValue<EntityLuggage>() {
		@Override
		protected EntityLuggage initialize() {
			return new EntityLuggage(null);
		}
	};

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

			EntityLuggage luggage = this.luggage.get();
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
			RenderUtils.disableLightmap();
		}
	}

}
