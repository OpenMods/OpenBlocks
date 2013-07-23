package openblocks.client.gui;

import org.lwjgl.opengl.GL11;

import openblocks.common.container.ContainerLightbox;
import openblocks.common.tileentity.TileEntityLightbox;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.StatCollector;

public class GuiLightbox extends GuiContainer {

	private TileEntityLightbox lightbox;
	
	public GuiLightbox(ContainerLightbox container) {
		super(container);
		lightbox = container.getTileEntity();
		xSize = 176;
		ySize = 142;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int left = (this.width - this.xSize) / 2;
		int top = (this.height - this.ySize) / 2;
		this.mc.renderEngine.bindTexture("/mods/openblocks/textures/gui/lightbox.png");
		this.drawTexturedModalRect(left, top, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String machineName = StatCollector.translateToLocal("openblocks.gui.lightbox");
		int x = this.xSize / 2 - (fontRenderer.getStringWidth(machineName) / 2);
		fontRenderer.drawString(machineName, x, 6, 4210752);
		String translatedName = StatCollector.translateToLocal("container.inventory");
		fontRenderer.drawString(translatedName, 8, this.ySize - 96 + 2, 4210752);
	}
}
