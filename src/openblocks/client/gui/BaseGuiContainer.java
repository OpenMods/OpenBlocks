package openblocks.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.StatCollector;
import openblocks.client.gui.component.GuiComponentPanel;
import openblocks.common.container.ContainerInventory;
import openblocks.common.tileentity.NetworkedTileEntity;

import org.lwjgl.opengl.GL11;

public abstract class BaseGuiContainer<T extends ContainerInventory<?>> extends
		GuiContainer {

	private T container;

	protected GuiComponentPanel panel;
	protected String name;

	public BaseGuiContainer(T container, int width, int height, String name) {
		super(container);
		this.container = container;
		xSize = width;
		ySize = height;
		panel = new GuiComponentPanel(0, 0, xSize, ySize, container);
		this.name = name;
	}

	public T getContainer() {
		return container;
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		panel.mouseClicked(x - this.guiLeft, y - this.guiTop, button);
		syncChangesToServer();
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int button) {
		super.mouseMovedOrUp(x, y, button);
		panel.mouseMovedOrUp(x - this.guiLeft, y - this.guiTop, button);
		syncChangesToServer();
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int button, long time) {
		super.mouseClickMove(mouseX, mouseY, button, time);
		panel.mouseClickMove(mouseX - this.guiLeft, mouseY - this.guiTop, button, time);
	}

	private void syncChangesToServer() {
		Object te = getContainer().getOwner();
		if (te instanceof NetworkedTileEntity) {
			((NetworkedTileEntity)te).sync(false);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslated(this.guiLeft, this.guiTop, 0);
		panel.render(this.mc, 0, 0, mouseX - this.guiLeft, mouseY - this.guiTop);
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String machineName = StatCollector.translateToLocal(name);
		int x = this.xSize / 2 - (fontRenderer.getStringWidth(machineName) / 2);
		fontRenderer.drawString(machineName, x, 6, 4210752);
		String translatedName = StatCollector.translateToLocal("container.inventory");
		fontRenderer.drawString(translatedName, 8, this.ySize - 96 + 2, 4210752);
	}

}
