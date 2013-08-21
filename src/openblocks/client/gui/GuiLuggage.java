package openblocks.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import openblocks.OpenBlocks;
import openblocks.common.container.ContainerLuggage;
import openblocks.common.entity.EntityLuggage;

import org.lwjgl.opengl.GL11;

public class GuiLuggage extends GuiContainer {

	private EntityLuggage luggage;

	public GuiLuggage(ContainerLuggage container) {
		super(container);
		luggage = container.luggage;
		xSize = 176;
		ySize = luggage.isSpecial() ? 221 : 167;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		ySize = luggage.isSpecial() ? 221 : 167;
		guiTop = (this.height - this.ySize) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (this.width - this.xSize) / 2;
		int top = (this.height - this.ySize) / 2;
		this.mc.renderEngine.bindTexture(OpenBlocks.getTexturesPath(luggage.isSpecial() ? "gui/luggage_special.png" : "gui/luggage.png"));
		this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String machineName = StatCollector.translateToLocal("openblocks.gui.luggage");
		int x = 8;
		fontRenderer.drawString(machineName, x, 6, 4210752);
		String translatedName = StatCollector.translateToLocal("container.inventory");
		fontRenderer.drawString(translatedName, 8, this.ySize - 96 + 2, 4210752);
	}

}
