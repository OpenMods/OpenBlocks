package openblocks.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import openblocks.OpenBlocks;
import openblocks.client.gui.component.GuiComponentPanel;
import openblocks.client.gui.component.GuiComponentTab;
import openblocks.client.gui.component.GuiComponentTabs;
import openblocks.client.gui.component.GuiComponentTankLevel;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.utils.CompatibilityUtils;
import net.minecraft.item.Item;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiVacuumHopper extends GuiContainer {

	private GuiComponentTankLevel xpLevel;
	private GuiComponentTabs tabs;
	private GuiComponentPanel main;

	public GuiVacuumHopper(ContainerVacuumHopper container) {
		super(container);
		xSize = 176;
		ySize = 151;
		main = new GuiComponentPanel(0, 0, xSize, ySize, container);
		xpLevel = new GuiComponentTankLevel(140, 18, 17, 37);
		xpLevel.setFluidStack(new FluidStack(OpenBlocks.Fluids.openBlocksXPJuice, 1));
		
		
		tabs = new GuiComponentTabs(xSize -3, 4);
		tabs.addComponent(new GuiComponentTab(0xf6c3ae, Item.expBottle.getIconFromDamage(0), 100, 100));
		tabs.addComponent(new GuiComponentTab(0x9f95ae, Item.redstone.getIconFromDamage(0), 100, 100));
		
		main.addComponent(xpLevel);
		main.addComponent(tabs);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslated(this.guiLeft, this.guiTop, 0);
		xpLevel.setPercentFull(0.5);
		main.render(this.mc, mouseX, mouseY);
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
	protected void mouseClicked(int x, int y, int button){
		super.mouseClicked(x, y, button);
		main.mouseClicked(x - this.guiLeft, y - this.guiTop, button);
    }
	
}
