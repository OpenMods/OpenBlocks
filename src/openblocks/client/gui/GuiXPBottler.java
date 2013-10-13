package openblocks.client.gui;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import openblocks.OpenBlocks;
import openblocks.client.gui.component.*;
import openblocks.common.container.ContainerXPBottler;

import org.lwjgl.opengl.GL11;

public class GuiXPBottler extends BaseGuiContainer<ContainerXPBottler> {

	private GuiComponentTankLevel xpLevel;
	private GuiComponentTabs tabs;
	private GuiComponentPanel main;
	private GuiComponentTab glassInputTab;
	private GuiComponentTab xpInputTab;
	private GuiComponentProgress progress;
	private GuiComponentSideSelector glassSideSelector;
	private GuiComponentSideSelector xpSideSelector;

	public GuiXPBottler(ContainerXPBottler container) {
		super(container);

		xSize = 176;
		ySize = 151;
		progress = new GuiComponentProgress(72, 33);
		main = new GuiComponentPanel(0, 0, xSize, ySize, container);
		xpLevel = new GuiComponentTankLevel(140, 18, 17, 37);
		xpLevel.setFluidStack(new FluidStack(OpenBlocks.Fluids.openBlocksXPJuice, 1));

		tabs = new GuiComponentTabs(xSize - 3, 4);
		glassSideSelector = new GuiComponentSideSelector(30, 30, 40.0, OpenBlocks.Blocks.xpBottler, container.getTileEntity().getGlassSides(), new ISideSelectionCallback() {
			@Override
			public void onSideSelected(ForgeDirection direction) {
				getContainer().sendButtonClick(direction.ordinal());
			}
		});
		xpSideSelector = new GuiComponentSideSelector(30, 30, 40.0, OpenBlocks.Blocks.xpBottler, container.getTileEntity().getXPSides(), new ISideSelectionCallback() {
			@Override
			public void onSideSelected(ForgeDirection direction) {
				getContainer().sendButtonClick(direction.ordinal() + 7);
			}
		});

		glassInputTab = new GuiComponentTab(0xe4b9b0, new ItemStack(Item.glassBottle, 1), 100, 100);
		glassInputTab.addComponent(glassSideSelector);
		glassInputTab.addComponent(glassSideSelector);

		xpInputTab = new GuiComponentTab(0xd2e58f, new ItemStack(Item.expBottle), 100, 100);
		xpInputTab.addComponent(xpSideSelector);
		xpInputTab.addComponent(xpSideSelector);

		tabs.addComponent(glassInputTab);
		tabs.addComponent(xpInputTab);

		main.addComponent(xpLevel);
		main.addComponent(tabs);
		main.addComponent(progress);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslated(this.guiLeft, this.guiTop, 0);
		xpLevel.setPercentFull(getContainer().getTileEntity().getXPBufferRatio());
		progress.setProgress(getContainer().getTileEntity().getProgressRatio());
		main.render(this.mc, 0, 0, mouseX - this.guiLeft, mouseY - this.guiTop);
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String machineName = StatCollector.translateToLocal("openblocks.gui.xpbottler");
		int x = this.xSize / 2 - (fontRenderer.getStringWidth(machineName) / 2);
		fontRenderer.drawString(machineName, x, 6, 4210752);
		String translatedName = StatCollector.translateToLocal("container.inventory");
		fontRenderer.drawString(translatedName, 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		main.mouseClicked(x - this.guiLeft, y - this.guiTop, button);
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int button) {
		super.mouseMovedOrUp(x, y, button);
		main.mouseMovedOrUp(x - this.guiLeft, y - this.guiTop, button);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int button, long time) {
		super.mouseClickMove(mouseX, mouseY, button, time);
		main.mouseClickMove(mouseX - this.guiLeft, mouseY - this.guiTop, button, time);
	}

}
