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

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiVacuumHopper extends GuiContainer {

	private GuiComponentTankLevel xpLevel;
	private GuiComponentTabs tabs;
	private GuiComponentPanel bg;

	public GuiVacuumHopper(ContainerVacuumHopper container) {
		super(container);
		xSize = 176;
		ySize = 151;
		bg = new GuiComponentPanel(0, 0, xSize, ySize, container);
		xpLevel = new GuiComponentTankLevel(140, 18, 17, 37);
		tabs = new GuiComponentTabs(xSize, 4);
		tabs.addTab(new GuiComponentTab(0xCC0000));
		tabs.addTab(new GuiComponentTab(0x00CC00));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY) {
		GL11.glPushMatrix();
		GL11.glTranslated(this.guiLeft, this.guiTop, 0);
		bg.render(this.mc, mouseX, mouseY);
		GL11.glPopMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String machineName = StatCollector.translateToLocal("openblocks.gui.vacuumhopper");
		int x = this.xSize / 2 - (fontRenderer.getStringWidth(machineName) / 2);
		fontRenderer.drawString(machineName, x, 6, 4210752);
		String translatedName = StatCollector.translateToLocal("container.inventory");
		fontRenderer.drawString(translatedName, 8, this.ySize - 96 + 2, 4210752);
		xpLevel.render(this.mc, mouseX, mouseY, new FluidStack(OpenBlocks.Fluids.openBlocksXPJuice, 1), 0.5);
		tabs.render(this.mc, mouseX, mouseY);
	}
	
}
