package openmods.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public class GuiComponentTankLevel extends GuiComponentBox {

	protected IFluidTank tank;

	public GuiComponentTankLevel(int x, int y, int width, int height, IFluidTank tank) {
		super(x, y, width, height, 0, 0, 0xc6c6c6);
		this.tank = tank;
	}

	@Override
	public void render(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.render(minecraft, offsetX, offsetY, mouseX, mouseY);
		FluidStack stack = tank.getFluid();
		double percentFull = Math.max(0, Math.min(1, (double)tank.getFluidAmount()
				/ (double)tank.getCapacity()));
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
				tessellator.addVertexWithUV(offsetX + x + width - 3, offsetY
						+ y + height - 3, this.zLevel, icon.getMaxU(), icon.getMaxV());
				tessellator.addVertexWithUV(offsetX + x + width - 3, offsetY
						+ y + (height - fluidHeight), this.zLevel, icon.getMaxU(), icon.getMinV());
				tessellator.addVertexWithUV(offsetX + x + 3, offsetY + y
						+ (height - fluidHeight), this.zLevel, icon.getMinU(), icon.getMinV());
				tessellator.draw();
			}
		}
	}

}
