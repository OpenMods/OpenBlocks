package openblocks.client.gui.component;

import org.lwjgl.opengl.GL11;

import openblocks.utils.CompatibilityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class GuiComponentTankLevel extends GuiComponentBox {

	protected FluidStack stack;
	protected double percentFull;
	
	public GuiComponentTankLevel(int x, int y, int width, int height) {
		super(x,  y, width, height, 0, 0, 0xc6c6c6);
	}

	public void setPercentFull(double full) {
		this.percentFull = full;
	}
	
	public void setFluidStack(FluidStack stack) {
		this.stack = stack;
	}
	
	public void render(Minecraft minecraft, int mouseX, int mouseY) {
		super.render(minecraft, mouseX, mouseY);
		if (stack != null && stack.getFluid() != null) {
			minecraft.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			Icon icon = stack.getFluid().getIcon();
			if (icon != null) {
				double fluidHeight = (double)(height - 3) * percentFull;
				tessellator.addVertexWithUV((double)(x + 3), (double)(y
						+ height - 3), (double)this.zLevel, (double)icon.getMinU(), (double)icon.getMaxV());
				tessellator.addVertexWithUV((double)(x + width - 3), (double)(y
						+ height - 3), (double)this.zLevel, (double)icon.getMaxU(), (double)icon.getMaxV());
				tessellator.addVertexWithUV((double)(x + width - 3), (double)(y + (height - fluidHeight)), (double)this.zLevel, (double)icon.getMaxU(), (double)icon.getMinV());
				tessellator.addVertexWithUV((double)(x + 3), (double)(y + (height - fluidHeight)), (double)this.zLevel, (double)icon.getMinU(), (double)icon.getMinV());
				tessellator.draw();
			}
		}
	}

}
