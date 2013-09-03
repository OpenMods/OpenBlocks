package openblocks.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import openblocks.common.tileentity.TileEntityTank;

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
		teTank.getInternalTank().setFluid(null);
		if (item.hasTagCompound() && item.getTagCompound().hasKey("tank")) {
			teTank.readFromNBT(item.getTagCompound().getCompoundTag("tank"));
			FluidStack lstack = teTank.getInternalTank().getFluid();
			if (lstack != null
					&& FluidRegistry.getFluidName(lstack) != null) {
				teTank.setClientLiquidId(lstack.fluidID);
			}
		}
		TileEntityRenderer.instance.renderTileEntityAt(teTank, 0.0D, 0.0D, 0.0D, 0.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

}
