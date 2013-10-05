package openblocks.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import openblocks.OpenBlocks;
import openblocks.client.gui.component.GuiComponentTankLevel;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.utils.CompatibilityUtils;

import org.lwjgl.opengl.GL11;

public class GuiVacuumHopper extends GuiContainer {

	private GuiComponentTankLevel xpLevel;
	
	public GuiVacuumHopper(ContainerVacuumHopper container) {
		super(container);
		xSize = 176;
		ySize = 151;
		xpLevel = new GuiComponentTankLevel(140, 18, 17, 37);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (this.width - this.xSize) / 2;
		int top = (this.height - this.ySize) / 2;
		CompatibilityUtils.bindTextureToClient("textures/gui/vacuumhopper.png");
		drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String machineName = StatCollector.translateToLocal("openblocks.gui.vacuumhopper");
		int x = this.xSize / 2 - (fontRenderer.getStringWidth(machineName) / 2);
		fontRenderer.drawString(machineName, x, 6, 4210752);
		String translatedName = StatCollector.translateToLocal("container.inventory");
		fontRenderer.drawString(translatedName, 8, this.ySize - 96 + 2, 4210752);
		xpLevel.render(this.mc, mouseX, mouseY, new FluidStack(OpenBlocks.Fluids.openBlocksXPJuice, 1), 0.5);
	}

}
