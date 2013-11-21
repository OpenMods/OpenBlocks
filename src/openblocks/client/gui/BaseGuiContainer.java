package openblocks.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.StatCollector;
import openblocks.client.gui.component.BaseComponent;
import openblocks.client.gui.component.GuiComponentPanel;
import openblocks.common.container.ContainerInventory;
import openmods.common.tileentity.SyncedTileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public abstract class BaseGuiContainer<T extends ContainerInventory<?>> extends
		GuiContainer {

	protected BaseComponent root;
	protected String name;

	public BaseGuiContainer(T container, int width, int height, String name) {
		super(container);
		xSize = width;
		ySize = height;
		root = createRoot();
		this.name = name;
	}

	protected BaseComponent createRoot() {
		return new GuiComponentPanel(0, 0, xSize, ySize, getContainer());
	}

	@SuppressWarnings("unchecked")
	public T getContainer() {
		return (T)inventorySlots;
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		root.mouseClicked(x - this.guiLeft, y - this.guiTop, button);
		syncChangesToServer();
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int button) {
		super.mouseMovedOrUp(x, y, button);
		root.mouseMovedOrUp(x - this.guiLeft, y - this.guiTop, button);
		syncChangesToServer();
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int button, long time) {
		super.mouseClickMove(mouseX, mouseY, button, time);
		root.mouseClickMove(mouseX - this.guiLeft, mouseY - this.guiTop, button, time);
	}

	private void syncChangesToServer() {
		Object te = getContainer().getOwner();
		if (te instanceof SyncedTileEntity) {
			((SyncedTileEntity)te).sync();
		}
	}

	public void preRender(float mouseX, float mouseY) {
		root.mouseMovedOrUp((int)mouseX - this.guiLeft, (int)mouseY - this.guiTop, -1);
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);
		root.keyTyped(par1, par2);
	}

	public void postRender(int mouseX, int mouseY) {}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		this.preRender(mouseX, mouseY);
		GL11.glPushMatrix();
		GL11.glTranslated(this.guiLeft, this.guiTop, 0);
		BaseComponent.IS_OVERLAY_PASS = false;
		root.render(this.mc, 0, 0, mouseX - this.guiLeft, mouseY - this.guiTop);
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.postRender(mouseX, mouseY);
		String machineName = StatCollector.translateToLocal(name);
		int x = this.xSize / 2 - (fontRenderer.getStringWidth(machineName) / 2);
		fontRenderer.drawString(machineName, x, 6, 4210752);
		String translatedName = StatCollector.translateToLocal("container.inventory");
		fontRenderer.drawString(translatedName, 8, this.ySize - 96 + 2, 4210752);

	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);        
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glPushMatrix();
		BaseComponent.IS_OVERLAY_PASS = true;
		root.render(this.mc, this.guiLeft, this.guiTop, par1 - this.guiLeft, par2 - this.guiTop);
		GL11.glPopMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
	}

	public void sendButtonClick(int buttonId) {
		this.mc.playerController.sendEnchantPacket(getContainer().windowId, buttonId);
	}

}
