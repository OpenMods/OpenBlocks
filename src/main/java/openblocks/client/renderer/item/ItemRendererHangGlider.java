package openblocks.client.renderer.item;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import openblocks.common.entity.EntityHangGlider;
import openmods.utils.TextureUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemRendererHangGlider implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
		EntityLivingBase par1EntityLiving = (EntityLivingBase)data[1];
		if (EntityHangGlider.isEntityHoldingGlider(par1EntityLiving)) return;
		IIcon icon = par1EntityLiving.getItemIcon(stack, 0);

		if (icon == null) return;

		if (stack.getItemSpriteNumber() == 0) TextureUtils.bindDefaultTerrainTexture();
		else TextureUtils.bindDefaultItemsTexture();

		Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef(-0.0F, -0.3F, 0.0F);
		GL11.glScalef(1.5F, 1.5F, 1.5F);
		GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
		GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
		ItemRenderer.renderItemIn2D(tessellator,
				icon.getMaxU(), icon.getMinV(),
				icon.getMinU(), icon.getMaxV(),
				icon.getIconWidth(), icon.getIconHeight(),
				0.0625F);

	}
}
