package openblocks.client.renderer;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;
import openblocks.common.entity.EntityHangGlider;
import openblocks.utils.CompatibilityUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemRendererHangGlider implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.EQUIPPED
				|| type == ItemRenderType.EQUIPPED_FIRST_PERSON;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack par2ItemStack, Object... data) {
		EntityLivingBase par1EntityLiving = (EntityLivingBase)data[1];
		if (EntityHangGlider.isEntityHoldingGlider(par1EntityLiving)) return;
		Icon icon = par1EntityLiving.getItemIcon(par2ItemStack, 0);

		if (icon == null) {
			GL11.glPopMatrix();
			return;
		}

		if (par2ItemStack.getItemSpriteNumber() == 0) {
			CompatibilityUtils.bindDefaultTerrainTexture();
		} else {
			CompatibilityUtils.bindDefaultItemsTexture();
		}

		Tessellator tessellator = Tessellator.instance;
		float f = icon.getMinU();
		float f1 = icon.getMaxU();
		float f2 = icon.getMinV();
		float f3 = icon.getMaxV();
		float f4 = 0.0F;
		float f5 = 0.3F;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef(-f4, -f5, 0.0F);
		float f6 = 1.5F;
		GL11.glScalef(f6, f6, f6);
		GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
		GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
		ItemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getOriginX(), icon.getOriginY(), 0.0625F);

	}
}
