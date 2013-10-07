package openblocks.client.gui;

import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import openblocks.OpenBlocks;
import openblocks.client.gui.component.*;
import openblocks.common.container.ContainerVacuumHopper;

import org.lwjgl.opengl.GL11;

public class GuiVacuumHopper extends BaseGuiContainer<ContainerVacuumHopper> {

	private GuiComponentTankLevel xpLevel;
	private GuiComponentTabs tabs;
	private GuiComponentPanel main;
	private GuiComponentSideSelector sideSelector;
	private GuiComponentTab xpTab;
	private GuiComponentLabel xpOutputsLabel;

	public GuiVacuumHopper(ContainerVacuumHopper container) {
		super(container);
		xSize = 176;
		ySize = 151;
		main = new GuiComponentPanel(0, 0, xSize, ySize, container);
		xpOutputsLabel = new GuiComponentLabel(24, 10, "XP Outputs:");
		xpLevel = new GuiComponentTankLevel(140, 18, 17, 37);
		xpLevel.setFluidStack(new FluidStack(OpenBlocks.Fluids.openBlocksXPJuice, 1));

		sideSelector = new GuiComponentSideSelector(30, 30, 40.0, container.getTileEntity().getXPOutputs(), new ISideSelectionCallback() {
			@Override
			public void onSideSelected(ForgeDirection direction) {
				getContainer().sendButtonClick(direction.ordinal());
			}
		});
		
		tabs = new GuiComponentTabs(xSize - 3, 4);
		xpTab = new GuiComponentTab(0xf6c3ae, Item.expBottle.getIconFromDamage(0), 100, 100);
		xpTab.addComponent(sideSelector);
		xpTab.addComponent(xpOutputsLabel);
		tabs.addComponent(xpTab);
		tabs.addComponent(new GuiComponentTab(0x9f95ae, Item.redstone.getIconFromDamage(0), 100, 100));

		main.addComponent(xpLevel);
		main.addComponent(tabs);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslated(this.guiLeft, this.guiTop, 0);
		xpLevel.setPercentFull(0.5);
		main.render(this.mc, 0, 0, mouseX - this.guiLeft, mouseY - this.guiTop);
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String machineName = StatCollector.translateToLocal("openblocks.gui.vacuumhopper");
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
