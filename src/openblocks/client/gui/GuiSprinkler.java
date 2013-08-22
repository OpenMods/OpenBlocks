package openblocks.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import openblocks.OpenBlocks;
import openblocks.common.container.ContainerSprinkler;
import openblocks.common.tileentity.TileEntitySprinkler;

import org.lwjgl.opengl.GL11;

public class GuiSprinkler extends GuiContainer {

	private TileEntitySprinkler sprinkler;

	public GuiSprinkler(ContainerSprinkler container) {
		super(container);
		sprinkler = container.getTileEntity();
		xSize = 176;
		ySize = 167;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (this.width - this.xSize) / 2;
		int top = (this.height - this.ySize) / 2;
		this.mc.renderEngine.bindTexture(OpenBlocks.getTexturesPath("gui/sprinkler.png"));
		this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String machineName = StatCollector.translateToLocal("openblocks.gui.sprinkler");
		int x = this.xSize / 2 - (fontRenderer.getStringWidth(machineName) / 2);
		fontRenderer.drawString(machineName, x, 6, 4210752);
		String translatedName = StatCollector.translateToLocal("container.inventory");
		fontRenderer.drawString(translatedName, 8, this.ySize - 96 + 2, 4210752);
	}

}
