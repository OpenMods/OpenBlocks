package openblocks.client.renderer;

import openblocks.OpenBlocks;
import openblocks.common.block.BlockSpecialStainedClay;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeDirection;

public class ItemRendererSpecialStainedClay implements IItemRenderer {

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
		//GL11.glTranslated(-0.5, -0.5, -0.5);
		if (type != ItemRenderType.INVENTORY) {
			GL11.glTranslated(0.5, 0.5, -0.5);
		}
		RenderBlocks renderer = null;
		if (data.length > 0 && data[0] instanceof RenderBlocks) {
			renderer = (RenderBlocks) data[0];
			int color = BlockSpecialStainedClay.getColorFromNBT(itemstack);
			BlockRenderingHandler.renderInventoryBlock(renderer, OpenBlocks.Blocks.specialStainedClay, ForgeDirection.EAST, color);
		}
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

}
