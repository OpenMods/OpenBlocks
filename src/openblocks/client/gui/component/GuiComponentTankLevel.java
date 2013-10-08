package openblocks.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.FluidStack;

public class GuiComponentTankLevel extends GuiComponentBox {

	protected FluidStack stack;
	protected double percentFull;

	public GuiComponentTankLevel(int x, int y, int width, int height) {
		super(x, y, width, height, 0, 0, 0xc6c6c6);
	}

	public void setPercentFull(double full) {
		this.percentFull = full;
	}

	public void setFluidStack(FluidStack stack) {
		this.stack = stack;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		if (stack != null && stack.getFluid() != null) {
			minecraft.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_F(1, 1, 1);
			Icon icon = stack.getFluid().getIcon();
			if (icon != null) {
				double fluidHeight = (height - 3) * percentFull;
				tessellator.addVertexWithUV(offsetX + x + 3, offsetY + y
						+ height - 3, this.zLevel, icon.getMinU(), icon.getMaxV());
				tessellator.addVertexWithUV(offsetX + x + width - 3, offsetY + y
						+ height - 3, this.zLevel, icon.getMaxU(), icon.getMaxV());
				tessellator.addVertexWithUV(offsetX + x + width - 3, offsetY + y + (height - fluidHeight), this.zLevel, icon.getMaxU(), icon.getMinV());
				tessellator.addVertexWithUV(offsetX + x + 3, offsetY + y + (height - fluidHeight), this.zLevel, icon.getMinU(), icon.getMinV());
				tessellator.draw();
			}
		}
	}

}
