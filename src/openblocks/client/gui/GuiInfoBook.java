package openblocks.client.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import openblocks.OpenBlocks.Blocks;
import openblocks.client.gui.pages.*;
import openmods.gui.component.BaseComponent;
import openmods.gui.component.BaseComponent.IComponentListener;
import openmods.gui.component.GuiComponentSprite;
import openmods.utils.render.FakeIcon;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

public class GuiInfoBook extends GuiScreen {


	private GuiComponentSprite imgLeftBackground;
	private GuiComponentSprite imgRightBackground;

	public static Icon iconPageLeft = FakeIcon.createSheetIcon(-45, 0, -211, 180);
	public static Icon iconPageRight = FakeIcon.createSheetIcon(0, 0, 211, 180);

	private static final ResourceLocation texture = new ResourceLocation("openblocks:textures/gui/book.png");
	
	public ArrayList<BaseComponent> pages;
	
	public BaseComponent pageLeft = null;
	public BaseComponent pageRight = null;
	
	private int centerX;
	private int guiLeft;
	private int guiTop;
	
	private int index = 0;
	
	public GuiInfoBook() {
		
		imgLeftBackground = new GuiComponentSprite(0, 0, iconPageLeft, texture);
		imgRightBackground = new GuiComponentSprite(0, 0, iconPageRight, texture);
		
		pages = new ArrayList<BaseComponent>();
		
		pages.add(new BlankPage());
		pages.add(new IntroPage());
		pages.add(new StandardBlockPage("tile.openblocks.elevator.name", "openblocks.book.guide.description", new ItemStack(Blocks.elevator)));
		pages.add(new StandardBlockPage("tile.openblocks.sprinkler.name", "openblocks.book.sprinkler.description", new ItemStack(Blocks.sprinkler)));
		pages.add(new StandardBlockPage("tile.openblocks.beartrap.name", "openblocks.book.beartrap.description", new ItemStack(Blocks.bearTrap)));
		pages.add(new StandardBlockPage("tile.openblocks.guide.name", "openblocks.book.guide.description", new ItemStack(Blocks.guide)));
		pages.add(new StandardBlockPage("tile.openblocks.canvas.name", "openblocks.book.canvas.description", new ItemStack(Blocks.canvas)));
		pages.add(new StandardBlockPage("tile.openblocks.projector.name", "openblocks.book.projector.description", new ItemStack(Blocks.projector)));
		pages.add(new StandardBlockPage("tile.openblocks.vacuumhopper.name", "openblocks.book.vacuumhopper.description", new ItemStack(Blocks.vacuumHopper)));
		pages.add(new StandardBlockPage("tile.openblocks.tank.name", "openblocks.book.tank.description", new ItemStack(Blocks.tank)));
		pages.add(new StandardBlockPage("tile.openblocks.path.name", "openblocks.book.path.description", new ItemStack(Blocks.path)));
		pages.add(new StandardBlockPage("tile.openblocks.fan.name", "openblocks.book.fan.description", new ItemStack(Blocks.fan)));
		pages.add(new StandardBlockPage("tile.openblocks.blockbreaker.name", "openblocks.book.blockbreaker.description", new ItemStack(Blocks.blockBreaker)));
		pages.add(new StandardBlockPage("tile.openblocks.blockPlacer.name", "openblocks.book.blockplacer.description", new ItemStack(Blocks.blockPlacer)));
		pages.add(new StandardBlockPage("tile.openblocks.bigbutton.name", "openblocks.book.bigbutton.description", new ItemStack(Blocks.bigButton)));
		pages.add(new StandardBlockPage("tile.openblocks.autoanvil.name", "openblocks.book.autoanvil.description", new ItemStack(Blocks.autoAnvil)));
		pages.add(new StandardBlockPage("tile.openblocks.autoenchantmenttable.name", "openblocks.book.autoenchantmenttable.description", new ItemStack(Blocks.autoEnchantmentTable)));
		pages.add(new StandardBlockPage("tile.openblocks.sponge.name", "openblocks.book.sponge.description", new ItemStack(Blocks.sponge)));
		pages.add(new StandardBlockPage("tile.openblocks.heal.name", "openblocks.book.heal.description", new ItemStack(Blocks.heal)));
		pages.add(new StandardBlockPage("tile.openblocks.ropeladder.name", "openblocks.book.ropeladder.description", new ItemStack(Blocks.ropeLadder)));
		pages.add(new StandardBlockPage("tile.openblocks.villageHighlighter.name", "openblocks.book.villageHighlighter.description", new ItemStack(Blocks.villageHighlighter)));
		pages.add(new StandardBlockPage("tile.openblocks.xpBottler.name", "openblocks.book.xpBottler.description", new ItemStack(Blocks.xpBottler)));
		pages.add(new StandardBlockPage("tile.openblocks.xpDrain.name", "openblocks.book.xpDrain.description", new ItemStack(Blocks.xpDrain)));
		pages.add(new StandardBlockPage("tile.openblocks.drawingTable.name", "openblocks.book.drawingTable.description", new ItemStack(Blocks.drawingTable)));
		pages.add(new StandardBlockPage("tile.openblocks.paintCan.name", "openblocks.book.paintCan.description", new ItemStack(Blocks.paintCan)));
		pages.add(new StandardBlockPage("tile.openblocks.radio.name", "openblocks.book.radio.description", new ItemStack(Blocks.radio)));
		pages.add(new StandardBlockPage("tile.openblocks.grave.name", "openblocks.book.grave.description", new ItemStack(Blocks.grave)));
		
		for (BaseComponent page : pages) {
			page.setEnabled(false);
		}
		
		pageLeft = pages.get(0);
		pageRight = pages.get(1);
		
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (pageLeft != null)
			pageLeft.mouseClicked(x - this.guiLeft, y - this.guiTop, button);
		if (pageRight != null)
			pageRight.mouseClicked(x - centerX, y - this.guiTop, button);
		
		if (index > 0 && x > guiLeft && x < guiLeft + 20 && y > guiTop + 160 && y < guiTop + 180) {
			index -= 2;
		} else if (index < pages.size() - 2 && x > centerX + 191 && x < centerX + 211 && y > guiTop + 160 && y < guiTop + 180) {
			index += 2;
		}
		
		pageLeft = pages.get(index);
		pageRight = index + 1 < pages.size() ? pages.get(index + 1) : null;
		
	}
	
	public void drawScreen(int mouseX, int mouseY, float par3) {
		
		centerX = this.width / 2;
		guiLeft = centerX - 211;
		guiTop = (height - 200) / 2;
		
		imgLeftBackground.render(this.mc, guiLeft, guiTop, mouseX - guiLeft, mouseY - guiTop);
		imgRightBackground.render(this.mc, centerX, guiTop, mouseX - centerX, mouseY - guiTop);
		if (pageLeft != null) {
			pageLeft.setEnabled(true);
			pageLeft.render(this.mc, guiLeft, guiTop, mouseX - guiLeft, mouseY - guiTop);
		}
		if (pageRight != null) {
			pageRight.setEnabled(true);
			pageRight.render(this.mc, centerX, guiTop, mouseX - centerX, mouseY - guiTop);
		}
	}


}
