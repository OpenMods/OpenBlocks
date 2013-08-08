package openblocks.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import openblocks.common.tileentity.tank.TileEntityTank;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemRendererTank implements IItemRenderer {

	private TileEntityTank teTank = new TileEntityTank();
	
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
		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslated(-0.5, -0.5, -0.5);
		if (type != ItemRenderType.INVENTORY) {
			GL11.glTranslated(0, 0.5, 0);
		}
		teTank.getInternalTank().clear();
		if (item.hasTagCompound()) {
			teTank.getInternalTank().readFromNBT(item.getTagCompound(), "tank");
		}
		TileEntityRenderer.instance.renderTileEntityAt(teTank, 0.0D, 0.0D, 0.0D, 0.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

}
