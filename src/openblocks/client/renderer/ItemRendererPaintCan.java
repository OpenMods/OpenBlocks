package openblocks.client.renderer;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemPaintCan;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemRendererPaintCan implements IItemRenderer {

	private Set<ForgeDirection> secondPassEnabledSides;

	public ItemRendererPaintCan() {
		secondPassEnabledSides = new HashSet<ForgeDirection>();
		secondPassEnabledSides.add(ForgeDirection.UP);
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack itemstack, Object... data) {
		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		// GL11.glTranslated(-0.5, -0.5, -0.5);
		if (type != ItemRenderType.INVENTORY) {
			GL11.glTranslated(0.5, 0.5, -0.5);
		}
		RenderBlocks renderer = null;
		if (data.length > 0 && data[0] instanceof RenderBlocks) {
			renderer = (RenderBlocks)data[0];
			int color = ItemPaintCan.getColorFromStack(itemstack);
			OpenBlocks.Blocks.paintCan.renderPass = 0;
			GL11.glPushMatrix();
			RenderUtils.renderInventoryBlock(renderer, OpenBlocks.Blocks.paintCan, ForgeDirection.EAST, 0xFFFFFF);
			GL11.glPopMatrix();
			OpenBlocks.Blocks.paintCan.renderPass = 1;
			GL11.glPushMatrix();
			RenderUtils.renderInventoryBlock(renderer, OpenBlocks.Blocks.paintCan, ForgeDirection.EAST, color, secondPassEnabledSides);
			GL11.glPopMatrix();
		}
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

}
