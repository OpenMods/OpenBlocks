package openblocks.client.renderer.item;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemPaintCan;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemRendererPaintCan implements IItemRenderer {

	private static final Set<ForgeDirection> secondPassEnabledSides = EnumSet.of(ForgeDirection.UP);

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
		if (data.length > 0 && data[0] instanceof RenderBlocks) {
			if (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON) GL11.glTranslated(+0.5, +0.5, +0.5);

			RenderBlocks renderer = (RenderBlocks)data[0];
			int color = ItemPaintCan.getColorFromStack(itemstack);

			OpenBlocks.Blocks.paintCan.renderPass = 0;
			RenderUtils.renderInventoryBlock(renderer, OpenBlocks.Blocks.paintCan, 0, 0xFFFFFF);

			OpenBlocks.Blocks.paintCan.renderPass = 1;
			RenderUtils.renderInventoryBlock(renderer, OpenBlocks.Blocks.paintCan, 0, color, secondPassEnabledSides);

			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}

}
